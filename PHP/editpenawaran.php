<?php
	require_once("functions/globals.php");

	$var_PID        = isset($_POST['PID'])? $_POST["PID"] : -1;
	$var_NamaBarang = isset($_POST['NamaBarang'])? $_POST["NamaBarang"] : "";
	$var_Deskripsi  = isset($_POST['Deskripsi'])? $_POST["Deskripsi"] : "";
	$var_Harga      = isset($_POST['Harga'])? $_POST["Harga"] : 0;

	// escape characters
	$var_PID = addslashes($var_PID);
	$var_NamaBarang = addslashes($var_NamaBarang);
	$var_Deskripsi = addslashes($var_Deskripsi);
	$var_Harga = addslashes($var_Harga);
	
	$conn = openConnection();
	$sql = "call editPenawaran($var_PID, '$var_NamaBarang', '$var_Deskripsi', '$var_Harga')";
	$result = $conn->query($sql);

	$response = array();

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