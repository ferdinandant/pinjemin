<?php
	// link dependencies
	require_once('../functions/globals.php');
?>

<html>
<head>
	<!-- styling starts -->
	<style>
		body {
			background-color: #eee;
			padding: 1em;
			font-family: Arial;
		}
		h4 {
			margin: 0;
			margin-bottom: 4px;
		}
		input {
			margin-top: 10px;
		}
		textarea {
			width: 100%;
			height: 90%;
		}
	</style>
	<!-- styling ends -->
</head>

<body>

<!-- form starts -->
<form action="execute.php" method="post">

<h4>Enter SQL command here:</h4>
<textarea name="command"><?php
	if (issetcook("commandCache")) {
		echo getcook("commandCache");
	}
?></textarea>
<br>

<input type="submit">
<input type="reset">
</form>
<!-- form ends -->

</body>
</html>