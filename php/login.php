<?php

require_once('init.php');

$user_email=$_POST["login_email"];
$user_password=$_POST["login_password"];
$user_device_token=$_POST["login_device_token"];


$sql1 = "SELECT userid FROM febe_profile WHERE email = '".addslashes($user_email)."' AND password = '".addslashes($user_password)."';";

$result1 = mysqli_query($con, $sql1);

if(mysqli_num_rows($result1)>0){

$row = mysqli_fetch_array($result1);

$user_id = $row['userid'];


$sql2 = "UPDATE febe_profile SET device_token = '$user_device_token' WHERE userid = '$user_id';";

$result2 = mysqli_query($con, $sql2);


$sql3 = "SELECT * FROM febe_chat_rooms WHERE user1_id = '$user_id'";

$result3 = mysqli_query($con, $sql3);


$sql4 = "SELECT * FROM febe_chat_rooms WHERE user2_id = '$user_id'";

$result4 = mysqli_query($con, $sql4);


if(mysqli_num_rows($result3)>0)
$sql5 = "UPDATE febe_chat_rooms SET user1_online = 1 WHERE user1_id = '$user_id';";

else if(mysqli_num_rows($result4)>0)
$sql5 = "UPDATE febe_chat_rooms SET user2_online = 1 WHERE user2_id = '$user_id';";

else
$sql5 = "SELECT * FROM febe_profile";

if(mysqli_query($con, $sql5))
echo "Login Successful !";

else
echo "Login Failed !";

}

else
echo "Email-Id and Password does not match !";

?>