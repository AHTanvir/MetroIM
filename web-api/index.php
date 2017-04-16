<?php
require_once("mysql.class.php");
$dbHost ="localhost";
$dbUsername ="metroim";
$dbPassword ="metroim";
$dbName ="metroim";
$db = new MySQL($dbHost,$dbUsername,$dbPassword,$dbName);

// if operation is failed by unknown reason
define("FAILED", 0);
define("SUCCESSFUL", 1);
// when  signing up, if username is already taken, return this error
define("SIGN_UP_CRASHED", 5);

// TIME_INTERVAL_FOR_USER_STATUS: if last authentication time of user is older
// than NOW - TIME_INTERVAL_FOR_USER_STATUS, then user is considered offline
define("TIME_INTERVAL_FOR_USER_STATUS", 60);
define("NO_NEW_UPDATE",9);
$phone = (isset($_REQUEST['phone']) && count($_REQUEST['phone']) > 0) ? $_REQUEST['phone'] : NULL;
$password = isset($_REQUEST['password']) ? md5($_REQUEST['password']) : NULL;
$port = isset($_REQUEST['port']) ? $_REQUEST['port'] : NULL;
$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : NULL;

if ($action == "testWebAPI")
{
	if ($db->testconnection()){
	echo SUCCESSFUL;
	exit;
	}else{
	echo FAILED;
	exit;
	}
}
if ($phone == NULL || $password == NULL)
{
	echo FAILED;
	exit;
}
$out = NULL;
switch($action)
{
	case "signUpUser":
			 $sql = "select * from  users
			 				where phone = '".$phone."' limit 1";

			 if ($result = $db->query($sql))
			 {
				 $name=$_REQUEST['name'];
				 $email=$_REQUEST['email'];
				 $dept=$_REQUEST['dept'];
				 $batch=$_REQUEST['batch'];
				 $Id=$_REQUEST['Id'];
				 $db->createDB($dept);
			 		if ($db->numRows($result) == 0)
			 		{
			 				$sql = "insert into users( phone, password, dept)
			 					values ( '".$phone."', '".$password."', '".$dept."') ";
								
							$sql1= "insert into ".$dept."(name,Id, phone, email,batch)
			 					values ('".$name."', '".$Id."', '".$phone."', '".$email."','".$batch."') ";


							if ($db->query($sql) && $db->query($sql1))
							{
							 		$out = SUCCESSFUL;

							}
							else {
									$out = FAILED;
							}
						
			 		}
			 		else
			 		{
			 			$out =SIGN_UP_CRASHED;
			 		}
			 }
	break;
	case "authenticateUser":
	         $userphone=authenticateUser($db, $phone, $password);
			 if($userphone !=null)
			 {
				 $sql="select * from messages where tot='".$userphone."' and take=0 limit 0,15";
				 if($result=$db->query($sql))
				 {
					 $message_id="000";
					  $response = array();
				 $response['message']=array();
				 while($row = mysqli_fetch_array($result)){
					 $message_id=$row['id'];
					 $messagesType=$row['messagesType'];
					 $temp = array(); 
					 $temp['sfrom']=$row['sfrom'];
					 $temp['sentDt']=$row['sentDt'];
					 $temp['messagesType']=$row['messagesType'];
					 $temp['messageText']=$row['messageText'];
					 array_push($response['message'],$temp);
					 $sql22="update messages set take= 1, takeDt='".DATE("Y-m-d H:i")."' where id= ".$message_id."";
					$db->query($sql22);
					 }
					$out=json_encode($response);
				 }
				 if(is_null($out))
				 {
					 $out=NO_NEW_UPDATE;
				 }				 
			 }
           else{
			   $out=FAILED;
		   }
				 /*
				 if($result=$db->query($sql))
				 {
					 if($result22=$db->fetchObject($result))
					 {
						$sql22="update messages set take= 0, takeDt='".DATE("Y-m-d H:i")."' where id= ".$result22->id."";
						if($db->query($sql22))
						{
							$mType=$result22->messagesType;
							if($mType=="messagesText"){
								$mType="text";
							}
							//$mess=$result22->$mType;
							 //parts[0]=from,parts[1]=sentDate,parts[2]=MessageType,parts[3]=Message
							$message[]="".$result22->sfrom.";".$result22->sentDt.";".$mType.";".$result22->$mType."";
						}
					 }
					 $out=json_encode($message);
				 }
				 
                if(is_null($out)){
						$out=NO_NEW_UPDATE;
					}						
			 }
           else{
			   $out=FAILED;
		   } */   			 
	break;
	case "sendMessage":
	if ($userphone =authenticateUser($db, $phone, $password))
		{
	   if (isset($_REQUEST['tto']))
		{
			 $tophone= $_REQUEST['tto'];
			 $mType=$_REQUEST['mType'];
			 $messagess=$_REQUEST['message'];
			  //$sql = "INSERT INTO `messages`(sfrom,tot) VALUES ('".$tophone."','".$tophone."')";

			 $sql ="INSERT INTO `messages` (sfrom, tot, sentDt,messageText,messagesType) VALUES('".$userphone."', '".$tophone."', '".DATE("Y-m-d H:i")."', '".$messagess."','".$mType."') ";

							if ($db->query($sql))
							{
							 		$out = SUCCESSFUL;
							}
							else {
									$out = FAILED;
							}
		 }
		}
		else
		{
			$out=FAILED;
		}
	break;
	case "contactUpdate":
	   $json1=$_REQUEST['json'];
	   $aaj=json_decode($json1);
	   $len=count($aaj);
	   $out =null;
	 /*  
	    for($i=0; $i<$len;$i++){
		   if($phonenum=checkValidity($aaj[$i]))
		   {        
					//$out[]="[{".name.":".$phonenum[0].",".num.":".$phonenum[1]."}]";
					$out[]=$phonenum[0];
					$out[]=$phonenum[2];
					  // $sql22="delete from contact where 1";
			   	     $sql="insert into contact(Con_id, num)
		                values('".$phonenum[0]."', '".$phonenum[1]."')";
		               // $db->query($sql);
					   break;
	   }
		}
	*/
	$contacts=null;
	   for($i=0; $i<$len;$i++){
		   if($phonenum=checkValidity($aaj[$i]))
		   {
			   $sql1="select * from users where phone='".$phonenum[0]."' limit 1";
			   //$sql1="select * from users where phone='".$phone."' limit 1";
			   if($res=$db->query($sql1))
			   {
				   if ($row=$db->fetchObject($res))
				   {
					     $contacts[]="".$phonenum[1]."";
						 $sql22="delete from contact where 1";
						 $sql="insert into contact(Con_id, num)
		                values('".$phonenum[0]."', '".$phonenum[1]."')";
		                //$db->query($sql);
				   }
			   }  
		   }
	   }
		$out=json_encode($contacts);
		if(is_null($contacts)){
			$out=NO_NEW_UPDATE;
		}
	break;
	  
	case "updateInfo":
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		if (isset($_REQUEST['photo']))
		{
			$image=$_REQUEST['photo'];
			$image2=json_decode($image);
			$image3=$image2->{'tt'};
			$sql="update users set photo='".$image3."' where phone=".$phone." ";
			if($db->query($sql))
			{
				$sql="UPDATE `users` SET `infoupdatestatus`=CASE `infoupdatestatus`
				WHEN 0 THEN 1 WHEN 1 THEN 0 ELSE `infoupdatestatus` END WHERE phone='".$phone."'";
				$db->query($sql);
				$out=SUCCESSFUL;
			}
			else $out=FAILED;
		}
		else if (isset($_REQUEST['status']))
		{
			$stat=$_REQUEST['status'];
			$sql="update users set contactstatus='".$stat."' where phone=".$phone." ";
			
			if($db->query($sql))
			{
				$sql="UPDATE `users` SET `infoupdatestatus`=CASE `infoupdatestatus`
				WHEN 0 THEN 1 WHEN 1 THEN 0 ELSE `infoupdatestatus` END WHERE phone='".$phone."'";
				$db->query($sql);
				$out=SUCCESSFUL;
			}
			else $out=FAILED;
		}
		else if(isset($_REQUEST['getinfo']))
		{
			//$sql="select * from users where phone='".$userphone."'";
			$f=$db->query("select dept from users where phone='".$userphone."'");
			$f=$db->fetchObject($f);
			
			$sql="select * from users t1 
			left join ".$f->dept ." t2 on t1.phone=t2.phone 
			where t1.phone='".$userphone."'";
			if($res=$db->query($sql))
			{
				if($re=$db->fetchObject($res))
				{
					$info[]=$re->name;
					$info[]=$re->contactstatus;
					$info[]=$re->photo;
					$out=json_encode($info);
				}
				else $out=FAILED;
			}
			else $out=FAILED;
		}
		else if(isset($_REQUEST['updateName']))
		{
			$f=$db->query("select dept from users where phone='".$userphone."'");
			$f=$db->fetchObject($f);
			$sql="update ".$f->dept ." set name='".$_REQUEST['updateName']."' 
			           where phone='".$userphone."'";
			 if($db->query($sql))
			 {
				 $out=SUCCESSFUL;
			 }
			 else $out=FAILED;
			 
		}
		else if(isset($_REQUEST['updatePassword']))
		{
			$newpass= isset($_REQUEST['updatePassword']) ? md5($_REQUEST['updatePassword']) : NULL;
			$sql="update users set password='".$newpass."' where phone='".$userphone."'";
			 if($db->query($sql))
			 {
				 $out=SUCCESSFUL;
			 }
			 else $out=FAILED;
		}
		else if(isset($_REQUEST['deleteaccount']))
		{
			$f=$db->query("select dept from users where phone='".$userphone."'");
			$f=$db->fetchObject($f);
			 $sql="delete from users where phone='".$userphone."' ";
			 "delete from ".$f->dept ." where phone='".$userphone."' ";
			 if($db->query($sql))
			 {
				 $out=SUCCESSFUL;
			 }
			 else $out=FAILED;
		}
		else if(isset($_REQUEST['getFriendInfo']))
		{
			$f=$db->query("select dept from users where phone='".$_REQUEST['getFriendInfo']."'");
			$f=$db->fetchObject($f);
			
			$sql="select * from users t1 
			left join ".$f->dept ." t2 on t1.phone=t2.phone 
			where t1.phone='".$_REQUEST['getFriendInfo']."'";
			if($result=$db->query($sql))
			{
				$response = array();
				$response['info']=array();
				while($row=mysqli_fetch_array($result))
				{
					$temp = array();
					$temp['name']=$row['name'];
					$temp['phone']=$row['phone'];
					$temp['email']=$row['email'];
					if($row['dept']=='teacher')
					{
						$temp['type']='Teacher';
						$temp['dept']=$row['batch'];
					}
					else{
						$temp['type']='Student';
						$temp['dept']=$row['dept'];
					}
					$temp['photo']=$row['photo'];
					$temp['contactstatus']=$row['contactstatus'];
					array_push($response['info'],$temp);
				}
				$out=json_encode($response);
			}
		}
	}
	else $out=FAILED;
	 break;
	 
	case "updateUsersInfo":	
	$contact_list=$_REQUEST['contact_list'];
	$contact_list1=json_decode($contact_list);
	$len=count($contact_list1);
	$userphone=authenticateUser($db, $phone, $password);
	$contact=null;
	if($userphone !=null)
	{
		 $response = array();
		$response['contactinfo']=array();
		for($i=0; $i<$len;$i++){
			$contact_list2=explode(";",$contact_list1[$i]);
		 $sql1="select * from users where phone=".$contact_list2[0]." and infoupdatestatus !=".$contact_list2[1]." limit 1";
			   if($res=$db->query($sql1))
			   {
				   if ($row22=$db->fetchObject($res))
				   {
					   $temp=array();
					   $temp['number']=$contact_list2[0];
					   $temp['contactstatus']=$row22->contactstatus;
					   $temp['photo']=$row22->photo;
					   $temp['infoupdatestatus']=$row22->infoupdatestatus;
					    array_push($response['contactinfo'],$temp);
					   //$contact[]="".$contact_list2[0].";".$status.";".$photo.";".$infoupdatestatus."";
				   }
			   } 
	}
	$out=json_encode($response);
	}
	else{$out=FAILED;		
	}
	break;
	
	case "donorlist":	
	$bloodGroup=$_REQUEST['group'];
	 $dd= new DateTime(date('Y-m-d'));
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		 $response = array();
		$response['donorList']=array();
		$sql1="select * from donorList where bgroup='".$bloodGroup."'";
			   if($res=$db->query($sql1))
			   {
				   while ($row=mysqli_fetch_array($res))
				   {
					   $difference=91;
					   $d=new DateTime($row['donationDate']);
					   if($row['donationDate']!= NULL)
					   {
						   $difference=$d->diff($dd)->format('%R%a');
					   }
					   if($difference>=90)
					   {
						   $temp=array();
						   $temp['name']=$row['name'];
						   $temp['number']=$row['contactnumber'];
						   array_push($response['donorList'],$temp);
					   }
				   }
			   }
	$out=json_encode($response);
	}
	else{$out=FAILED;		
	}
	 break;
	
	case "addDonor":	
	$name=$_REQUEST['name'];
	$number=$_REQUEST['number'];
	$bloodGroup=$_REQUEST['group'];
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		$sql = "select phone from  donorList where phone = '".$userphone."' limit 1";
		if ($result = $db->query($sql))
		{
			if ($db->numRows($result) == 0)
			{
				$sql = "insert into donorList( phone, contactnumber, name,bgroup)
			 					values ( '".$phone."', '".$number."', '".$name."','".$bloodGroup."') ";
								if ($db->query($sql))
								{
								$out=SUCCESSFUL;
								}
								else
								{
								$out=FAILED;
								}
								
			}
			else $out =SIGN_UP_CRASHED;
		}
	}
	else{$out=FAILED;		
	}
	break;
	
	case "updateDonationDate":	
	$date=$_REQUEST['date'];
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		$sql ="update donorList set donationDate= '".$date."' where phone=".$phone."";
							if ($db->query($sql))
							{
								$out = SUCCESSFUL;
							}
							else
							{
								$out = FAILED;
							}
	}
	else{$out=FAILED;		
	}
	break;
	
	case "deleteDonor":	
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		$sql ="delete from donorList where phone=".$phone."";
							if ($db->query($sql))
							{
								$out = SUCCESSFUL;
							}
							else
							{
								$out = FAILED;
							}
	}
	else{$out=FAILED;		
	}
	break;
	case "getLastSeen":	
	$userphone=authenticateUser($db, $phone, $password);
	if($userphone !=null)
	{
		$sql="select date,  (NOW()-date) as authenticateTimeDifference 
		from users where phone='".$_REQUEST['friendphone']."' ";"
		update users set date=NOW() where phone='".$phone."'";
		//$sql= "select authenticationTime,  (NOW()-authenticationTime) as authenticateTimeDifference from users where phone='".$_REQUEST['friendphone']."'";
		if($d=$db->query($sql))
		{
			$res=$db->fetchObject($d);
			if (((int)$res->authenticateTimeDifference ) < TIME_INTERVAL_FOR_USER_STATUS)
			{
				$out= "online";
			}
			else{
				$out=$res->date;
			}
				
		}
		else $out=FAILED;
	}
	else $out=FAILED;
	break;
}
echo $out;
/////////////////////////////
function checkValidity($phonenum){
	$phonenum= explode(";", $phonenum);
    $phonenum[0]=str_replace(' ', '', $phonenum[0]);
	$phonenum[0]=preg_replace('/[^+0-9]/', '', $phonenum[0]);
	$phonenum[0]=preg_replace('/[^+0-9]/', '', $phonenum[0]);
   // $phonenum[0]=preg_replace('/[^+A-Za-z0-9]/', '', $phonenum[0]);
	$length=strlen($phonenum[0]);	
	return $phonenum;
	
}
///////////////////////////////////////////////////////////////
function authenticateUser($db, $phone, $password)
{

	$sql22 = "select * from users
					where phone = '".$phone."' and password = '".$password."'
					limit 1";

	$out =null;
	if ($result22 = $db->query($sql22))
	{
		if ($row22 = $db->fetchObject($result22))
		{
				$out = $row22->phone;

				$sql22 = "update users set date = NOW(),
																 IP = '".$_SERVER["REMOTE_ADDR"]."' ,
																 port = 15145
								where phone= ".$row22->phone."
								limit 1";

				$db->query($sql22);


		}
		
	}
	return $out;
}
?>