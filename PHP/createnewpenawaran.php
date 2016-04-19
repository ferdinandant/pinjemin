<?php
	require_once("functions/globals.php");

	$var_uid        = isset($_POST['uid'])? $_POST["uid"] : -1;
	$var_namabarang = isset($_POST['namaBarang'])? $_POST["namaBarang"] : "";
	$var_deskripsi  = isset($_POST['deskripsi'])? $_POST["deskripsi"] : "";
	$var_harga      = isset($_POST['harga'])? $_POST["harga"] : 0;
	
	// escape characters
	$var_uid = addslashes($var_uid);
	$var_namabarang = addslashes($var_namabarang);
	$var_deskripsi = addslashes($var_deskripsi); 
	$var_harga = addslashes($var_harga);
	
	// send data
	$conn = openConnection();
	$sql = "call createNewPenawaran($var_uid, '$var_namabarang', '$var_deskripsi', $var_harga)";
	$result = $conn->query($sql);
	
	// return value
	$return = "";
	while ($row = $result->fetch_assoc()) {
		if (isset($row["TRUE"])) {
			$return = "true";
		}
		else {
			$return  = "false";
		}
	}
	
	echo $return;
?>