<?php
	require_once("functions/globals.php");

	$var_ownUID     = isset($_POST['ownUID'])? $_POST["ownUID"] : -1;
	$var_partnerUID = isset($_POST['partnerUID'])? $_POST["partnerUID"] : -1;

	// escape characters
	$var_ownUID = addslashes($var_ownUID);
	$var_partnerUID = addslashes($var_partnerUID);
	
	$conn = openConnection();
	$sql = "call cancelRequest($var_ownUID, $var_partnerUID)";
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