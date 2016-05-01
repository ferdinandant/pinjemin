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
end;;

-- ==============================================================================
-- Mengecek apakah suatu UID bisa melakukan initiate penyerahan barang di PID
-- ------------------------------------------------------------------------------
-- @param: varPID - PID post
-- @param: varUID - UID yang ingin dicoba
-- @param: varParentUID - ParentUID thread
-- @return: 0 jika tidak bisa melakukan apa-apa, 1 jika bisa meng-initiate
--    penyerahan barang, 2 jika bisa meng-confirm penyerahan barang, 3 jika
--    bisa membatalkan initiate penyerahan barang.
-- ==============================================================================
drop procedure if exists getPossibleThreadAction;;
create procedure getPossibleThreadAction(
	in varPID int,
	in varUID int,
	in varParentUID int
)
	reads sql data
ProcBody: begin
	declare postType varchar(10) default 0;
	declare isPembuatPost tinyint(1) default 0;
	declare isSudahKomentar tinyint(1) default 0;
	declare isDiKonfirmasiUID tinyint(1) default 0;
	declare isDiKonfirmasiParentUI tinyint(1) default 0;
	declare isSudahInitiateKeLainnya tinyint(1) default 0;
	
	-- cari ada di mana
	select getPostType(varPID) into postType;
	
	-- apakah dia pembuat post
	select count(*) into postType
	from post pos
	where pos.PID = varPID and pos.UID = varUID;
	
	-- apakah UID terlibat dalam konfirmasi
	select count(*) into isDiKonfirmasiUID
	from konfirmasi kon
	where kon.PID = varPID and kon.UID = varUID;
	
	-- apakah parentUID terlibat dalam konfirmasi
	select count(*) into isDiKonfirmasiParentUI
	from konfirmasi kon
	where kon.PID = varPID and kon.UID = varParentUID;
	
	if postType = 'Permintaan' then
		-- untuk post penawaran, yang bisa initiate: pembaca post
		if isPembuatPost > 0 then
			-- kalau sudah ada initiate, bisa confirm
			-- kalau nggak, ya nggak bisa apa-apa
			if isDiKonfirmasiParentUI > 0 then
				select 2;
				leave procBody;
			else
				select 0;
				leave procBody;
			end if;
		else
			-- apakah dia sudah komentar
			select count(*) into isSudahKomentar
			from komentar kom
			where kom.PID = varPID and kom.UID = varUID;
			-- kalau sudah initate, bisa cancel,
			-- kalau belum, bisa initiate
			if isDiKonfirmasiParentUID > 0 and isSudahKomentar > 0 then
				select 3;
				leave procBody;
			elseif isSudahKomentar > 0 then
				select 1;
				leave procBody;
			else
				select 0;
				leave procBody;
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
				select 0;
				leave procBody;
			elseif isDiKonfirmasiParentUI > 0 then
				select 3;
				leave procBody;
			else
				select 1;
				leave procBody;
			end if;
		else
			-- apakah dia sudah komentar
			select count(*) into isSudahKomentar
			from komentar kom
			where kom.PID = varPID and kom.UID = varUID;
		end if;
		
	else
		select 0;
		leave procBody;
	end if;
end;;

delimiter ;