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
      select ad.addee
      from adds ad
      where ad.added = varUID
   )
   order by usr.RealName;
end;;