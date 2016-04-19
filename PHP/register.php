<?php
	require_once("functions/globals.php");

	$uid      = isset($_POST['uid'])      ? $_POST["uid"] : -1;
	$realname = isset($_POST['realname']) ? $_POST["realname"] : "";
	$fakultas = isset($_POST['fakultas']) ? $_POST["fakultas"] : "";	
	$prodi    = isset($_POST['prodi'])    ? $_POST["prodi"] : "";	
	$telepon  = isset($_POST['telepon'])  ? $_POST["telepon"] : "";	
	$bio      = isset($_POST['bio'])      ? $_POST["bio"] : "";	
		
	$sql = "update user set realname = '$realname', bio = '$bio', fakultas = '$fakultas', prodi = '$prodi', telepon = '$telepon' where uid = $uid";
		
	/*
	$myfile = fopen("sqlregister.txt", "w"); 
	fwrite($myfile, $sql);
	
	ob_start();
	var_dump($_POST);
	$dumpres = ob_get_clean();
	fwrite($myfile, "\r\n\r\n");
	fwrite($myfile, $dumpres);
	fclose($myfile);
	*/
	
	$conn = openConnection();
	
	$conn->query($sql);	
?>