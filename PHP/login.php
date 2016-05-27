<?php
	require_once("functions/globals.php");

	$username = $_POST["username"];
	$password = $_POST["password"];	

	// escape characters
	$username = addslashes($username);
	$password = addslashes($password);
	
	$conn = openConnection();
	$sql = "call DBCheckLoginHashed('$username', '$password')";
	$result = $conn->query($sql);
	
	$response = array();

	while ($row = $result->fetch_assoc()) {
		array_push($response, $row); 
	}

	echo json_encode(array('server_response'=>$response));
	
?>