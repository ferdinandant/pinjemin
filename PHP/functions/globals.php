<?php
	session_start();
	
	// link dependencies
	require_once('dbconnect.php');
	
	//==============================================================================
	// Cookie functions
	//==============================================================================
	function issetcook($idx) {
		return (isset($_COOKIE[$idx]) && !empty($_COOKIE[$idx]));
	}
	function unsetcook($idx) {
		setcookie($idx, '', 0, '/');
	}
	function prepcook($idx) {
		if (!issetcook($idx)) {
			setcookie($idx, '', 0, '/');
			$_COOKIE[$idx] = '';
		}
	}
	function setcook($idx, $val) {
		setcookie($idx, $val, 0, '/');
		$_COOKIE[$idx] = $val;
	}
	function getcook($idx) {
		return $_COOKIE[$idx];
	}
?>