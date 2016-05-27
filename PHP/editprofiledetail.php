<?php
	require_once("functions/globals.php");

	$var_ownUID    = isset($_POST['ownUID'])? $_POST["ownUID"] : -1;
	$var_realname  = isset($_POST['realName'])? $_POST["realName"] : "";
	$var_bio	  	   = isset($_POST['bio'])? $_POST["bio"] : "";
	$var_telepon   = isset($_POST['telepon'])? $_POST["telepon"] : "";

	// escape characters
	$var_ownUID = addslashes($var_ownUID);
	$var_bio = addslashes($var_bio);
	$var_telepon = addslashes($var_telepon);
	
	$conn = openConnection();
	$sql = "call editProfileDetail($var_ownUID, $var_realname, $var_bio, $var_telepon)";
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