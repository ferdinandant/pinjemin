-- ====================================================================
-- Buat ngapus prosedur
-- "if exist" biar nggak error kalau nggak ada
-- ====================================================================
drop procedure if exists namaProsedur;;


-- ====================================================================
-- Buat bikin prosedur
-- ====================================================================

create procedure namaProsedur(in param1 int, in param2 int)
   -- ============================================================
   -- CHARACTERISTIC:
   -- untuk optimisasi DBMS
   -- ============================================================ 
   [reads|modifies] sql data

begin
   -- ============================================================
   -- DEKLARASI:
   -- ============================================================
   -- Harus berurut dari atas ke bawah:
   -- (1) Variables: untuk nyimpen data
   -- (2) Cursors: row iterator dari suatu perintah select
   -- (3) Handlers: semacam "try-catch"
   -- ============================================================

   -- ------------------------------------------------------------
   -- deklarasi variables
   -- DECLARE [namanya] [tipenya] default [nilaiAwal]
   -- ------------------------------------------------------------
   declare var1 int default 0;
   declare var2 int default -1;
   declare isDone tinyint(1) default 0;

   -- ------------------------------------------------------------
   -- assign ke variabel
   -- SET [namanya] = [nilainya]
   -- ------------------------------------------------------------
   set isDone = 0;

   -- ------------------------------------------------------------
   -- deklarasi cursor
   -- DECLARE [namanya] CURSOR FOR [selectStatement]
   -- ------------------------------------------------------------
   declare myCursor cursor for
      select * from mytable;
   
   -- ------------------------------------------------------------
   -- deklarasi handler
   -- DECLARE [continue|exit] HANDLER FOR [condition] [commands];
   -- ------------------------------------------------------------
   -- [condition] bisa berupa:
   -- (1) mysql_error_code
   -- (2) SQLSTATE [VALUE] sqlstate_value
   -- (3) condition_name
   -- (4) SQLWARNING
   -- (5) NOT FOUND
   -- (6) SQLEXCEPTION
   -- ------------------------------------------------------------
   -- [commands] kalau banyak:
   -- group pakai "begin ... end"
   -- ------------------------------------------------------------
   declare continue handler for not found
      set isDone = 1;
   


   -- ============================================================
   -- CONTROL FLOW
   -- Technically, setelah deklarasi, masukkan perintah apa pun.
   -- ============================================================

   -- ------------------------------------------------------------
   -- bentuk if
   -- ------------------------------------------------------------
   if isDone = 1 then
      select "yeaay";
      select "noooo";
   elseif isDone = 2 then
      select "yeaay";
      select "noooo";
   else
      select "yeaaay";
   endif;

   -- ------------------------------------------------------------
   -- bentuk while-do
   -- ------------------------------------------------------------
   while x <= 5 do
      set str = concat(str,x,',');
      set x = x + 1; 
   end while;

   -- ------------------------------------------------------------
   -- bentuk repeat-until
   -- ------------------------------------------------------------
   repeat
      set str = CONCAT(str,x,',');
      set x = x + 1; 
   until x > 5
   end repeat;
 

   -- ============================================================
   -- PEMAKAIAN CURSOR
   -- open/close buat buka/tutup cursor
   -- fetch buat ambil row berikutnya
   -- ============================================================
   open myCursor;
   
   loopLabel: loop
      -- setiap kolom di-assign ke satu variabel
      -- fetch returns next row, atau not found error
      fetch myCursor into var1, var2, var3, var4;
      
      -- contoh penggunaan if
      if isDone = 1 then
         -- setara dengan "break"
         leave loopLabel;
      elseif isDone = 2 then
         -- setara dengan "continue"
         iterate loopLabel;
      else
         select "yeaaay";
      endif; 
   end loop loopLabel;
 
   close myCursor;

end;;