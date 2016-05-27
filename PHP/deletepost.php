<?php
	require_once("functions/globals.php");

	$var_PID = isset($_POST['PID'])? $_POST['PID'] : -1;

	// escape characters
	$var_PID = addslashes($var_PID);
	
	$conn = openConnection();
	$sql = "call deletePost($var_PID)";
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