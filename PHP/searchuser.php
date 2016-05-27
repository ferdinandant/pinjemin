<?php
	require_once("functions/globals.php");

	$query = isset($_POST['query'])? $_POST["query"] : "";
	
	// escape characters
	$query = addslashes($query);
	
	$conn = openConnection();
	$sql = "call searchUser('$query')";
	$result = $conn->query($sql);

	$response = array();

	while ($row = $result->fetch_assoc()) {
		array_push($response, $row); 
	}

	echo json_encode(array('server_response'=>$response));
?>