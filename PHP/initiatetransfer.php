<?php
	require_once("functions/globals.php");

	$var_PID       = isset($_POST['PID'])? $_POST['PID'] : -1;
   $var_ownUID    = isset($_POST['ownUID'])? $_POST['ownUID'] : -1;
   $var_targetUID = isset($_POST['targetUID'])? $_POST['targetUID'] : -1;
   $var_deadline  = isset($_POST['deadline'])? $_POST['deadline'] : '0000-00-00 23:59:59';

	// escape characters
	$var_PID = addslashes($var_PID);
   $var_ownUID = addslashes($var_ownUID);
   $var_targetUID = addslashes($var_targetUID);
   $var_deadline = addslashes($var_deadline);
	
	$conn = openConnection();
	$sql = "call initiateTransfer($var_PID, $var_ownUID, $var_targetUID, '$var_deadline')";
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