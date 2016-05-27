<?php
	require_once("functions/globals.php");

	$var_PID        = isset($_POST['PID'])? $_POST["PID"] : -1;
	$var_NamaBarang = isset($_POST['NamaBarang'])? $_POST["NamaBarang"] : "";
	$var_Deskripsi  = isset($_POST['Deskripsi'])? $_POST["Deskripsi"] : "";
	$var_LastNeed   = isset($_POST['LastNeed'])? $_POST["LastNeed"] : "";

	// escape characters
	$var_PID = addslashes($var_PID);
	$var_NamaBarang = addslashes($var_NamaBarang);
	$var_Deskripsi = addslashes($var_Deskripsi);
	$var_LastNeed = addslashes($var_LastNeed);
	
	$conn = openConnection();
	$sql = "call editPermintaan($var_PID, '$var_NamaBarang', '$var_Deskripsi', '$var_LastNeed')";
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