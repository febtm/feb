<?php 

require_once('init.php');

$method = $_POST["method"];
$id = $_POST["id"];
$comment = $_POST["comment"];
$user_id = $_POST["user_id"];

$methodname4 = "notification";
$methodname5 = "readnotification";
$methodname6 = "readchat";

$sql_12 = "SELECT username FROM febe_profile WHERE userid = $user_id";

$res_12 = mysqli_query($con, $sql_12);

$row_12 = mysqli_fetch_array($res_12);


if($method === $methodname4)
{

$sql_4 = "INSERT INTO febe_notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '$id', '$comment', '$user_id')";

if(mysqli_query($con,$sql_4))
echo "Notification sent successfully !";

else
echo "Notification Failed !";

}


else if($method === $methodname5)
{

if(!($comment === $user_id)){

$sql_5 = "UPDATE febe_notifications SET post_user_read = 1 WHERE post_user_id = '$user_id' AND post_id = '$id' AND notification_user_id = '$comment';";

if(mysqli_query($con, $sql_5))
echo "Success !";
}

}


else if($method === $methodname6)
{

$sql_6 = "SELECT * FROM febe_chat_rooms WHERE user1_id = '$user_id' AND chat_room_id = '$id'";

$res_6 = mysqli_query($con, $sql_6);


$sql_6_1 = "SELECT * FROM febe_chat_rooms WHERE user2_id = '$user_id' AND chat_room_id = '$id'";

$res_6_1 = mysqli_query($con, $sql_6_1);

if(mysqli_num_rows($res_6)>0)
$sql_6_2 = "UPDATE febe_chat_rooms SET user1_read = 1 WHERE chat_room_id = '$id';";

else if(mysqli_num_rows($res_6_1)>0)
$sql_6_2 = "UPDATE febe_chat_rooms SET user2_read = 1 WHERE chat_room_id = '$id';";

else
$sql_6_2 = "SELECT * FROM febe_profile";


if(mysqli_query($con, $sql_6_2))
echo "Success !";

}

?>