-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan pengolahan / pengambilan profil users.
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================

delimiter ;;
use Pinjemin;;


-- ==============================================================================
-- Mendapatkan entri suatu user di database berdasarkan UID-nya
-- ------------------------------------------------------------------------------
-- @param: varUID - UID user yang ingin didapatkan informasinya
-- @returns: satu record mengenai user yang dicari.
-- ==============================================================================
drop procedure if exists getUserWithUID;;
create procedure getUserWithUID(in varUID int)
	reads sql data
begin
	select * from user usr
	where usr.UID = varUID;
end;;

-- ==============================================================================
-- Mendapatkan entri suatu user di database berdasarkan SSO account name-nya
-- ------------------------------------------------------------------------------
-- @param: varAccName - SSO account name user yang ingin didapatkan informasinya
-- @returns: satu record mengenai user yang dicari.
-- ==============================================================================
drop procedure if exists getUserWithAccountName;;
create procedure getUserWithAccountName(in varAccName varchar(60))
	reads sql data
begin
	select * from user usr
	where usr.AccountName = varAccName;
end;;

-- ==============================================================================
-- Membuat entri user baru di database Pinjemin
-- USAGE: panggil saat memroses login, ternyata itu login yang pertama (?)
-- JANGAN LUPA UNTUK MENG-ESCAPE MASUKAN DARI USER!!!
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varRealName - nama asli (e.g. Ferdinand Antonius)
-- @param: varBio - profil user (e.g. "suka makan keju bakar", nullable)
-- @param: varFakultas - nama fakultas user (nullable)
-- @param: varProdi - nama prodi user (nullable)
-- @param: varTelepon - nomor telepon user (nullable)
-- @returns: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists createNewUser;;
create procedure createNewUser(
	in varAccountName varchar(60),
	in varRealName varchar(80),
	in varBio varchar(240),
	in varFakultas varchar(60),
	in varProdi varchar(60),
	in varTelepon varchar(15)
)
	modifies sql data
begin
	-- error handler
	declare exit handler for sqlexception
	select false;
	-- masukkan data baru
	insert into user (AccountName, RealName, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating)
	values (varAccountName, varRealName, varBio, varFakultas, varProdi, varTelepon, 0, 0);
	-- operasi berhasil
	select true;
end;;

delimiter ;
get procedure status;