<?php
	require_once("functions/globals.php");

	$var_PID    = isset($_POST['PID'])? $_POST["PID"] : -1;
	$var_Status = isset($_POST['status'])? $_POST["status"] : "";
	$var_Rating = isset($_POST['rating'])? $_POST["rating"] : "0";
	$var_Review = isset($_POST['review'])? $_POST["review"] : "";
   
	// escape characters
	$var_PID = addslashes($var_PID);
	$var_Status = addslashes($var_Status);
	$var_Rating = addslashes($var_Rating);
	$var_Review = addslashes($var_Review);
   
   if ($var_Rating === 0 || $var_Rating === '0') {
      $var_Rating = "null";
   }
	
	$conn = openConnection();
	$sql = "call changePeminjamanStatus($var_PID, '$var_Status', $var_Rating, '$var_Review')";
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