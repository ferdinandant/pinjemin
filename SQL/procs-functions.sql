-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Fungsi-fungsi pembantu (BUKAN BAGIAN DARI INTERFACE YANG DITAWARKAN!)
-- Jika terjadi error / return false, kirimkan query: "show errors"
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================

delimiter ;;

-- ==============================================================================
-- Mengecek apakah UID yang diberikan bisa mengomentari suatu PID post/peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang mengomentari
-- @returns: true jika bisa dikomentari, false jika tidak
-- ==============================================================================
drop function if exists canComment;;
create function canComment(varPID int, varUID int)
   returns tinyint(1)
   reads sql data
begin
   declare peminjamanCount int default 0;
   -- error handler
   declare exit handler for sqlexception
   return false;
   -- peminjamanCount bernilai 0 (artinya boleh dikomentari) jika:
   -- [1] post ini belum jadi peminjaman (tidak ada di peminjaman), ATAU
   -- [2] post ini sudah jadi peminjaman, tapi deal-nya dengan user ini, ATAU
   -- [3] yang mengomentari UID-nya 0 (sistem)
   if (varUID <> 0) then
      select count(*) into peminjamanCount
      from peminjaman pem
      where PID = varPID and PartnerUID <> varUID;
   end if;
   -- asalkan peminjamanCount = 0, maka bisa.
   if peminjamanCount = 0 then
      return true;
   else
      return false;
   end if;
end;;

-- ==============================================================================
-- Mengecek apakah suatu post sudah menjadi peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang mengomentari
-- @returns: true jika sudah menjadi peminjaman, false jika tidak.
-- ==============================================================================
drop function if exists isPostPeminjaman;;
create function isPostPeminjaman(varPID int)
   returns tinyint(1)
   reads sql data
begin
   declare peminjamanCount int default 0;
   -- error handler
   declare exit handler for sqlexception
   return false;
   -- cek apakah ada di tabel peminjaman
   select count(*) into peminjamanCount
   from peminjaman pem
   where PID = varPID;
   -- kalau ada, return true
   if peminjamanCount > 0 then
      return true;
   else
      return false;
   end if;
end;;

-- ==============================================================================
-- Mengecek tipe post: 'Permintaan' atau 'Penawaran' atau 'Null'
-- (Tidak ada, atau sudah jadi peminjaman)
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin dicek tipenya
-- @returns: 'Permintaan', 'Penawaran', atau 'Null'
-- ==============================================================================
drop function if exists getPostType;;
create function getPostType(varPID int)
   returns varchar(10)
   reads sql data
begin
   -- declare variable boolean
   declare inPermintaan tinyint(1) default 0;
   declare inPenawaran tinyint(1) default 0;
   
   -- cek di permintaan
   select count(*) into inPermintaan
   from permintaan per natural join post pos
   where per.pid = varPID
      and varPID not in (select pid from peminjaman pem)
      and per.LastNeed >= now();
      
   -- cek di penawaran
   select count(*) into inPenawaran
   from penawaran pen natural join post pos
   where pen.PID = varPID
      and varPID not in (select PID from peminjaman pem);
      
   -- handling kalau sudah tidak ada, atau kalau sudah jadi peminjaman
   -- tapi kalau ada, fetch/join dari tabel yang sesuai
   if inPermintaan > 0 then
      return 'Permintaan';
   elseif inPenawaran > 0 then
      return 'Penawaran';
   else
      return 'Null';
   end if;
end;;


-- ===============<<< SPRINT 2 >>>===============

-- ==============================================================================
-- Mengetahui hubungan dua buah user (temenan, engga, dll)
-- ------------------------------------------------------------------------------
-- @param: varUID1 - UID kita
-- @param: varUID2 - UID user yang ingin diketahui statusnya terhadap kita
-- @returns:
--   'OwnProfile' kalo user yang mau diketahui ternyata kita sendiri
--   'Friends' kalo user yang ingin diketahui ternyata teman kita
--   'NotFriends' kalo nggak temenan
--   'Requesting' kalo user yang ingin diketahui sudah di-add, tapi belom di-accept
--   'Requested' kalau user yang ingin diketahui sudah nge-add, tapi belum kita accept
-- ==============================================================================
drop function if exists getUserRelationStatus;;
create function getUserRelationStatus(varUID1 int, varUID2 int)
   returns varchar(20)
   reads sql data
begin
   declare counter1 int default 0;
   declare counter2 int default 0;
   
   declare exit handler for sqlexception
   return false;
    
    if varUID1 = varUID2 then
        return 'OwnProfile';
    end if;
   
    -- cek profil yang diliat itu ada di daftar temen kita apa engga
    -- kalo ada, cek kita ada di daftar temennya apa engga
    -- kalo kita ngga ada di daftar temennya, berarti kita udah add dia tp blm temenan
    -- kalo kita ada di daftar temennya, berarti kita udah temenan
   select count(*) into counter1
   from friend fr
   where fr.UID1 = varUID1 and fr.UID2 = varUID2;
   
    -- cek user yang diliat temenan ngga sama kita
    -- kalo ada di tabel temen, berarti dia temenan
   select count(*) into counter2
   from friend fr
   where fr.UID1 = varUID2 and fr.UID2 = varUID1;
   
   -- kalau ada, return true
   if counter1 > 0 && counter2 > 0 then
      return 'Friends';
   elseif counter1>0 && counter2=0 then
      return 'Requesting';
   elseif counter1=0 && counter2>0 then
        return 'Requested';
    else
      return 'NotFriends';
   end if;
end;;

-- ==============================================================================
-- Mengecek apakah suatu post sudah menjadi peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang mengomentari
-- @returns: true jika sudah menjadi peminjaman, false jika tidak.
-- ==============================================================================
drop function if exists isInKomentar;;
create function isInKomentar(varUID int, varPID int)
   returns tinyint(1)
   reads sql data
begin
   declare counts int default 0;
   -- error handler
   declare exit handler for sqlexception
   return false;
   -- cek apakah ada di tabel peminjaman
   select count(*) into counts
   from komentar
   where PID = varPID and UID = varUID;
   -- kalau ada, return true
   if counts > 0 then
      return true;
   else
      return false;
   end if;
end;;

-- ==============================================================================
-- Mengecek apakah user ada di dalam tabel lastseen
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang berkaitan
-- @param: varUID - UID user yang berkaitan
-- @returns: true jika uid dan pid sudah ada di dalam tabel lastseen
-- ==============================================================================
drop function if exists isInLastseen;;
create function isInLastseen(varUID int, varPID int)
   returns tinyint(1)
   reads sql data
begin
   declare counts int default 0;
   -- error handler
   declare exit handler for sqlexception
   return false;
   -- cek apakah ada di tabel peminjaman
   select count(*) into counts
   from lastseen
   where PID = varPID and UID = varUID;
   -- kalau ada, return true
   if counts > 0 then
      return true;
   else
      return false;
   end if;
end;;

-- ==============================================================================
-- Mengecek apakah user ada di dalam tabel lastseen
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang berkaitan
-- @param: varUID - UID user yang berkaitan
-- @returns: true jika uid dan pid sudah ada di dalam tabel lastseen
-- ==============================================================================
delimiter ;;
drop function if exists getLastseenUser;;
create function getLastseenUser(varUID int, varPID int)
   returns timestamp
   reads sql data
begin
   declare lastTimeChecking timestamp;
   -- error handler
   declare exit handler for sqlexception
   return false;
   
    select Lastseen into lastTimeChecking from lastseen
    where UID=varUID and PID=varPID;
    
    return lastTimeChecking;
end;;

delimiter ;