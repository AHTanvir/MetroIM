<?php

class MySQL
{
	private $dbLink;
	private $dbHost;
	private $dbUsername;
    private $dbPassword;
	private $dbName;
	public  $queryCount;

	function MySQL($dbHost,$dbUsername,$dbPassword,$dbName)
	{
		$this->dbHost = $dbHost;
		$this->dbUsername = $dbUsername;
		$this->dbPassword = $dbPassword;
		$this->dbName = $dbName;
		$this->queryCount = 0;
	}
	function __destruct()
	{
		$this->close();
	}
	//connect to database
	private function connect() {
		$this->dbLink = mysqli_connect($this->dbHost, $this->dbUsername, $this->dbPassword);
		if (!$this->dbLink)	{
			$this->ShowError();
			return false;
		}
		else if (!mysqli_select_db($this->dbLink, $this->dbName))	{
			$this->ShowError();
			return false;
		}
		else {
			mysqli_query($this->dbLink, "set names utf8");
			return true;
		}
		unset ($this->dbHost, $this->dbUsername, $this->dbPassword, $this->dbName);
	}
	/*****************************
	 * Method to close connection *
	 *****************************/
	function close()
	{
		@mysqli_close($this->dbLink);
	}
	/*******************************************
	 * Checks for MySQL Errors
	 * If error exists show it and return false
	 * else return true
	 *******************************************/
	function ShowError()
	{
		$error = mysqli_error($this->dbLink);
		echo $error;
	}
	/****************************
	 * Method to run SQL queries
	 ****************************/
	function  query($sql)
	{
		if (!$this->dbLink)
			$this->connect();

		if (! $result = mysqli_query($this->dbLink, $sql)) {
			$this->ShowError();
			return false;
		}
		$this->queryCount++;
		return $result;
	}
	/************************
	* Method to fetch values*
	*************************/
	function fetchObject($result)
	{
		if (!$Object=mysqli_fetch_object($result))
		{
			$this->ShowError();
			return false;
		}
		else
		{
			return $Object;
		}
	}
	/*************************
	* Method to number of rows
	**************************/
	function numRows($result)
	{
		if (false === ($num = mysqli_num_rows($result))) {
			$this->ShowError();
			return -1;
		}
		return $num;
	}
	/*******************************
	 * Method to safely escape strings
	 *********************************/
	function escapeString($string)
	{
		if (get_magic_quotes_gpc())
		{
			return $string;
		}
		else
		{
			$string = mysqli_escape_string($string);
			return $string;
		}
	}

	function free($result)
	{
		if (mysqli_free_result($result)) {
			$this->ShowError();
			return false;
		}
		return true;
	}

	function lastInsertId()
	{
		return mysqli_insert_id($this->dbLink);
	}

	function getUniqueField($sql)
	{
		$row = mysqli_fetch_row($this->query($sql));

		return $row[0];
	}

	function createDB($dept) {
        $createUsers = "CREATE TABLE IF NOT EXISTS users (
        phone varchar(55) NOT NULL DEFAULT '' ,
        password varchar(32) NOT NULL DEFAULT '',
        email varchar(45) NOT NULL DEFAULT '',
        date datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
        status tinyint(3) unsigned NOT NULL DEFAULT '0',
        authenticationTime datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
        userKey varchar(32) NOT NULL DEFAULT '',
        IP varchar(45) NOT NULL DEFAULT '',
        port int(10) unsigned NOT NULL DEFAULT '0',
		infoupdatestatus tinyint(3) NOT NULL DEFAULT '0',
        PRIMARY KEY (phone),
        KEY Index_3 (authenticationTime)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=7";
        $createDept = "CREATE TABLE IF NOT EXISTS ".$dept."(
        Cid int(10) unsigned NOT NULL AUTO_INCREMENT,
        phone varchar(55) NOT NULL ,
        Id varchar(55) NOT NULL,
        name varchar(255) NOT NULL,
		email varchar(255) NOT NULL,
		batch varchar(55) NOT NULL,
        PRIMARY KEY (phone),
        UNIQUE KEY Index_3 (Cid,Id,email),
        KEY Index_2 (Id,name,email)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cid is the Id of the users who wish to be friend with' AUTO_INCREMENT=7";

        $createMessages = "CREATE TABLE IF NOT EXISTS `messages` (
		`id` int(255) NOT NULL AUTO_INCREMENT,
		`from` int(255) NOT NULL,
		`to` int(255) NOT NULL,
		`sentDt` datetime NOT NULL,
		`take` tinyint(1) NOT NULL DEFAULT '0',
		`takeDt` datetime DEFAULT NULL,
		`read` tinyint(1) NOT NULL DEFAULT '0',
		`readDt` datetime DEFAULT NULL,
		`messageText` longtext CHARACTER SET utf8 COLLATE utf8_bin ,
		messagesPhoto longblob,
		messagesType varchar(55) NOT NULL,
		PRIMARY KEY (`id`),
		KEY `id` (`id`)
		) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=22";
		
		$createdonorList= "CREATE TABLE IF NOT EXISTS donorList(
        phone varchar(55) NOT NULL ,
        contactnumber varchar(55) NOT NULL,
        name varchar(255) NOT NULL,
		bgroup varchar(255) NOT NULL,
		donationDate varchar(55) DEFAULT NULL,
        PRIMARY KEY(phone),
        UNIQUE KEY Index_3 (contactnumber)
        ) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=22";

        $this->query($createUsers);
        $this->query($createDept);
        $this->query($createMessages);
		$this->query($createdonorList);
		//echo SUCCESSFUL;

    }

	function testconnection() {
		$this->dbLink = mysqli_connect($this->dbHost, $this->dbUsername, $this->dbPassword);
		if (!$this->dbLink)	{
			$this->ShowError();
			return false;
		}
		else if (!mysqli_select_db($this->dbLink, $this->dbName))	{
			$this->ShowError();
			return false;
		}
		else {
			mysqli_query($this->dbLink, "set names utf8");
			return true;
		}
		unset ($this->dbHost, $this->dbUsername, $this->dbPassword, $this->dbName);
	}
}