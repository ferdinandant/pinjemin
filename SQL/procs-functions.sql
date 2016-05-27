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
   declare postType varchar(10) default 0;
   declare isInvolvedInPeminjaman int default 0;
   declare exit handler for sqlexception
      return false;
   
   -- varUID = 0 berarti system notification
   if (varUID = 0) then
      return true;
   end if;
   
   select getPostType(varPID) into postType;
      
   if postType = 'Peminjaman' then
      -- hanya bisa komentar peminjamna kalau terlibat di dalamnya
      select count(*) into isInvolvedInPeminjaman
      from peminjaman pem
      where UIDPemberi = varPID or UIDPenerima = varUID;
      
      if isInvolvedInPeminjaman > 0 then
         return true;
      else
         return false;
      end if;
   elseif postType = 'Null' then
      -- post sudah tidak ada
      return false;
   else
      return true;
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
   declare inPermintaan tinyint(1) default 0;
   declare inPenawaran tinyint(1) default 0;
   declare inPeminjaman tinyint(1) default 0;
   
   select count(*) into inPermintaan
   from permintaan per natural join post pos
   where per.pid = varPID
      and per.LastNeed >= now();
      
   select count(*) into inPenawaran
   from penawaran pen natural join post pos
   where pen.PID = varPID;
   
   select count(*) into inPeminjaman
   from peminjaman pem
   where pem.PID = varPID;
      
   if inPermintaan > 0 then
      return 'Permintaan';
   elseif inPenawaran > 0 then
      return 'Penawaran';
   elseif inPeminjaman > 0 then
      return 'Peminjaman';
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
   declare isFriend tinyint(1) default 0;
   declare isRequesting tinyint(1) default 0;
   declare isRequested tinyint(1) default 0;
   declare exit handler for sqlexception
      return false;
    
   if varUID1 = varUID2 then
      return 'OwnProfile';
   end if;

   select count(*) into isFriend
      from friend fr
      where fr.UID1 = varUID1 and fr.UID2 = varUID2;
   if (isFriend > 0) then
      return 'Friends';
   end if;
   
   select count(*) into isRequesting
      from adds ad
      where ad.UIDAdder = varUID1 and ad.UIDAdded = varUID2;
   if (isRequesting > 0) then
      return 'Requesting';
   end if;
   
   select count(*) into isRequested
      from adds ad
      where ad.UIDAdder = varUID2 and ad.UIDAdded = varUID1;
   if (isRequested > 0) then
      return 'Requested';
   end if;
      
   return 'NotFriends';
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


-- ===============<<< SPRINT 3 >>>===============

-- ==============================================================================
-- Untuk menentukan action buttons apa yang muncul di suatu thread
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang ingin dicoba
-- @param: varParentUID - ParentUID thread
-- @return: 0 jika tidak bisa melakukan apa-apa, 1 jika bisa meng-initiate
--    penyerahan barang, 2 jika bisa meng-confirm penyerahan barang, 3 jika
--    bisa membatalkan initiate penyerahan barang.
-- ==============================================================================
drop function if exists getPossibleThreadAction;;
create function getPossibleThreadAction(varPID int, varUID int, varParentUID int)
   returns int
   reads sql data
begin
   declare postType varchar(10) default 0;
   declare isPembuatPost tinyint(1) default 0;
   declare isSudahKomentar tinyint(1) default 0;
   declare isDiKonfirmasiUID tinyint(1) default 0;
   declare isDiKonfirmasiParentUID tinyint(1) default 0;
   declare isSudahInitiateKeLainnya tinyint(1) default 0;
   
   -- cari ada di mana (permintaan atau penawaran)
   select getPostType(varPID) into postType;
   
   -- apakah dia pembuat post
   select count(*) into isPembuatPost
   from post pos
   where pos.PID = varPID and pos.UID = varUID;
   
   -- apakah UID terlibat dalam konfirmasi
   select count(*) into isDiKonfirmasiUID
   from konfirmasi kon
   where kon.PID = varPID and kon.UID = varUID;
   
   -- apakah parentUID terlibat dalam konfirmasi
   select count(*) into isDiKonfirmasiParentUID
   from konfirmasi kon
   where kon.PID = varPID and kon.UID = varParentUID;
   
   if postType = 'Permintaan' then
      -- untuk post penawaran, yang bisa initiate: pembaca post
      if isPembuatPost > 0 then
         -- kalau sudah ada initiate, bisa confirm
         -- kalau nggak, ya nggak bisa apa-apa
         if isDiKonfirmasiParentUID > 0 then
            return 2;
         else
            return 0;
         end if;
      else
         -- apakah dia sudah komentar
         select count(*) into isSudahKomentar
         from komentar kom
         where kom.PID = varPID and kom.UID = varUID;
         -- kalau sudah initate, bisa cancel,
         -- kalau belum, bisa initiate
         if isDiKonfirmasiParentUID > 0 and isSudahKomentar > 0 then
            return 3;
         elseif isSudahKomentar > 0 then
            return 1;
         else
            return 0;
         end if;
      end if;
   
   elseif postType = 'Penawaran' then
      -- untuk post penawaran, yang bisa initiate: penulis post
      if isPembuatPost > 0 then
         -- cek kalau belum meng-initiate ke yang lainnya
         select count(*) into isSudahInitiateKeLainnya
         from konfirmasi kon
         where kon.PID = varPID and kon.UID <> varParentUID;
         -- kalau sudah initiate ke yg lain, nggak bisa ngapa-ngapain untuk yg ini
         -- kalau sudah initiate untuk ini, maka bisa cancel
         -- lainnya, maka bisa initiate
         if isSudahInitiateKeLainnya > 0 then
            return 0;
         elseif isDiKonfirmasiParentUID > 0 then
            return 3;
         else
            return 1;
         end if;
      else
         -- apakah dia sudah komentar
         select count(*) into isSudahKomentar
         from komentar kom
         where kom.PID = varPID and kom.UID = varUID;
         -- kalau sudah ada initiate, bisa confirm
         -- kalau nggak, ya nggak bisa apa-apa
         if isDiKonfirmasiParentUID > 0 then
            return 2;
         else
            return 0;
         end if;
      end if;
      
   else
      return 0;
   end if;
end;;

-- ==============================================================================
-- Untuk mengecek apakah suatu user pernah mengomentari suatu post
-- ------------------------------------------------------------------------------
-- @param: varUID - UID yang ingin di-test
-- @param: varPID - PID yang ingin di-test
-- @return: 1 apabila user sudah pernah mengomentari post tersebut, 0 otherwise
-- ==============================================================================
drop function if exists userHasCommented;;
create function userHasCommented(varUID int, varPID int)
   returns int
   reads sql data
begin
   declare userHasCommented int default 0;
   
   select exists (select * from komentar where UID = varUID and PID = varPID)
   into userHasCommented;
   
   return userHasCommented;
end;;

-- ==============================================================================
-- Untuk mengecek apakah suatu user pernah mengomentari suatu post
-- ------------------------------------------------------------------------------
-- @param: varUID - UID yang ingin di-test
-- @param: varPID - PID yang ingin di-test
-- @return: 1 apabila user sudah pernah mengomentari post tersebut, 0 otherwise
-- ==============================================================================
drop function if exists userHasCommented;;
create function userHasCommented(varUID int, varPID int)
   returns int
   reads sql data
begin
   declare userHasCommented int default 0;
   
   select exists (select * from komentar where UID = varUID and PID = varPID)
   into userHasCommented;
   
   return userHasCommented;
end;;

-- ==============================================================================
-- Mendapatkan jumlah post yang belum dibaca oleh user pada suatu post/peminjaman
-- ------------------------------------------------------------------------------
-- @param: varUID - UID yang ingin di-test
-- @param: varPID - PID yang ingin di-test
-- @param: varAuthorUID - UID yang membuat post tersebut (sengaja di-denormalisasi
--   agar tidak perlu mengecek lagi apakah varUID adalah pembuat post).
-- @return: jumlah post yang belum dibaca oleh user pada suatu post/peminjaman
-- ==============================================================================
drop function if exists getUnreadCount;;
create function getUnreadCount(varUID int, varPID int, varAuthorUID int)
   returns int
   reads sql data
begin
   declare unreadComments int default 0;
   declare lastSeenTimestamp datetime default '0000-00-00 00:00:00';
      
   select las.LastSeen into lastSeenTimestamp
   from lastseen las
   where las.UID = varUID and las.PID = varPID;
   
   if varUID = varAuthorUID then
      -- pembuat post bisa perlu menghitung semuanya
      select count(*) into unreadComments
      from komentar kom
      where kom.PID = varPID
         and kom.Timestamp > lastSeenTimestamp;

      return unreadComments;
   else
      -- pembaca post hanya perlu cek yang parent UID-nya dia
      select count(*) into unreadComments
      from komentar kom
      where kom.PID = varPID
         and kom.parentUID = varUID
         and kom.Timestamp > lastSeenTimestamp;
         
      return unreadComments;
   end if;     
end;;

delimiter ;