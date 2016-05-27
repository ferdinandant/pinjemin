<?php
	require_once("functions/globals.php");

	$page = isset($_POST['page'])? $_POST["page"] : 0;
	
	// escape characters
	$page = addslashes($page);
	
	$conn = openConnection();
	$sql = "call getPenawaranTimeline($page, 999999)";
	$result = $conn->query($sql);

	$response = array();

	while ($row = $result->fetch_assoc()) {
		array_push($response, $row); 
	}

	echo json_encode(array('server_response'=>$response));
?>