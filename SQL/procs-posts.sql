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
         per.LastNeed, usr.AccountName, postType as Type
      from permintaan per natural join post pos natural join user usr
      where per.pid = varPID
         and varPID not in (select pid from peminjaman pem)
         and per.LastNeed >= now();
   elseif postType = 'Penawaran' then
      select
         pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, 
         pen.Harga, usr.AccountName, postType as Type
      from penawaran pen natural join post pos natural join user usr
      where pen.PID = varPID
         and varPID not in (select PID from peminjaman pem);
   end if;
end;;

-- ==============================================================================
-- Melakukan inisiasi penyerahan barang untuk post PEMINJAMAN
-- di-invoke oleh pembaca (non-pembuat) post peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang ingin memberikan pinjaman
-- @param: deadline - deadline peminjaman barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists initiateTransferPeminjaman;;
create procedure initiateTransferPeminjaman(
   in varPID int,
   in varUID int,
   in varDeadline datetime
)
   modifies sql data
begin
   declare realName varchar(80) default '';
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- tidak boleh sudah jadi peminjaman
   if getPostType(varPID) <> 'Peminjaman' then
      call raise_error;
   end if;
   
   -- masukkan ke tabel konfirmasi
   insert into konfirmasi (PID, UID, Deadline)
   values (varPID, varUID, varDeadline);
   
   -- cari tahu nama yang meng-initiate peminjaman
   select usr.RealName into realName
   from user usr
   where UID = varUID;
   
   -- buat notifikasi sistem
   -- return true/false ditentukan replyThread
   call replyThread(varPID, null, varUID, concat(realName, ' melaporkan penyerahan barang. ',
      'Deadline penyerahan barang ', date_format(now(), '%e %b %Y, jam %H:%i'),
      '. Menunggu konfirmasi penerima barang . . .'));
end;;

-- ==============================================================================
-- Melakukan inisiasi penyerahan barang untuk post PENAWARAN
-- di-invoke oleh pembuat post penawaran
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID target user yang ingin diberikan pinjaman
-- @param: deadline - deadline peminjaman barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists initiateTransferPenawaran;;
create procedure initiateTransferPenawaran(
   in varPID int,
   in varUID int,
   in varDeadline datetime
)
   modifies sql data
begin
   declare realName varchar(80) default '';
   declare initiateCount int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- tidak boleh sudah jadi peminjaman
   if getPostType(varPID) <> 'Penawaran' then
      call raise_error;
   end if;
   
   -- hanya bisa mengirim initiate ke 1 user
   -- untuk PID yang sama
   select count(*) into initiateCount
   from konfirmasi kon
   where kon.PID = varPID and kon.UID = varUID;
   
   -- cari tahu nama yang meng-initiate penyerahan barang
   select usr.RealName into realName
   from user usr natural join post pos
   where pos.PID = varPID;
   
   if initiateCount = 0 then
      -- masukkan ke tabel konfirmasi 
      insert into konfirmasi (PID, UID, Deadline)
      values (varPID, varUID, varDeadline);
      -- buat notifikasi sistem
      -- return true/false ditentukan replyThread
      call replyThread(varPID, null, varUID, concat(realName, ' melaporkan penyerahan barang. ',
         'Deadline penyerahan barang ', date_format(now(), '%e %b %Y, jam %H:%i'),
         '. Menunggu konfirmasi penerima barang . . .'));
   else
      call raise_error;
   end if;
end;;


-- ==============================================================================
-- Mengonfirmasi penyerahan barang untuk post PEMINJAMAN
-- di-invoke oleh pembuat post peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID user yang initiate penyerahannya diterima
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists confirmTransferPeminjaman;;
create procedure confirmTransferPeminjaman(
   in varPID int,
   in varUID int
)
   modifies sql data
begin
   declare realName varchar(80) default '';
   declare deadline datetime;
   
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- tidak boleh sudah jadi peminjaman
   if getPostType(varPID) <> 'Permintaan' then
      call raise_error;
   end if;
   
   -- Ambil tanggal deadline
   select kon.Deadline into deadline
   from konfirmasi kon
   where kon.PID = varPID and kon.UID = varUID;
   
   -- cari tahu nama yang meng-confirm peminjaman
   select usr.RealName into realName
   from user usr natural join post pos
   where pos.PID = varPID;
   
   -- hapus komentar lain
   delete from komentar
   where parentUID <> varUID;
   -- hapus dari tabel konfirmasi
   delete from konfirmasi
   where PID = varPID;
   
   -- masukkan ke tabel peminjaman
   insert into peminjaman (PID, PartnerUID, Deadline, TimestampMulai, Status)
   values (varPID, varUID, deadline, now(), 'MASIH DIPINJAM');
   
   -- buat notifikasi sistem
   -- return true/false ditentukan replyThread
   call replyThread(varPID, null, varUID, concat(realName,
      ' mengonfirmasi penyerahan barang. Jangan lupa mengembalikan barang yang dipinjam, ya!'));
end;;

-- ==============================================================================
-- Mengonfirmasi penyerahan barang untuk post PENAWARAN
-- di-invoke oleh pembaca (non-pembuat) post penawaran
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists confirmTransferPenawaran;;
create procedure confirmTransferPenawaran(
   in varPID int
)
   modifies sql data
begin
   declare varUID int default 0;
   declare realName varchar(80) default '';
   declare deadline datetime;
   
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- tidak boleh sudah jadi peminjaman
   if isPostPeminjaman(varPID) then
      call raise_error;
   end if;
   
   -- Ambil tanggal deadline dan UID
   select kon.Deadline, kon.UID into deadline, varUID
   from konfirmasi kon
   where kon.PID = varPID;
   
   -- cari tahu nama yang meng-confirm peminjaman
   select usr.RealName into realName
   from user usr
   where usr.UID = varUID;
   
   -- hapus komentar lain
   delete from komentar
   where parentUID <> varUID;
   -- hapus dari tabel konfirmasi
   delete from konfirmasi
   where PID = varPID;
   
   -- masukkan ke tabel peminjaman
   insert into peminjaman (PID, PartnerUID, Deadline, TimestampMulai, Status)
   values (varPID, varUID, deadline, now(), 'MASIH DIPINJAM');
   
   -- buat notifikasi sistem
   -- return true/false ditentukan replyThread
   call replyThread(varPID, null, varUID, concat(realName,
      ' mengonfirmasi penyerahan barang. Jangan lupa mengembalikan barang yang dipinjam, ya!'));
end;;


-- ===============<<< SPRINT 2 >>>===============

-- handle kalo resultnya 0
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

delimiter ;