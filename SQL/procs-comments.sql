-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan komentar (user-made dan notifikasi sistem)
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================
delimiter ;;


-- ==============================================================================
-- Membuat komentar baru (specifically: membuka thread).
-- thread = komentar pertama dari seorang user
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang membuka thread baru (tidak mungkin yang membuat post)
-- @param: varContent - isi komentar
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists createThread;;
create procedure createThread(
   in varPID int,
   in varUID int,
   in varContent varchar(240)
)
   modifies sql data
begin
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- judgement apakah bisa dibuat thread-nya
   if canComment(varPID, varUID) then
      insert into komentar (PID, UID, Timestamp, Content, ParentUID)
      values (varPID, varUID, now(), varContent, varUID);
      select true;
   else 
      select false;
   end if;
end;;

-- ==============================================================================
-- Membuat komentar dengan membalas sebuah thread
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang mengomentari
-- @param: varParentUID - Me-refer ke thread yang dibuat oleh user mana
-- @param: varContent - isi komentar
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists replyThread;;
create procedure replyThread(
   in varPID int,
   in varUID int,
   in varParentUID int,
   in varContent varchar(240)
)
   modifies sql data
begin
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- judgement apakah bisa dibuat thread-nya
   if canComment(varPID, varUID) then
      insert into komentar (PID, UID, Timestamp, Content, ParentUID)
      values (varPID, varUID, now(), varContent, varParentUID);
      select true;
   end if;
end;;


-- ==============================================================================
-- Melihat daftar komentar (bisa untuk post, bisa untuk peminjaman)
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin dilihat komentarnya
-- @param: varUID - UID user yang melihat
-- @return: daftar komentar, sorted berdasarkan thread dan timestamp
-- ==============================================================================
drop procedure if exists getThreads;;
create procedure getThreads(
   in varPID int,
   in varUID int
)
   modifies sql data
begin
   declare posterUID int default -1;
   -- error handler
   declare exit handler for sqlexception
   select false;
   -- dapatkan yang membuat post-nya
   select UID into posterUID
   from post pos
   where PID = varPID;
   -- atur privilege: pembuat post bisa melihat semua thread,
   -- yang lain hanya antara dirinya dan si pembuat post
   if posterUID = varUID then
      -- yang melihat pembuat post
      select kom.CID, kom.ParentUID, usr.UID, usr.RealName, kom.Timestamp, kom.Content
      from komentar kom natural left join user usr
      where kom.PID = varPID
      order by kom.ParentUID asc, kom.Timestamp asc;
   else
      -- yang melihat orang lain
      select kom.CID, kom.ParentUID, usr.UID, usr.RealName, kom.Timestamp, kom.Content
      from komentar kom natural left join user usr
      where kom.PID = varPID and kom.ParentUID = varUID
      order by kom.Timestamp asc;
   end if;
   
   -- update tabel last seen (buat baru kalau tidak ada)
   insert into lastseen (UID, PID, LastSeen)
   values (varUID, varPID, now())
   on duplicate key update LastSeen = now();
end;;


-- ===============<<< SPRINT 2 >>>===============

-- ==============================================================================
-- Memasukan/mengupdate lastseen user yang melihat post dirinya sendiri
-- atau post yang dikomentarinya
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post yang ingin dilihat
-- @param: varUID - UID user yang melihat
-- @return: true jika berhasil mengupdate/memasukan
-- ==============================================================================
drop procedure if exists justSeenPost;;
create procedure justSeenPost(
   in varUID int,
    in varPID int
)
   modifies sql data
begin
   declare posterUID int default -1;
    
   -- error handler
   declare exit handler for sqlexception
   select false;
    
   -- dapatkan yang membuat post-nya
   select UID into posterUID
   from post pos
   where PID = varPID;
    
   if posterUID = varUID then
      -- yang melihat pembuat post
      -- cek ada di tabel Lastseen ngga
        -- kalo ada, update value
        -- kalo ngga ada, insert value
        if isInLastseen(varUID, varPID) then
            update lastseen 
            set Lastseen=now() 
            where UID=varUID and PID=varPID;
            select true;
        else
            insert into lastseen values (varUID, varPID, now());
            select true;
        end if;
   else
      -- yang melihat orang lain, cek ada di table Komentar ngga
        -- kalo ada, lastseen-nya update
      if isInKomentar(varUID, varPID) then
            -- cek ada ngga di tabel Lastseen
            -- kalo ada, update value-nya
            -- kalo ngga ada, insert value
            if isInLastseen(varUID, varPID) then
                update lastseen 
                set Lastseen=now() 
                where UID=varUID and PID=varPID;
                select true;
            else
                insert into lastseen values (varUID, varPID, now());
                select true;
            end if;
        end if;
   end if;
end;;

-- ==============================================================================
-- Mendapatkan jumlah notifikasi (jumlah komentar) setelah terakhir kali
-- melihat post
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post/peminjaman yang dikomentari
-- @param: varUID - UID yang mengomentari
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists getUpdateNotif;;
create procedure getUpdateNotif(
   in varPID int,
   in varUID int
)
   reads sql data
begin
    declare lastTimeChecking timestamp;
    
   -- error handler
   declare exit handler for sqlexception
   select false;
    
   -- liat user itu lastseen-nya kapan di post terkait
    select getLastseenUser(varUID, varPID) into lastTimeChecking;
    
    select count(*) from komentar
    where PID=varPID and Timestamp>lastTimeChecking;
    -- ngeluarin jumlah komen yang muncul setelah lastseen user tersebut pada 
    
end;;


-- ===============<<< SPRINT 3 >>>===============

-- ==============================================================================
-- Menentukan action buttons apa saja yang muncul di setiap thread, untuk
-- user dengan UID varUID. (Secara iteratif, untuk setiap jenis parentUID pada
-- getThreads, method ini memanggil procedure getPossibleThreadAction)
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang mengakses
-- @return: 1 kolom untuk setiap jenis parentPID di thread ini. Kolom yang
--    dikembalikan: ParentUID, ThreadAction (bdsk. getPossibleThreadAction)
-- ==============================================================================
drop procedure if exists getAllPossibleThreadAction;;
create procedure getAllPossibleThreadAction(
   in varPID int,
   in varUID int
)
   reads sql data
begin
     declare posterUID int default -1;
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   -- dapatkan yang membuat post-nya
   select UID into posterUID
   from post pos
   where PID = varPID;
   
   -- atur privilege: pembuat post bisa melihat semua thread,
   -- yang lain hanya antara dirinya dan si pembuat post
   if posterUID = varUID then
      -- yang melihat pembuat post
      select kom.ParentUID, getPossibleThreadAction(varPID, varUID, kom.ParentUID) as ThreadAction
      from komentar kom natural left join user usr
      where kom.PID = varPID
      group by kom.ParentUID
      order by kom.ParentUID asc, kom.Timestamp asc;
   else
      -- yang melihat orang lain
      select kom.ParentUID, getPossibleThreadAction(varPID, varUID, kom.ParentUID) as ThreadAction
      from komentar kom natural left join user usr
      where kom.PID = varPID and kom.ParentUID = varUID
      group by kom.ParentUID
      order by kom.Timestamp asc;
   end if;
end;;



delimiter ;