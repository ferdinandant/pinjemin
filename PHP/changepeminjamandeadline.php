<?php
	require_once("functions/globals.php");

	$var_PID      = isset($_POST['PID'])? $_POST["PID"] : 96;
   $var_Deadline = isset($_POST['deadline'])? $_POST["deadline"] : "2000-02-02 20:00:00";

	// escape characters
	$var_PID = addslashes($var_PID);
	$var_Deadline = addslashes($var_Deadline);

	$conn = openConnection();
	$sql = "call changePeminjamanDeadline($var_PID, '$var_Deadline')";
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