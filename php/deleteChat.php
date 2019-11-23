<?php

require_once('init.php');

$user_id = $_POST["user_id"];
$chat_room_id = $_POST["chat_room_id"];


$sql_query_1 = "SELECT * FROM febe_chat_rooms WHERE user1_id = '$user_id' AND chat_room_id = '$chat_room_id'";

$result_1 = mysqli_query($con, $sql_query_1);


$sql_query_2 = "SELECT * FROM febe_chat_rooms where user2_id = '$user_id' AND chat_room_id = '$chat_room_id'";

$result_2 = mysqli_query($con,$sql_query_2);


if((mysqli_num_rows($result_1)>0) || (mysqli_num_rows($result_2)>0)){

$sql_query_3 = "DELETE FROM febe_chat_rooms WHERE chat_room_id = '$chat_room_id'";

$sql_query_33 = "DELETE FROM febe_chat_messages WHERE chat_room_id = '$chat_room_id'";

$sql_query_333 = "DELETE FROM febe_notifications WHERE chat_room_id = '$chat_room_id'";

}


if(mysqli_query($con, $sql_query_3) && mysqli_query($con, $sql_query_33) && mysqli_query($con, $sql_query_333)){

$sql_query_4 = "DELETE FROM febe_notifications WHERE post_user_id = '$user_id' AND chat_room_id = '55555551' UNION DELETE FROM febe_notifications WHERE notification_user_id = '$user_id' AND chat_room_id = '55555551'";

$result_4 = mysqli_query($con, $sql_query_4);

echo "Chat removed from Chat List !";

}

else
echo "Failed !";

?>