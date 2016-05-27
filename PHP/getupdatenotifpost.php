<?php
	require_once("functions/globals.php");

	$var_pid = isset($_POST['pid'])? $_POST["pid"] : -1;
	$var_ownUID = isset($_POST['ownUID'])? $_POST["ownUID"] : -1;
	
	$conn = openConnection();
	$sql = "call getupdatenotifpost($var_pid, $var_ownUID)";
	$result = $conn->query($sql);

	$response = array();

	while ($row = $result->fetch_assoc()) {
		array_push($response, $row); 
	}

	echo json_encode(array('server_response'=>$response));
?>