<?php
 if($_SERVER['REQUEST_METHOD']=='POST'){
 $file_name = $_FILES['myFile']['name'];
 $file_size = $_FILES['myFile']['size'];
 $file_type = $_FILES['myFile']['type'];
 $temp_name = $_FILES['myFile']['tmp_name'];
 $server_ip=$_SERVER['SERVER_ADDR'];
 $location = "upload2/";
 move_uploaded_file($temp_name, $location.$file_name);
  echo 'http://'.$server_ip.'/metroim/upload2/'.$file_name;
    //echo "http://www.simplifiedcoding.16mb.com/VideoUpload/uploads/".$file_name;
 }else{
 echo "Error";
 }
 ?>