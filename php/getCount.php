<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$user_id= $_GET['user_id'];

require_once('init.php');


$sql1 = "SELECT count(*) AS notification_count FROM feb_notifications WHERE post_user_id = '$user_id' AND post_user_read = 0 AND post_user_id != notification_user_id";
 
$res1 = mysqli_query($con, $sql1);
 
$row1 = mysqli_fetch_array($res1);


$sql2 = "SELECT count(*) AS chat_count FROM feb_chat_rooms WHERE (user1_id = '$user_id' AND user1_read = 0) OR (user2_id = '$user_id' AND user2_read = 0)";
 
$res2 = mysqli_query($con, $sql2);
 
$row2 = mysqli_fetch_array($res2);


$result = array();

array_push($result,array(	
	 "notification_count"=>$row1['notification_count'],
	 "chat_count"=>$row2['chat_count'],
)
);
 
echo json_encode(array("result"=>$result));
 
mysqli_close($con);

}
 
?>
