<?php
	require_once("functions/globals.php");

	$var_uid        = isset($_POST['uid'])? $_POST["uid"] : -1;
	$var_namabarang = isset($_POST['namaBarang'])? $_POST["namaBarang"] : "";
	$var_deskripsi  = isset($_POST['deskripsi'])? $_POST["deskripsi"] : "";
	$var_lastneed   = isset($_POST['lastNeed'])? $_POST["lastNeed"] : "";
	
	// escape characters
	$var_uid = addslashes($var_uid);
	$var_namabarang = addslashes($var_namabarang);
	$var_deskripsi = addslashes($var_deskripsi); 
	$var_lastneed = addslashes($var_lastneed);
	
	// send data
	$conn = openConnection();
	$sql = "call createNewPermintaan($var_uid, '$var_namabarang', '$var_deskripsi', '$var_lastneed')";
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