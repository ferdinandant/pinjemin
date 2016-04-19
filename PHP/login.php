<?php
	require_once("functions/globals.php");

	$username = $_POST["username"];
	$password = $_POST["password"];	

	$sql_query = "call DBCheckLoginHashed('$username', '$password')";

	$result = mysqli_query(openConnection(), $sql_query);

	$response = array();

	while ($row = mysqli_fetch_array($result)) {
		array_push($response,array('uid'=>$row[0],'realname'=>$row[2])); 
	}

	echo json_encode(array('server_response'=>$response));
?>