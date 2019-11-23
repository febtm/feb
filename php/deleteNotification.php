<?php

require_once('init.php');

$id = $_POST["id"];
$chat_room_id = $_POST["chat_room_id"];
$user_id = $_POST["user_id"];
$notify_user_id = $_POST["notify_user_id"];


if(!($user_id === $notify_user_id))
{

$sql_query_1 = "DELETE FROM feb_notifications WHERE post_id = '$id' AND chat_room_id = '$chat_room_id' AND post_user_id = '$user_id' AND notification_user_id = '$notify_user_id' ";

if(mysqli_query($con,$sql_query_1))
echo "Notification removed from Notification List !";

else
echo "Failed !";

}

?>