<?php
	require_once("functions/globals.php");

	$var_username = "";
	
	if (isset($_POST['username'])) {
		$var_username = $_POST['username'];
	}
	else {
		die("No username parameter");
	}
	
	// escape characters
	$var_username = addslashes($var_username);
	
	$conn = openConnection();
	$sql = "call getUserWithAccountName('$var_username')";
	$result = $conn->query($sql);
	
	if ($result->num_rows >= 1) {
		$response = array();
		while ($row = $result->fetch_assoc()) {
			array_push($response, $row); 
		}
		
		// print return value
		echo json_encode(array('server_response'=>$response));
	}
	else {
		// bikin akun baru		
		$conn = openConnection();
		$sql = "call DBCreateNewUserHashed('$var_username', '', 'pakipaki', '', '', '', '')";
		$result = $conn->query($sql);
				
		// get user info
		$conn = openConnection();
		$sql = "call getUserWithAccountName('$var_username')";
		$result = $conn->query($sql);
		
		$response = array();
		while ($row = $result->fetch_assoc()) {
			array_push($response, $row); 
		}
		
		// print return value
		echo json_encode(array('server_response'=>$response));
	}
	
	

?>

