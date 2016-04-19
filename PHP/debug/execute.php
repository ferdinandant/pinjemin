<?php
	include_once("../functions/globals.php");
	setcook("commandCache", $_POST['command']);
?>

<style>
	body {
		padding: 0.5em;
		font-family: Arial;
	}
  pre {
    margin-top: 0;
		background-color: #f5f5f5;
		padding: 0.5em;
		font-size: 8pt;
		border: 1px dashed grey;
	}
	a {
		text-decoration: none;
	}
	a:hover {
		text-decoration: underline;
	}
	td, th {
		font-size: 11pt;
	}
	table {
		margin-bottom: 0.5em;
	}
</style>

<?php
/** **********************************************************************
 * Menjalankan perintah SQL dari ferddy.php
 *********************************************************************** */

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	// print commands
	echo '<b>Received commands:</b> '
	 . '&nbsp;&nbsp;<a href="#bottom"><i>(to bottom?)</i></a>'
	 . '&nbsp;&nbsp;<a href="http://kemalamru.cloudapp.net/ppl/debug/ferddy.php"><i>(go back?)</i></a>';
	echo '<pre>' . $_POST['command'] . '</pre>';
	echo "<br>\n";
	
	// execute commands
	$conn = openConnection();
	$sql = $_POST['command'];
		
	$result = $conn->query($sql);
	
	// print OK status
	if ($result) {
		$numrows = 0;
		
		if ($result === true) {
			// kalau dia tidak mengembalikan hasil apa pun (e.g. insert)
			$numrows = $conn->affected_rows;
		}
		else {
			// jika dia bisa mengembalikan hasil (select query)
			$numrows = $result->num_rows;
			if ($numrows > 0) {
				printDebugTable($result);
			}
		}
		
		// print status
		echo '<a id="bottom"></a><span style="color: green">';
		echo "OK: $numrows rows affected/returned.</span><br>\n";
	}
	else {
		echo '<a id="bottom"></a><span style="color: red">';
		echo $conn->error . "</span><br>\n";
	}
}

else {
	echo "No POST request was detected.";
}

?>