-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan pengolahan / pengambilan profil users.
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================

delimiter ;;


-- DEBUG PURPOSE START
-- Stored procedures ini seharusnya tidak dipakai lagi kalau
-- sudah bisa otentikasi dengan SSO

-- ==============================================================================
-- Membuat entri user baru di database Pinjemin
-- PASSWORD-NYA DI-SUPPLY PLAINTEXT
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varRealName - nama asli (e.g. Ferdinand Antonius)
-- @param: varPassword - password user (plaintext)
-- @param: varBio - profil user (e.g. "suka makan keju bakar", nullable)
-- @param: varFakultas - nama fakultas user (nullable)
-- @param: varProdi - nama prodi user (nullable)
-- @param: varTelepon - nomor telepon user (nullable)
-- @returns: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists DBCreateNewUserHashed;;
create procedure DBCreateNewUserHashed(
   in varAccountName varchar(60),
   in varRealName varchar(80),
   in varPassword varchar(64),
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
   insert into user (AccountName, RealName, Password, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating)
   values (varAccountName, varRealName, MD5(varPassword), varBio, varFakultas, varProdi, varTelepon, 0, 0);
   -- operasi berhasil
   select true;
end;;

-- ==============================================================================
-- Membuat entri user baru di database Pinjemin
-- PASSWORD-NYA DI-SUPPLY HASHED MD5
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varRealName - nama asli (e.g. Ferdinand Antonius)
-- @param: varPassword - password user (sudah di-hash MD5)
-- @param: varBio - profil user (e.g. "suka makan keju bakar", nullable)
-- @param: varFakultas - nama fakultas user (nullable)
-- @param: varProdi - nama prodi user (nullable)
-- @param: varTelepon - nomor telepon user (nullable)
-- @returns: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists DBCreateNewUserUnhashed;;
create procedure DBCreateNewUserUnhashed(
   in varAccountName varchar(60),
   in varRealName varchar(80),
   in varPassword varchar(64),
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
   insert into user (AccountName, RealName, Password, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating)
   values (varAccountName, varRealName, varPassword, varBio, varFakultas, varProdi, varTelepon, 0, 0);
   -- operasi berhasil
   select true;
end;;

-- ==============================================================================
-- Mengambil data user dengan AccountName dan Password tertentu
-- PASSWORD-NYA DI-SUPPLY PLAINTEXT
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varPassword - password user (plaintext)
-- @returns: datanya jika login berhasil, empty set jika slogin gagal
-- ==============================================================================
drop procedure if exists DBCheckLoginHashed;;
create procedure DBCheckLoginHashed(
   in varAccountName varchar(60),
   in varPassword varchar(64)
)
   reads sql data
begin
   -- mencari di database'
   select *
   from user usr
   where AccountName = varAccountName and Password = MD5(varPassword)
   limit 1;
end;;

-- ==============================================================================
-- Mengambil data user dengan AccountName dan Password tertentu
-- PASSWORD-NYA DI-SUPPLY HASHED MD5
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varPassword - password user (hashed)
-- @returns: datanya jika login berhasil, empty set jika slogin gagal
-- ==============================================================================
drop procedure if exists DBCheckLoginUnhashed;;
create procedure DBCheckLoginUnhashed(
   in varAccountName varchar(60),
   in varPassword varchar(64)
)
   reads sql data
begin
   -- mencari di database'
   select *
   from user usr
   where AccountName = varAccountName and Password = varPassword
   limit 1;
end;;

-- DEBUG PURPOSE END

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
   select UID, AccountName, RealName, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating
   from user usr
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

-- ==============================================================================
-- STORED PROCEDURES FOR PINJEMIN PROJECT (PPL B-1)
-- Terkait dengan pengolahan / pengambilan profil users.
-- ------------------------------------------------------------------------------
-- @author Ferdinand Antonius
-- @version 1.0
-- ==============================================================================

delimiter ;;


-- DEBUG PURPOSE START
-- Stored procedures ini seharusnya tidak dipakai lagi kalau
-- sudah bisa otentikasi dengan SSO

-- ==============================================================================
-- Membuat entri user baru di database Pinjemin
-- PASSWORD-NYA DI-SUPPLY PLAINTEXT
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varRealName - nama asli (e.g. Ferdinand Antonius)
-- @param: varPassword - password user (plaintext)
-- @param: varBio - profil user (e.g. "suka makan keju bakar", nullable)
-- @param: varFakultas - nama fakultas user (nullable)
-- @param: varProdi - nama prodi user (nullable)
-- @param: varTelepon - nomor telepon user (nullable)
-- @returns: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists DBCreateNewUserHashed;;
create procedure DBCreateNewUserHashed(
   in varAccountName varchar(60),
   in varRealName varchar(80),
   in varPassword varchar(64),
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
   insert into user (AccountName, RealName, Password, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating)
   values (varAccountName, varRealName, MD5(varPassword), varBio, varFakultas, varProdi, varTelepon, 0, 0);
   -- operasi berhasil
   select true;
end;;

-- ==============================================================================
-- Membuat entri user baru di database Pinjemin
-- PASSWORD-NYA DI-SUPPLY HASHED MD5
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varRealName - nama asli (e.g. Ferdinand Antonius)
-- @param: varPassword - password user (sudah di-hash MD5)
-- @param: varBio - profil user (e.g. "suka makan keju bakar", nullable)
-- @param: varFakultas - nama fakultas user (nullable)
-- @param: varProdi - nama prodi user (nullable)
-- @param: varTelepon - nomor telepon user (nullable)
-- @returns: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists DBCreateNewUserUnhashed;;
create procedure DBCreateNewUserUnhashed(
   in varAccountName varchar(60),
   in varRealName varchar(80),
   in varPassword varchar(64),
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
   insert into user (AccountName, RealName, Password, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating)
   values (varAccountName, varRealName, varPassword, varBio, varFakultas, varProdi, varTelepon, 0, 0);
   -- operasi berhasil
   select true;
end;;

-- ==============================================================================
-- Mengambil data user dengan AccountName dan Password tertentu
-- PASSWORD-NYA DI-SUPPLY PLAINTEXT
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varPassword - password user (plaintext)
-- @returns: datanya jika login berhasil, empty set jika slogin gagal
-- ==============================================================================
drop procedure if exists DBCheckLoginHashed;;
create procedure DBCheckLoginHashed(
   in varAccountName varchar(60),
   in varPassword varchar(64)
)
   reads sql data
begin
   -- mencari di database'
   select *
   from user usr
   where AccountName = varAccountName and Password = MD5(varPassword)
   limit 1;
end;;

-- ==============================================================================
-- Mengambil data user dengan AccountName dan Password tertentu
-- PASSWORD-NYA DI-SUPPLY HASHED MD5
-- ------------------------------------------------------------------------------
-- @param: varAccountName - nama akun sso (e.g. ferdinand.antonius)
-- @param: varPassword - password user (hashed)
-- @returns: datanya jika login berhasil, empty set jika slogin gagal
-- ==============================================================================
drop procedure if exists DBCheckLoginUnhashed;;
create procedure DBCheckLoginUnhashed(
   in varAccountName varchar(60),
   in varPassword varchar(64)
)
   reads sql data
begin
   -- mencari di database'
   select *
   from user usr
   where AccountName = varAccountName and Password = varPassword
   limit 1;
end;;

-- DEBUG PURPOSE END

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
   select UID, AccountName, RealName, Bio, Fakultas, Prodi, Telepon, TotalRating, NumRating
   from user usr
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


-- ===============<<< SPRINT 2 >>>===============

-- ==============================================================================
-- Melihat profile user, baik profile sendiri maupun user lain
-- ------------------------------------------------------------------------------
-- @param: varUID1 - UID kita
-- @param: varUID2 - UID profile yang ingin dilihat
-- @return: Satu row terkait profile suatu user + statusnya apakah teman/tidak
-- ==============================================================================
drop procedure if exists getProfileDetail;;
create procedure getProfileDetail(
   in varUID1 int,
   in varUID2 int
)
   reads sql data
begin
   select
      usr.UID, usr.RealName, usr.AccountName, usr.TotalRating, usr.NumRating,
      usr.Bio, usr.Prodi, usr.Fakultas, usr.Telepon,
      getUserRelationStatus(varUID1,varUID2) as Status
   from user usr
   where usr.uid = varUID2;
end;;

-- ==============================================================================
-- Mengedit Profil pengguna
-- ------------------------------------------------------------------------------
-- @param: varUID - UID kita
-- @return: true jika berhasil, false jika gagal
-- ==============================================================================
drop procedure if exists editProfileDetail;;
create procedure editProfileDetail(
   in varUID int,
   in varPassword varchar(32),
   in varBio varchar(240),
   in varTelepon varchar(15)
)
   modifies sql data
begin
   -- error handler
   declare exit handler for sqlexception
   select false;
   
   update user 
   set Bio=varBio, Telepon=varTelepon 
   where UID=varUID;
   
   select true;
end;;

-- ==============================================================================
-- Mencari user lain berdasarkan nama atau nama account
-- ------------------------------------------------------------------------------
-- @param: varQuery - input 
-- @return: daftar nama-nama yang dicari sesuai input
-- ==============================================================================
drop procedure if exists searchUser;;
create procedure searchUser(in varQuery varchar(80))
   reads sql data
begin
   select usr.UID, usr.AccountName, usr.RealName, usr.Fakultas, usr.Prodi
   from user usr
   where RealName regexp varQuery or AccountName regexp varQuery
   order by RealName;
end;;

-- ==============================================================================
-- Mendapatkan daftar review user
-- ------------------------------------------------------------------------------
-- @param: varQuery - input 
-- @return: daftar nama-nama yang dicari sesuai input
-- ==============================================================================
drop procedure if exists getUserReviews;;
create procedure getUserReviews(in varUID int)
   reads sql data
begin
   select pos.NamaBarang, pem.Rating, pem.Review,
      (select Realname from user where UID = pem.UIDPemberi) as RealName
   from peminjaman pem natural join post pos
   where pem.UIDPenerima = varUID and pem.status = 'DIKEMBALIKAN'
      and ( (rating is not null or rating > 0) or (review is not null and review <> ""));
end;;


delimiter ;