<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$current_user_id = $_GET['current_user_id'];

$index = $_GET['index'];

require_once('init.php');

$sql = "SELECT p.username as notification_username, n.* FROM feb_notifications n LEFT JOIN feb_profile p ON n.notification_user_id = p.userid WHERE ((n.post_user_id = '$current_user_id' AND n.post_user_id != n.notification_user_id) OR (n.post_user_id = n.notification_user_id AND n.post_user_id != '$current_user_id')) ORDER BY n.created_at DESC LIMIT $index, 10";

$res = mysqli_query($con, $sql);

$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result, array(	

	 "post_id"=>$row['post_id'],
	 "chat_room_id"=>$row['chat_room_id'],
	 "post_user_read"=>$row['post_user_read'],
	 "notification_user_id"=>$row['notification_user_id'],
	 "notification_username"=>$row['notification_username'],
	 "timestamp"=>$row['created_at'],

  )
 );
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
}

?>