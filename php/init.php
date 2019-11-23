<?php

$db_name = "id4775978_febtech";
$mysql_user = "id4775978_febin";
$mysql_pass = "hugh#007";
$server_name = "localhost";

define('FIREBASE_API_KEY', 'AAAApnqc97s:APA91bHeSOySgKNMy2E9FBMaYFFCgQhEM0FlHR02Xjv20p2xXAsaxBP7kiksJIg7JBfCxBizXtVPIe599Pd5M53YLsTvyQb9UrRPt6kyWrkPXAmlIipkX2tWzRpa1qBzAuAbTZbkeC5K');

$con = mysqli_connect($server_name,$mysql_user,$mysql_pass,$db_name);

if(!$con)
{
//echo "Connection Error ... ".mysqli_connect_error();
}
else
{
//echo "<h3>Database Connection Success ... </h3>";
}

?>