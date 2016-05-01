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

delimiter ;