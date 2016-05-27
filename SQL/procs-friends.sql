-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan pertemanan
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================


-- ===============<<< SPRINT 2 >>>===============

-- ==============================================================================
-- Mendapatkan daftar teman dari suatu user
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang akan didapatkan daftar temannya 
-- @return: daftar teman yang UID-nya varUID
-- ==============================================================================
drop procedure if exists getFriendList;;
create procedure getFriendList(in varUID int)
   reads sql data
begin
   select usr.UID, usr.AccountName, usr.RealName, usr.Fakultas, usr.Prodi
   from user usr
   where usr.UID in (
      -- dapatkan semua teman varUID
      -- Cara baca tabel friend: UID1 berteman dengan UID2
      select fri.UID2
      from friend fri
      where fri.UID1 = varUID
   )
   order by usr.RealName;
end;;

-- ==============================================================================
-- Mendapatkan request add pertemanan teman dari suatu user
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang akan didapatkan daftar request-nya 
-- @return: daftar request add teman yang UID-nya varUID
-- ==============================================================================
drop procedure if exists getRequestList;;
create procedure getRequestList(in varUID int)
   reads sql data
begin   
   select usr.UID, usr.AccountName, usr.RealName, usr.Fakultas, usr.Prodi
   from user usr
   where usr.UID in (
      -- dapatkan semua UID yang meng-add varUID
      -- (nggak bisa ngasih alias "add" karena keyword)
      select ad.uidadder
      from adds ad
      where ad.uidadded = varUID
   )
   order by usr.RealName;
end;;


-- ===============<<< SPRINT 3 >>>===============

-- ==============================================================================
-- Meng-accept request add pertemanan teman dari suatu user
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang meng-accept friend request
-- @param: varPartnerUID - UID user yang request-nya di-reject
-- @return: true jika operasi berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists acceptRequest;;
create procedure acceptRequest(in varUID int, in varPartnerUID int)
   modifies sql data
begin
   declare isRequestExist int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek apakah ada request yang dimaksud
   select count(*) from adds ad
   where UIDAdder = varPartnerUID and UIDAdded = varUID
   into isRequestExist;
   
   if isRequestExist > 0 then
      -- hapus dari tabel add
      delete from adds where UIDAdder = varPartnerUID and UIDAdded = varUID;
      -- tambahkan ke tabel friend
      insert into friend (UID1, UID2) values
         (varUID, varPartnerUID),
         (varPartnerUID, varUID);
      -- return value
      select true;
   else
      -- return value
      select false;
   end if;
end;;

-- ==============================================================================
-- Me-reject request add pertemanan teman dari suatu user
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang meng-reject friend request
-- @param: varPartnerUID - UID user yang request-nya di-reject
-- @return: true jika operasi berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists rejectRequest;;
create procedure rejectRequest(in varUID int, in varPartnerUID int)
   modifies sql data
begin
   declare isRequestExist int default 0;
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek apakah ada request yang dimaksud
   select count(*) from adds ad
   where UIDAdder = varPartnerUID and UIDAdded = varUID
   into isRequestExist;
   
   if isRequestExist > 0 then
      -- hapus dari tabel add
      delete from adds where UIDAdder = varPartnerUID and UIDAdded = varUID;
      -- return value
      select true;
   else
      -- return value
      select false;
   end if;
end;;

-- ==============================================================================
-- Mengirim request pertemanan kepada user lain
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang mengirim friend request
-- @param: varPartnerUID - UID user yang dikirimi friend request
-- @return: true jika operasi berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists sendRequest;;
create procedure sendRequest(in varUID int, in varPartnerUID int)
   modifies sql data
begin
   declare userRelationStatus varchar(20) default '';
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek hubungan relasi 2 user
   select getUserRelationStatus(varUID, varPartnerUID)
   into userRelationStatus;
   
   -- hanya kalau keduanya tidak berteman
   if (userRelationStatus like 'NotFriends') then
      insert into adds (UIDAdder, UIDAdded) values (varUID, varPartnerUID);
      select true;
   else
      select false;
   end if;
end;;

-- ==============================================================================
-- Membatalkan request pertemanan kepada user lain
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang membatalkan friend request
-- @param: varPartnerUID - UID user yang dibatalkan friend request-nya
-- @return: true jika operasi berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists cancelRequest;;
create procedure cancelRequest(in varUID int, in varPartnerUID int)
   modifies sql data
begin
   declare userRelationStatus varchar(20) default '';
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek hubungan relasi 2 user
   select getUserRelationStatus(varUID, varPartnerUID)
   into userRelationStatus;
   
   if (userRelationStatus like 'Requesting') then
      delete from adds where UIDAdder = varUID and UIDAdded = varPartnerUID;
      select true;
   else
      select false;
   end if;
end;;

-- ==============================================================================
-- Menghapus hubungan pertemanan antara 2 user
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang menghapus hubungan pertemanan
-- @param: varPartnerUID - UID user pasangan yang akan dihapus hubungan pertemanannya
-- @return: true jika operasi berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists removeFriend;;
create procedure removeFriend(in varUID int, in varPartnerUID int)
   modifies sql data
begin
   declare userRelationStatus varchar(20) default '';
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- cek hubungan relasi 2 user
   select getUserRelationStatus(varUID, varPartnerUID)
   into userRelationStatus;
   
   if (userRelationStatus like 'Friends') then
      delete from friend where UID1 = varUID and UID2 = varPartnerUID;
      delete from friend where UID2 = varUID and UID1 = varPartnerUID;
      select true;
   else
      select false;
   end if;
end;;

delimiter ;

