<?php
	require_once("functions/globals.php");

	$var_targetUID = isset($_POST['targetUID'])? $_POST["targetUID"] : -1;

	// escape characters
	$var_targetUID = addslashes($var_targetUID);
	
	$conn = openConnection();
	$sql = "call getUserReviews($var_targetUID)";
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