<?php
/** **********************************************************************
 * Kelas untuk koneksi data ke database
 * -----------------------------------------------------------------------
 * @author Ferdinand Antonius
 *********************************************************************** */
 
//==============================================================================
// Buka koneksi baru ke database server
//------------------------------------------------------------------------------
// @returns: sebuah objek dbconn jika berhasil, false jika error
//==============================================================================
function openConnection() {	
	// provide connection information
	$servername = "localhost";
	$username = "root";
	$password = "adhammiyO2016";
	$conn = new mysqli($servername, $username, $password);
	if (!$conn) {
		return false;
	}
	
	// set used database
	$sql = "use pinjemin";
	$result = $conn->query($sql);
	return $conn;
}

//==============================================================================
// Menutup koneksi ke database server
//------------------------------------------------------------------------------
// @param: conn - objek koneksi db yang digunakan
//==============================================================================
function closeConnection($conn) {
	$conn->close();
}

//==============================================================================
// Mencetak hasil dalam resource dalan sebuah tabel HTML
//------------------------------------------------------------------------------
// @param result - objek resource hasil query
//==============================================================================
function printDebugTable($result) {
	// write header and first row
	$indexKeys = null;
	
	echo "<style>
		table, td, th {
			border: 1px solid black;
			border-collapse: collapse;
	  }
		td, th {
			padding: 5px;
		}
	</style>\n";
	
	if ($row = $result->fetch_assoc()) {
		$indexKeys = array_keys($row);
		echo "<table>";
		
		// print table header
		echo "<tr>";
		foreach ($indexKeys as $key) {
			echo '<th style="background-color: #ddd">' . $key .'</th>';
		}
		echo "</tr>\n";
		
		// print first row
		echo "<tr>";
		foreach ($indexKeys as $key) {
			echo "<td>" . $row[$key] . "</td>";
		}
		echo "</tr>\n";
	}
	
	// write the remainders
	while ($row = $result->fetch_assoc()) {
		echo "<tr>";
		foreach ($indexKeys as $key) {
			echo "<td>" . $row[$key] . "</td>";
		}
		echo "</tr>\n";
	}
	
	echo "</table>";
}

?>