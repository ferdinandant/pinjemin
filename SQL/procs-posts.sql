-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ------------------------------------------------------------------------------
-- [SIGNATURES]
-- >> getUserWithUID(in varUID int)
-- >> getUserWithAccountName(in varAccName varchar(60))
-- >> createNewUser(in varAccountName varchar(60), in varRealName varchar(80),
--      in varBio varchar(240), in varFakultas varchar(60),
--      in varProdi varchar(60), in varTelepon varchar(15))
-- ==============================================================================

-- TODO: REFINE COLUMNS YANG DI-SELECT!!

delimiter ;;
use Pinjemin;;

-- ==============================================================================
-- Mendapatkan semua entri PERMINTAAN di timeline
-- ------------------------------------------------------------------------------
-- @param: varStart - mulai dari row ke berapa (setelah dirutkan)
-- @param: varCount - jumlah maksimal row yang dikembalikan (untuk paging)
-- @returns: tabel semua permintaan di sistem, terurut terbalik berdasarkan
--    timestamp, masih mungkin meng-include yang sudah expired.
-- ==============================================================================
drop procedure if exists getPermintaanTimeline;;
create procedure getPermintaanTimeline(in varStart int, in varCount int)
	reads sql data
begin
	select * from permintaan per natural join post pos
	where per.pid not in (select pid from peminjaman pem)
		and per.LastNeed >= now()
	limit varCount offset varStart;
end;;

-- ==============================================================================
-- Mendapatkan semua entri PENAWARAN di timeline
-- ------------------------------------------------------------------------------
-- @param: varStart - mulai dari row ke berapa (setelah dirutkan)
-- @param: varCount - jumlah maksimal row yang dikembalikan (untuk paging)
-- @returns: tabel semua permintaan di sistem, terurut terbalik berdasarkan
--    timestamp.
-- ==============================================================================
drop procedure if exists getPenawaranTimeline;;
create procedure getPenawaranTimeline(in varStart int, in varCount int)
	reads sql data
begin
	select * from penawaran pen natural join post pos
	where pen.PID not in (select PID from peminjaman pem)
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
-- @param: postType - 'Permintaan' atau 'Penawaran', tipe asli post
-- @return: Satu row terkait detail post dan user yang membuatnya
-- ==============================================================================
drop procedure if exists getPostDetail;;
create procedure getPostDetail(in varPID int, out postType varchar(10))
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
	select * into inPenawaran
	from penawaran pen natural join post pos
	where pen.PID = varPID
		and varPID not in (select PID from peminjaman pem);
		
	-- handling kalau sudah tidak ada, atau kalau sudah jadi peminjaman
	-- tapi kalau ada, fetch/join dari tabel yang sesuai
	if inPermintaan > 0 then
		set postType = 'Permintaan';
		select count(*) into inPermintaan
		from permintaan per natural join post pos natural join user usr
		where per.pid = varPID
			and varPID not in (select pid from peminjaman pem)
			and per.LastNeed >= now();
	elseif inPenawaran > 0 then
		set postType = 'Penawaran';
		select * into inPenawaran
		from penawaran pen natural join post pos natural join user usr
		where pen.PID = varPID
			and varPID not in (select PID from peminjaman pem);
	else
		set postType = 'Null';
	end if;
end;;

delimiter ;
get procedure status;