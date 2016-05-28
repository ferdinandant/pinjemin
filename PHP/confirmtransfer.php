<?php
	require_once("functions/globals.php");

	$var_PID       = isset($_POST['PID'])? $_POST['PID'] : 115;
   $var_ownUID    = isset($_POST['ownUID'])? $_POST['ownUID'] : 30;
   $var_targetUID = isset($_POST['targetUID'])? $_POST['targetUID'] : 31;

	// escape characters
	$var_PID = addslashes($var_PID);
   $var_ownUID = addslashes($var_ownUID);
   $var_targetUID = addslashes($var_targetUID);
	
	$conn = openConnection();
	$sql = "call confirmTransfer($var_PID, $var_ownUID, $var_targetUID)";
	$result = $conn->query($sql);

	// write file
	$file = fopen("log.txt", "a");
	fwrite($file, $sql . "\n");
	fclose($file);
	
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