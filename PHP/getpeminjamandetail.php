<?php
	require_once("functions/globals.php");

	$var_PID = isset($_POST['PID'])? $_POST["PID"] : -1;

	// escape characters
	$var_PID = addslashes($var_PID);
	
	$conn = openConnection();
	$sql = "call getPeminjamanDetail($var_PID)";
	$result = $conn->query($sql);

	$response = array();

	// if there is no error
	if ($result != false) {
		while ($row = $result->fetch_assoc()) {
			array_push($response, $row); 
		}
		
		echo json_encode(array('server_response'=>$response));
	}
	
?>