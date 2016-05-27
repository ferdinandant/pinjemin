<?php
	require_once("functions/globals.php");

	$var_PID     = isset($_POST['PID'])? $_POST["PID"] : -1;
	$var_ownUID  = isset($_POST['ownUID'])? $_POST["ownUID"] : -1;
	$var_parentUID = isset($_POST['parentUID'])? $_POST["parentUID"] : -1;
   $var_content = isset($_POST['content'])? $_POST["content"] : "";
   
	// escape characters
	$var_PID = addslashes($var_PID);
	$var_ownUID = addslashes($var_ownUID);
   $var_parentUID = addslashes($var_parentUID);
	$var_content = addslashes($var_content);
   
	$conn = openConnection();
	$sql = "call replyThread($var_PID, $var_ownUID, $var_parentUID, '$var_content')";
	$result = $conn->query($sql);

	$response = array();

	// return value
	$return = "";
	while ($row = $result->fetch_assoc()) {
		if (isset($row["TRUE"])) {
			$return = "true";
		}
		else {
			$return  = "false";
		}
	}

	echo $return;
	
?>