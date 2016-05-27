-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan entri peminjaman, dan post permintaan/penawaran
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================

delimiter ;;

-- ==============================================================================
-- Membuat post permintaan baru
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang membuat post ini
-- @param: varNamaBarang - nama barang
-- @param: varDeskripsi - deskripsi peminjaman
-- @param: varLastNeed - kapan terakhir dibutuhkan (e.g. 2017-01-01 00:00:00)
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists createNewPermintaan;;
create procedure createNewPermintaan(
   in varUID int,
   in varNamaBarang varchar(60),
   in varDeskripsi varchar(240),
   in varLastNeed datetime
)
   modifies sql data
begin
   declare insertPID int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- masukkan ke post
   insert into post (UID, Timestamp, NamaBarang, Deskripsi)
   values (varUID, now(), varNamaBarang, varDeskripsi);
   set insertPID = last_insert_id();
   -- masukkan ke permintaan
   insert into permintaan (PID, LastNeed)
   values (insertPID, varLastNeed);
   -- return value
   select true;
end;;

-- ==============================================================================
-- Membuat post penawaran baru
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang membuat post ini
-- @param: varNamaBarang - nama barang
-- @param: varDeskripsi - deskripsi peminjaman
-- @param: varHarga - harga
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists createNewPenawaran;;
create procedure createNewPenawaran(
   in varUID int,
   in varNamaBarang varchar(60),
   in varDeskripsi varchar(240),
   in varHarga int
)
   modifies sql data
begin
   declare insertPID int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- masukkan ke post
   insert into post (UID, Timestamp, NamaBarang, Deskripsi)
   values (varUID, now(), varNamaBarang, varDeskripsi);
   set insertPID = last_insert_id();
   -- masukkan ke penawaran
   insert into penawaran (PID, Harga)
   values (insertPID, varHarga);
   -- return value
   select true;
end;;

-- ==============================================================================
-- Mendapatkan semua entri PERMINTAAN di timeline
-- ------------------------------------------------------------------------------
-- @param: varStart - mulai dari row ke berapa (setelah dirutkan, default 0)
-- @param: varCount - jumlah maksimal row yang dikembalikan (untuk paging)
-- @returns: tabel semua permintaan di sistem, terurut terbalik berdasarkan
--    timestamp, masih mungkin meng-include yang sudah expired.
-- ==============================================================================
drop procedure if exists getPermintaanTimeline;;
create procedure getPermintaanTimeline(in varStart int, in varCount int)
   reads sql data
begin
   select pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, 
      per.LastNeed, usr.RealName, usr.AccountName
   from permintaan per natural join post pos natural join user usr
   where per.pid not in (select pid from peminjaman pem)
      and per.LastNeed >= now()
   order by pos.Timestamp desc
   limit varCount offset varStart;
end;;

-- ==============================================================================
-- Mendapatkan semua entri PENAWARAN di timeline
-- ------------------------------------------------------------------------------
-- @param: varStart - mulai dari row ke berapa (setelah dirutkan, default 0)
-- @param: varCount - jumlah maksimal row yang dikembalikan (untuk paging)
-- @returns: tabel semua permintaan di sistem, terurut terbalik berdasarkan
--    timestamp.
-- ==============================================================================
drop procedure if exists getPenawaranTimeline;;
create procedure getPenawaranTimeline(in varStart int, in varCount int)
   reads sql data
begin
   select pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, 
      pen.Harga, usr.RealName, usr.AccountName
   from penawaran pen natural join post pos natural join user usr
   where pen.PID not in (select PID from peminjaman pem)
   order by pos.Timestamp desc
   limit varCount offset varStart;
end;;

-- ==============================================================================
-- Menghapus semua post permintaan yang sudah expired
-- ------------------------------------------------------------------------------
-- @returns: n/a
-- ==============================================================================
drop procedure if exists clearExpiredPermintaan;;
create procedure clearExpiredPermintaan()
   modifies sql data
begin
   -- hapus setiap entri permintaan yang lastneed-nya sudah terlewat
   -- ASUMSI: yang sudah terpenuhi tidak punya lagi nilai lastneed
   delete from post
   where PID in (select PID from permintaan per where per.LastNeed < now());
end;;

-- ==============================================================================
-- Mendapat detail suatu post berdasarkan PID
-- HANYA UNTUK POST YANG BELUM MENJADI PEMINJAMAN
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin diambil informasinya
-- @return: Satu row terkait detail post dan user yang membuatnya, ditambah
--    tipe post di kolom Type
-- ==============================================================================
drop procedure if exists getPostDetail;;
create procedure getPostDetail(in varPID int)
   reads sql data
begin
   declare postType varchar(10) default 0;
   
   -- cari ada di mana
   select getPostType(varPID) into postType;
      
   -- handling kalau sudah tidak ada, atau kalau sudah jadi peminjaman
   -- tapi kalau ada, fetch/join dari tabel yang sesuai
   if postType = 'Permintaan' then
      select
         pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, 
         per.LastNeed, usr.AccountName, usr.RealName, postType as Type
      from permintaan per natural join post pos natural join user usr
      where per.pid = varPID
         and varPID not in (select pid from peminjaman pem)
         and per.LastNeed >= now();
   elseif postType = 'Penawaran' then
      select
         pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, 
         pen.Harga, usr.AccountName, usr.RealName, postType as Type
      from penawaran pen natural join post pos natural join user usr
      where pen.PID = varPID
         and varPID not in (select PID from peminjaman pem);
   end if;
end;;


-- ===============<<< SPRINT 2 >>>===============

-- ==============================================================================
-- Mencari post penawaran berdasarkan nama barang
-- ------------------------------------------------------------------------------
-- @param: varQuery - input 
-- @return: daftar post penawaran sesuai input
-- ==============================================================================
drop procedure if exists searchPenawaran;;
create procedure searchPenawaran (varQuery varchar(80))
   reads sql data
begin
   select pos.PID, pos.NamaBarang, usr.UID, usr.AccountName, usr.RealName, pos.Deskripsi, pen.Harga, pos.Timestamp
   from post pos natural join penawaran pen natural join user usr
   where pos.NamaBarang regexp varQuery or pos.Deskripsi regexp varQuery
   order by pos.Timestamp desc;
end;;

-- ==============================================================================
-- Mencari post permintaan berdasarkan nama barang
-- ------------------------------------------------------------------------------
-- @param: varQuery - input 
-- @return: daftar post permintaan sesuai input
-- ==============================================================================
drop procedure if exists searchPermintaan;;
create procedure searchPermintaan (varQuery varchar(80))
   reads sql data
begin
   select pos.PID, pos.NamaBarang, usr.UID, usr.AccountName, usr.RealName, pos.Deskripsi, per.LastNeed, pos.Timestamp
   from post pos natural join permintaan per natural join user usr
   where pos.NamaBarang regexp varQuery or pos.Deskripsi regexp varQuery
   order by pos.Timestamp desc;
end;;


-- ===============<<< SPRINT 3 >>>===============

-- ==============================================================================
-- Mengubah post permintaan dengan PID tertentu
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin diubah
-- @param: varNamaBarang - nama barang
-- @param: varDeskripsi - deskripsi peminjaman
-- @param: varLastNeed - kapan terakhir dibutuhkan (e.g. 2017-01-01 00:00:00)
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists editPermintaan;;
create procedure editPermintaan(
   in varPID int,
   in varNamaBarang varchar(60),
   in varDeskripsi varchar(240),
   in varLastNeed datetime
)
   modifies sql data
begin
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- update di post
   update post
   set NamaBarang = varNamaBarang, deskripsi = varDeskripsi
   where pid = varPID;
   -- update di permintaan
   update permintaan
   set LastNeed = varLastNeed
   where PID = varPID;
   -- return value
   select true;
end;;

-- ==============================================================================
-- Mengubah post penawaran dengan PID tertentu
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin diubah
-- @param: varNamaBarang - nama barang
-- @param: varDeskripsi - deskripsi peminjaman
-- @param: varLastNeed - kapan terakhir dibutuhkan (e.g. 2017-01-01 00:00:00)
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists editPenawaran;;
create procedure editPenawaran(
   in varPID int,
   in varNamaBarang varchar(60),
   in varDeskripsi varchar(240),
   in varHarga int
)
   modifies sql data
begin
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- update di post
   update post
   set NamaBarang = varNamaBarang, deskripsi = varDeskripsi
   where pid = varPID;
   -- update di permintaan
   update penawaran
   set Harga = varHarga
   where PID = varPID;
   -- return value
   select true;
end;;

-- ==============================================================================
-- Menghapus post tertentu
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin diubah
-- @return true jika ada post yang dihapus (operasi berhasil), false jika tidak
--   (e.g. saat post-nya sudah tidak ada).
-- ==============================================================================
drop procedure if exists deletePost;;
create procedure deletePost(
   in varPID int
)
   modifies sql data
begin
   declare isPostExist int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek apakah ada di tabel post
   select count(*) from post pos where PID = varPID
   into isPostExist;
   
   if isPostExist > 0 then
      -- hapus di tabel post
      -- sudah di-set pada structure: on delete cascade
      delete from post where PID = varPID;
      -- return value
      select true;
   else
      -- return value
      select false;
   end if;
end;;

delimiter ;