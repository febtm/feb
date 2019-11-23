<?php

require_once('init.php');

$user_username=$_POST["signin_username"];
$user_email=$_POST["signin_email"];
$user_password=$_POST["signin_password"];
$user_device_token=$_POST["signin_device_token"];


$sql_query_1 = "SELECT * FROM febe_profile WHERE email = '".addslashes($user_email)."';";

$result_1 = mysqli_query($con,$sql_query_1);


$sql_query_2 = "SELECT * FROM febe_profile WHERE username = '".addslashes($user_username)."';";

$result_2 = mysqli_query($con,$sql_query_2);


if(mysqli_num_rows($result_1)>0)
echo "An Account with this Email-Id already exists !";


else
{

if(mysqli_num_rows($result_2)>0)
echo "Username already exists ! Please enter a unique Username !";

else
{

$sql_query_3 ="INSERT INTO febe_profile (username, email, password, device_token) values('$user_username', '".addslashes($user_email)."', '".addslashes($user_password)."', '$user_device_token');";

if(mysqli_query($con,$sql_query_3))
echo "Sign In Successful !";

else
echo "Sign In Failed !";

}

}

?>