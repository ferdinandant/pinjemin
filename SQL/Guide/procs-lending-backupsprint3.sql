-- ==============================================================================
-- Melakukan inisiasi penyerahan barang untuk post PERMINTAAN
-- di-invoke oleh pembaca (non-pembuat) post permintaan
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang ingin memberikan pinjaman
-- @param: deadline - deadline peminjaman barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists initiateTransferPermintaan;;
create procedure initiateTransferPermintaan(
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
