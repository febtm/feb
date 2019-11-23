<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$user_id = $_GET['user_id'];

$index = $_GET['index'];

require_once('init.php');

$sql = "SELECT cr.chat_room_id, cr.user1_id, p1.username as user1_name, cr.user1_online, cr.user1_read, cr.user2_id, p2.username as user2_name, cr.user2_online, cr.user2_read, cr.created_at FROM febe_chat_rooms cr, febe_profile p1, febe_profile p2 WHERE (cr.user1_id = $user_id OR cr.user2_id = $user_id) AND p1.userid = cr.user1_id AND p2.userid = cr.user2_id ORDER BY cr.created_at DESC LIMIT $index, 10";

$res = mysqli_query($con, $sql);

$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result, array(	

	 "chat_room_id"=>$row['chat_room_id'],
	 "user1_id"=>$row['user1_id'],
	 "user1_name"=>$row['user1_name'],
	 "user1_online"=>$row['user1_online'],
	 "user1_read"=>$row['user1_read'],
	 "user2_id"=>$row['user2_id'],
	 "user2_name"=>$row['user2_name'],
	 "user2_online"=>$row['user2_online'],
	 "user2_read"=>$row['user2_read'],
	 "created_at"=>$row['created_at'],

  )
 );
}

echo json_encode(array("chat_rooms"=>$result));
 
mysqli_close($con);
 
}

?>