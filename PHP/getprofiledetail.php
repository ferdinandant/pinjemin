<?php
	require_once("functions/globals.php");

	$var_ownUID    = isset($_POST['ownUID'])? $_POST["ownUID"] : -1;
	$var_targetUID = isset($_POST['targetUID'])? $_POST["targetUID"] : -1;

	// escape characters
	$var_ownUID = addslashes($var_ownUID);
	$var_targetUID = addslashes($var_targetUID);
	
	$conn = openConnection();
	$sql = "call getProfileDetail($var_ownUID, $var_targetUID)";
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