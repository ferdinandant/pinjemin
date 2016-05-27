<?php
	require_once("functions/globals.php");

	$var_PID       = isset($_POST['PID'])? $_POST['PID'] : -1;
   $var_ownUID    = isset($_POST['ownUID'])? $_POST['ownUID'] : -1;
   $var_targetUID = isset($_POST['targetUID'])? $_POST['targetUID'] : -1;

	// escape characters
	$var_PID = addslashes($var_PID);
   $var_ownUID = addslashes($var_ownUID);
   $var_targetUID = addslashes($var_targetUID);
	
	$conn = openConnection();
	$sql = "call cancelTransfer($var_PID, $var_ownUID, $var_targetUID)";
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