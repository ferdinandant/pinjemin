-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan peminjaman, initiate dan confirm penyerahan barang
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================


-- ===============<<< SPRINT 3 >>>===============

-- ==============================================================================
-- Melakukan inisiasi penyerahan barang
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varOwnUID - UID yang meng-initiate penyerahan barang
-- @param: varTargetUID - UID yang menjadi target initiate barang
-- @param: deadline - deadline peminjaman barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists initiateTransfer;;
create procedure initiateTransfer(
   in varPID int,
   in varOwnUID int,
   in varTargetUID int,
   in varDeadline datetime
)
   modifies sql data
begin
   declare initiatorRealName varchar(80) default '';
   declare postType varchar(10) default 0;
   declare partnerUID int default 0;
   declare isInitiateExist int default 0;
   declare exit handler for sqlexception
      select false;

   select getPostType(varPID) into postType;
   
   if postType = 'Peminjaman' or postType = 'Null' then
      select false;
   else
      select usr.RealName into initiatorRealName
      from user usr
      where UID = varOwnUID;
      
      -- partnerUID adalah UID non-author yang terlibat dalam initiate ini
      -- raise_error juga kalau ternyata meng-intiiate salah
      if postType = 'Permintaan' then
         set partnerUID = varOwnUID;
      elseif postType = 'Penawaran' then
         set partnerUID = varTargetUID;
      end if;
      
      -- tidak boleh sudah ada initiate sebelumnya
      select count(*) into isInitiateExist
      from konfirmasi where PID = varPID and UID = partnerUID;
      
      if isInitiateExist > 0 then
         select false;
      else
         insert into konfirmasi (PID, UID, Deadline)
         values (varPID, partnerUID, varDeadline);
            
         -- buat notifikasi sistem
         -- return true/false ditentukan replyThread
         call replyThread(varPID, null, partnerUID, concat(initiatorRealName, ' melaporkan penyerahan barang. ',
            'Deadline penyerahan barang ', date_format(varDeadline, '%e %b %Y, jam %H:%i'),
            '. Menunggu konfirmasi penerima barang . . .'));
      end if;
   end if;
end;;

-- ==============================================================================
-- Melakukan pembatalan inisiasi penyerahan barang
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang meng-cancel initiate penyerahan barang
-- @param: varTargetUID - UID yang menjadi target cancel initiate barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists cancelTransfer;;
create procedure cancelTransfer(
   in varPID int,
   in varOwnUID int,
   in varTargetUID int
)
   modifies sql data
begin
   declare initiatorRealName varchar(80) default '';
   declare postType varchar(10) default 0;
   declare partnerUID int default 0;
   declare isInitiateExist int default 0;
   declare exit handler for sqlexception
      select false;
   
   select getPostType(varPID) into postType;
   
   if postType = 'Peminjaman' or postType = 'Null' then
      select false;
   else
      select usr.RealName into initiatorRealName
      from user usr
      where UID = varOwnUID;
      
      -- partnerUID adalah UID non-author yang terlibat dalam initiate ini
      -- raise_error juga kalau ternyata meng-intiiate salah
      if postType = 'Permintaan' then
         set partnerUID = varOwnUID;
      elseif postType = 'Penawaran' then
         set partnerUID = varTargetUID;
      end if;
      
      select count(*) into isInitiateExist
      from konfirmasi where PID = varPID and UID = partnerUID;
      
      if isInitiateExist = 0 then
         select false;
      else
         delete from konfirmasi
         where PID = varPID and UID = partnerUID;
            
         -- buat notifikasi sistem
         -- return true/false ditentukan replyThread
         call replyThread(varPID, null, partnerUID, concat(initiatorRealName,
         ' membatalkan laporan penyerahan barang.'));
      end if;
   end if;
end;;

-- ==============================================================================
-- Melakukan konfirmasi inisiasi penyerahan barang
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang meng-confirm penyerahan barang
-- @param: varTargetUID - UID yang menjadi target confirm initiate barang
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists confirmTransfer;;
create procedure confirmTransfer(
   in varPID int,
   in varOwnUID int,
   in varTargetUID int
)
   modifies sql data
begin
   declare initiatorRealName varchar(80) default '';
   declare postType varchar(10) default 0;
   declare partnerUID int default 0;
   declare isInitiateExist int default 0;
   declare UIDPemberi int default 0;
   declare UIDPenerima int default 0;
   declare extractedDeadline datetime;
   declare exit handler for sqlexception
      select false;
   
   select getPostType(varPID) into postType;
   
   if postType = 'Peminjaman' or postType = 'Null' then
      select false;
   else
      set UIDPemberi = varTargetUID;
      set UIDPenerima = varOwnUID;
         
      select usr.RealName into initiatorRealName
      from user usr
      where UID = varOwnUID;
      
      -- partnerUID adalah yang UID-nya ada di kolom UID konfirmasi
      -- untuk permintaan: varTargetUID (initiator: varTargetUID, author: varOwnUID)
      -- untuk penawaran: varOwnUID (initiator: varTargetUID, author: varTargetUID)
      if postType = 'Permintaan' then
         set partnerUID = varTargetUID;
      elseif postType = 'Penawaran' then
         set partnerUID = varOwnUID;
      end if;
      
      select count(*) into isInitiateExist
      from konfirmasi where PID = varPID and UID = partnerUID;
      
      if isInitiateExist = 0 then
         select false;
      else        
         select kon.Deadline into extractedDeadline
         from konfirmasi kon where PID = varPID and UID = partnerUID;
         
         delete from konfirmasi where PID = varPID;
         delete from komentar where PID = varPID and parentUID <> partnerUID;
         -- delete from permintaan where PID = varPID;
         -- delete from penawaran where PID = varPID;
         
         -- buat entri peminjaman
         insert into peminjaman (PID, UIDPemberi, UIDPenerima, Deadline, TimestampMulai, Status)
         values (varPID, UIDPemberi, UIDPenerima, extractedDeadline, now(), 'MASIH DIPINJAM');
                              
         -- buat notifikasi sistem
         -- return true/false ditentukan replyThread
         call replyThread(varPID, null, partnerUID, concat(initiatorRealName,
         ' mengonfirmasi penyerahan barang. Jangan lupa mengembalikan barang yang dipinjam, ya!'));
      end if;
   end if;
end;;

-- ==============================================================================
-- Mendapatkan detail peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID peminjaman
-- @return: data detail peminjaman kalau ada, empty set kalau tidak ada
-- ==============================================================================
drop procedure if exists getPeminjamanDetail;;
create procedure getPeminjamanDetail(in varPID int)
   reads sql data
begin
   select pem.PID, pem.UIDPemberi, pem.UIDPenerima, pem.Deadline, pem.TimestampMulai, pem.TimestampKembali,
      pem.Rating, pem.Review, pem.Status, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, usr.RealName,
         (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPemberi) as RealNamePemberi,
         (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPenerima) as RealNamePenerima
   from peminjaman pem natural join user usr natural join post pos
   where pem.PID = varPID;
end;;


-- =============================================================================='
-- Mendapatkan log barang yang sedang dipinjam
-- ------------------------------------------------------------------------------
-- @param: varPID - UID user yang ingin dilihat log-nya
-- @return: daftar log barang yang sedang dipinjam
-- ==============================================================================
drop procedure if exists getOngoingDipinjamLog;;
create procedure getOngoingDipinjamLog(in varUID int)
   reads sql data
begin
   select pem.PID, pem.Deadline, pem.TimestampMulai, pos.NamaBarang, pos.Deskripsi, usr.RealName,
      (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPemberi) as RealNamePemberi,
      pem.UIDPemberi, getUnreadCount(varUID, pos.PID, pos.UID) as UnreadCount
   from peminjaman pem natural join user usr natural join post pos
   where pem.UIDPenerima = varUID and pem.Status = 'MASIH DIPINJAM'
   order by pem.Deadline asc;
end;;

-- =============================================================================='
-- Mendapatkan log barang yang sedang dipinjamkan
-- ------------------------------------------------------------------------------
-- @param: varPID - UID user yang ingin dilihat log-nya
-- @return: daftar log barang yang sedang dipinjamkan
-- ==============================================================================
drop procedure if exists getOngoingDipinjamkanLog;;
create procedure getOngoingDipinjamkanLog(in varUID int)
   reads sql data
begin
   select pem.PID, pem.Deadline, pem.TimestampMulai, pos.NamaBarang, pos.Deskripsi, usr.RealName,
      (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPenerima) as RealNamePenerima,
      pem.UIDPenerima, getUnreadCount(varUID, pos.PID, pos.UID) as UnreadCount
   from peminjaman pem natural join user usr natural join post pos
   where pem.UIDPemberi = varUID and pem.Status = 'MASIH DIPINJAM'
   order by pem.Deadline asc;
end;;

-- =============================================================================='
-- Mendapatkan log barang yang statusnya masih "menunggu" (post peminjaman dan
-- penawaran yang dubuat sendiri, UNION post peminjaman dan penawaran orang lain
-- yang pernah dikomentari)
-- ------------------------------------------------------------------------------
-- @param: varPID - UID user yang ingin dilihat log-nya
-- @return: daftar log barang yang sedang dipinjamkan
-- ==============================================================================
drop procedure if exists getWaitingLog;;
create procedure getWaitingLog(in varUID int)
   reads sql data
begin
   select 
      pos.PID, pos.UID, pos.Timestamp, pos.NamaBarang, pos.Deskripsi, usr.RealName,
      per.LastNeed, pen.Harga, getUnreadCount(varUID, pos.PID, pos.UID) as UnreadCount
   from post pos
      natural join user usr 
      natural left join permintaan per
      natural left join penawaran pen
   where (pos.UID = varUID or userHasCommented(varUID, pos.PID) > 0)
      and (per.LastNeed is null or per.LastNeed > now())
      and not exists (select * from peminjaman pem where pem.PID = pos.PID)
   order by pos.Timestamp desc;
end;;

-- ==============================================================================
-- Mendapatkan log barang yang statusnya "SUDAH DIKEMBALIKAN" atau "HILANG"
-- ------------------------------------------------------------------------------
-- @param: varPID - UID user yang ingin dilihat log-nya
-- @return: daftar log barang yang sedang dipinjamkan
-- ==============================================================================
drop procedure if exists getExpiredLog;;
create procedure getExpiredLog(in varUID int)
   reads sql data
begin
   select pem.PID, pem.Deadline, pem.TimestampMulai, pem.TimestampKembali, pos.NamaBarang, pos.Deskripsi, usr.RealName,
      (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPemberi) as RealNamePemberi,
      (select usr2.RealName from user usr2 where usr2.UID = pem.UIDPenerima) as RealNamePenerima,
      pem.UIDPemberi, pem.UIDPenerima, pem.Status, getUnreadCount(varUID, pos.PID, pos.UID) as UnreadCount
   from peminjaman pem natural join user usr natural join post pos
   where (pem.UIDPemberi = varUID or pem.UIDPenerima = varUID)
      and (pem.Status = 'HILANG' or pem.Status = 'DIKEMBALIKAN');
end;;

-- ==============================================================================
-- Mengubah status peminjaman
-- ------------------------------------------------------------------------------
-- @param: varPID - PID peminjmana yang ingin diubah statusnya
-- @param: varStatus - 'MASIH DIPINJAM', 'DIKEMBALIKAN', atau 'HILANG'
-- @param: varRating - 1..5 atau null, rating yang diberikan
-- @param: varReview - String, review yang diberikan
-- @return: true jika operasi berhasil, false jika gagal. (NOTE: peminjaman yang
--   statusnya sudah DIKEMBALIKAN, tidak bisa diubah-ubah lagi status/review-nya)
-- ==============================================================================
drop procedure if exists changePeminjamanStatus;;
create procedure changePeminjamanStatus(
   in varPID int,
   in varStatus varchar(60),
   in varRating int(1),
   in varReview varchar(240)
)
   modifies sql data
begin
   declare initiatorRealName varchar(80) default '';
   declare extractedStatus varchar(60) default 'DIKEMBALIKAN';
   declare extractedReview varchar(240) default '';
   declare starRating varchar(20) default '';
   
   declare extractedRating int default null;
   declare extractedUIDPemberi int default -1;
   declare extractedUIDPenerima int default -1;
   declare extractedAuthorUID int default -1;
   declare impliedParentUID int default -1;
   declare dummy int default null;
   
   declare exit handler for sqlexception
      select false;
         
   select pem.Status, pem.Rating, pem.Review,
      pem.UIDPenerima, pem.UIDPemberi
   into extractedStatus, extractedRating, extractedReview,
      extractedUIDPenerima, extractedUIDPemberi
   from peminjaman pem
   where PID = varPID;
   
   -- -------------------
   -- validate parameters
   -- -------------------
      
   -- validate rating
   if (varRating is not null) and (varRating < 1 or varRating > 5) then
      call raise_error;
   end if;

   -- validate varStatus, tidak bisa mengganti status yang sudah dikembalikan
   if (extractedStatus = 'DIKEMBALIKAN') or
      (varStatus != 'DIKEMBALIKAN' and varStatus != 'HILANG' and varStatus != 'MASIH DIPINJAM') then
      call raise_error;
   end if;
   
   -- --------------------------
   -- update peminjaman dan user
   -- --------------------------
   
   -- update di peminjaman
   update peminjaman
   set Status = varStatus, Rating = varRating, Review = varReview
   where PID = varPID;
   
   -- update rating user
   if (extractedRating is null and varRating is null)
      or (extractedRating = varRating) then
      -- no change (ini dummy statement)
      set varRating = varRating;
   elseif (extractedRating is null and varRating is not null) then
      -- new rating
      update user
      set TotalRating = TotalRating + varRating,
         NumRating = NumRating + 1
      where UID = extractedUIDPenerima;
   elseif (extractedRating is not null and varRating is null) then
      -- remove rating
      update user
      set TotalRating = TotalRating - varRating,
         NumRating = NumRating - 1
      where UID = extractedUIDPenerima;
   else
      -- change the rating
      update user
      set TotalRating = TotalRating - extractedRating + varRating
      where UID = extractedUIDPenerima;
   end if;
   
   -- --------------------------
   -- cari tahu impliedParentUID
   -- --------------------------
   
   select pos.UID into extractedAuthorUID
   from post pos where pos.PID = varPID;
   
   -- parent UID = UID yang bukan pembuat post
   if (extractedAuthorUID = extractedUIDPenerima) then
      set impliedParentUID = extractedUIDPemberi;
   else 
      set impliedParentUID = extractedUIDPenerima;
   end if;
   
   -- ---------------
   -- buat notifikasi
   -- ---------------
   
   -- buat notifikasi sistem perubahan status
   if (varStatus != extractedStatus) then
      call replyThread(varPID, null, impliedParentUID,
      concat('Status peminjaman diubah menjadi ', varStatus, '.'));
      
      if varStatus = 'DIKEMBALIKAN' then
         update peminjaman
         set TimestampKembali = now()
         where PID = varPID;
      end if;
   end if;
   
   -- buat notifikasi sistem perubahan rating
   if (varRating != extractedRating or varReview != extractedReview)
   or (extractedReview is null and varReview is not null) 
   or (extractedRating is null and varRating is not null) then
      -- ambil nama yang memberikan review
      select usr.RealName into initiatorRealName
      from user usr where usr.UID = extractedUIDPemberi;
      -- ubah varRating ke bintang:
      case varRating
         when 1 then set starRating = '[1 Bintang] ';
         when 2 then set starRating = '[2 Bintang] ';
         when 3 then set starRating = '[3 Bintang] ';
         when 4 then set starRating = '[4 Bintang] ';
         when 5 then set starRating = '[5 Bintang] ';
         else set starRating = '[Tanpa rating] ';
      end case;
      -- buat notifikasi sistem
      call replyThread(varPID, null, impliedParentUID, concat('Update review dari ', initiatorRealName, ':\n',
         starRating, varReview));
   end if;
   
   select true;
end;;

-- ==============================================================================
-- Mengubah deadline peminjaman barang
-- ------------------------------------------------------------------------------
-- @param: varPID - PID peminjmaan yang ingin diubah statusnya
-- @param: varDeadline - Deadline yang baru
-- @return: true jika operasi berhasil, false jika gagal.
-- ==============================================================================
drop procedure if exists changePeminjamanDeadline;;
create procedure changePeminjamanDeadline(
   in varPID int,
   in varDeadline datetime
)
   modifies sql data
begin
   declare extractedStatus varchar(60) default 'DIKEMBALIKAN';
   declare extractedDeadline datetime default null;
   declare extractedUIDPemberi int default -1;
   declare extractedUIDPenerima int default -1;
   declare extractedAuthorUID int default -1;
   declare impliedParentUID int default -1;
   declare exit handler for sqlexception
      select false;
   
   select pem.Deadline, pem.UIDPemberi, pem.UIDPenerima, pem.Status
   into extractedDeadline, extractedUIDPemberi, extractedUIDPenerima, extractedStatus
   from peminjaman pem where pem.PID = varPID;
   
   if (extractedStatus = 'DIKEMBALIKAN') then
      call raise_error;
   end if;
         
   if (varDeadline <> extractedDeadline) then
      update peminjaman
      set Deadline = varDeadline
      where PID = varPID;
      
      select pos.UID into extractedAuthorUID
      from post pos where pos.PID = varPID;
      
      -- parent UID = UID yang bukan pembuat post
      if (extractedAuthorUID = extractedUIDPenerima) then
         set impliedParentUID = extractedUIDPemberi;
      else 
         set impliedParentUID = extractedUIDPenerima;
      end if;
   
      -- buat notifikasi sistem
      call replyThread(varPID, null, impliedParentUID, concat('Deadline peminjaman diubah menjadi ',
         date_format(varDeadline, '%e %b %Y, jam %H:%i'), '.'));
   end if;
end;;