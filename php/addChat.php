<?php 

require_once('init.php');

$method = $_POST['method'];

$current_user_id = $_POST['current_user_id'];

$user_id = $_POST['user_id'];

	
$sql_1 = "SELECT * FROM feb_chat_rooms WHERE user1_id = '$current_user_id' AND user2_id = '$user_id'";

$res1 = mysqli_query($con, $sql_1);

$sql_11 = "SELECT * FROM feb_chat_rooms WHERE user2_id = '$current_user_id' AND user1_id = '$user_id'";

$res11 = mysqli_query($con, $sql_11);

$sql_2 = "SELECT * FROM feb_notifications WHERE post_user_id = '$user_id' AND notification_user_id = '$current_user_id' AND chat_room_id = '55555555'";

$res2 = mysqli_query($con, $sql_2);


require_once('fcmNotPush.php');

require_once('fcmNotFirebase.php');

$push = null; 

$sql_5 = "SELECT username FROM feb_profile WHERE userid = '$current_user_id'";

$res_5 = mysqli_query($con, $sql_5);

$row_5 = mysqli_fetch_array($res_5);


$devicetoken = array();
	
$sql_6 = "SELECT device_token FROM feb_profile WHERE userid = '$user_id'";

$res_6 = mysqli_query($con,$sql_6);

$row_6 = mysqli_fetch_array($res_6);

array_push($devicetoken, $row_6['device_token']);


if($method === "chat_request")
{

if(!(mysqli_num_rows($res1)>0) && !(mysqli_num_rows($res11)>0)){

if(!(mysqli_num_rows($res2)>0)){

$sql_12 = "INSERT INTO feb_notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555555', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_12)) {
	
	
	$push = new Push("Feb", $row_5['username']. " has sent you a Chat Request !", null);
	 
	$mPushNotification = $push->getPush(); 
	
	 
	$firebase = new Firebase(); 
	 
	$firebase->send($devicetoken, $mPushNotification);
	
	
	echo "A Chat request has been sent to this User !";

}
}

else
echo "A Chat request has already been sent to this User !";

}

else{

$sql_4 = "SELECT chat_room_id FROM feb_chat_rooms WHERE (user1_id = '$current_user_id' AND user2_id = '$user_id') OR (user2_id = '$current_user_id' AND user1_id = '$user_id') ";

$res4 = mysqli_query($con, $sql_4);

$row = mysqli_fetch_array($res4);

$chat_room_id = $row['chat_room_id'];

echo "$chat_room_id";

}
}


else if($method === "chat_accept")
{

if(!(mysqli_num_rows($res1)>0) && !(mysqli_num_rows($res11)>0)){

$sql1 = "INSERT INTO feb_chat_rooms (user1_id, user2_id, user1_online) VALUES ('$current_user_id', '$user_id', '1')";

$result_1 = mysqli_query($con, $sql1);

$sql_11 = "INSERT INTO feb_notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555551', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_11)){

$sql_111 = "DELETE FROM feb_notifications WHERE chat_room_id = '55555555' AND post_user_id = '$current_user_id' AND notification_user_id = '$user_id'";

$result_111 = mysqli_query($con, $sql_111);


$push = new Push("Feb", $row_5['username']. " has accepted your Chat Request !", null);

$mPushNotification = $push->getPush(); 

	 
$firebase = new Firebase(); 

$firebase->send($devicetoken, $mPushNotification);

}

}

$sql_4 = "SELECT chat_room_id FROM feb_chat_rooms WHERE (user1_id = '$current_user_id' AND user2_id = '$user_id') OR (user2_id = '$current_user_id' AND user1_id = '$user_id')";

$res4 = mysqli_query($con, $sql_4);

$row = mysqli_fetch_array($res4);

$chat_room_id = $row['chat_room_id'];

echo "$chat_room_id";

}


else
{

$sql_22 = "INSERT INTO feb_notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555552', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_22)){

$sql_22 = "DELETE FROM feb_notifications WHERE chat_room_id = '55555555' AND post_user_id = '$current_user_id' AND notification_user_id = '$user_id'";

$result_22 = mysqli_query($con, $sql_22);


$push = new Push("Feb", $row_5['username']. " has rejected your Chat Request !", null);

$mPushNotification = $push->getPush();

	 
$firebase = new Firebase(); 

$firebase->send($devicetoken, $mPushNotification);


echo "The Chat request has been rejected !";

}

}

mysqli_close($con);

?>