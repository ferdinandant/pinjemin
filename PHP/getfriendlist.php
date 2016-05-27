<?php
	require_once("functions/globals.php");

	$var_ownUID = isset($_POST['ownUID'])? $_POST['ownUID'] : -1;

	// escape characters
	$var_ownUID = addslashes($var_ownUID);
	
	$conn = openConnection();
	$sql = "call getFriendList($var_ownUID)";
	$result = $conn->query($sql);

	// get return value
	$response = array();

	while ($row = $result->fetch_assoc()) {
		array_push($response, $row); 
	}

	echo json_encode(array('server_response'=>$response));
?>