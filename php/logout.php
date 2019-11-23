<?php

require_once('init.php');

$userid = $_POST['userid'];

$sql1 = "SELECT * FROM feb_chat_rooms WHERE user1_id = '$userid'";

$result1 = mysqli_query($con, $sql1);

$sql2 = "SELECT * FROM feb_chat_rooms WHERE user2_id = '$userid'";

$result2 = mysqli_query($con, $sql2);

if(mysqli_num_rows($result1)>0)
$sql3 = "UPDATE feb_chat_rooms SET user1_online = 0 WHERE user1_id = '$userid';";

else if(mysqli_num_rows($result2)>0)
$sql3 = "UPDATE feb_chat_rooms SET user2_online = 0 WHERE user2_id = '$userid';";

else
$sql3 = "SELECT * FROM feb_profile";

$sql4 = "UPDATE feb_profile SET device_token = '' WHERE userid = '$userid'";


if(mysqli_query($con, $sql3) && mysqli_query($con, $sql4))
echo "Logout Successful !";

else
echo "Logout Failed !";

?>