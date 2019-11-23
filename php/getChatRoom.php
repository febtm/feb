<?php

function Cipher($ch, $key){
    
	if (!ctype_alpha($ch))
		return $ch;

	$offset = ord(ctype_upper($ch) ? 'A' : 'a');
	
	return chr(fmod(((ord($ch) + $key) - $offset), 26) + $offset);
	
}

function Decrypt($input, $year, $month, $day){
    
	$output = "";
	
	$count = 0;
	
	$key = 0;
	
	$inputArr = str_split($input);
	
	foreach ($inputArr as $ch){
	    
	    if($count % 2 == 0)
	        $key = $day % 26;
	    
	    else if($count % 3 == 0)
	        $key = $month % 26;
	        
	    else
	        $key = $year % 26;
	        
		$output .= Cipher($ch, 26 - $key);
		
		$count += 1;
		
	}

	return $output;
}


if($_SERVER['REQUEST_METHOD']=='GET'){

$chat_room_id = $_GET['chat_room_id'];

$index = $_GET['index'];

$type = $_GET['type'];

require_once('init.php');

if($type === "old")
$sql = "SELECT p.username as username, c.message_id, c.user_id, c.message, c.created_at FROM feb_profile p, feb_chat_messages c WHERE p.userid = c.user_id AND c.chat_room_id = $chat_room_id ORDER BY c.created_at DESC LIMIT $index, 10";

else
$sql = "SELECT p.username as username, c.message_id, c.user_id, c.message, c.created_at FROM feb_profile p, feb_chat_messages c WHERE p.userid = c.user_id AND c.chat_room_id = $chat_room_id ORDER BY c.created_at LIMIT $index, 10";

$res = mysqli_query($con, $sql);

$result["messages"] = array();

while($row = mysqli_fetch_array($res)){

$cmt = array();
$cmt["message"] = $row['message'];
$cmt["message_id"] = $row['message_id'];
$cmt["created_at"] = $row['created_at'];


$split_timestamp = explode(" ", $cmt["created_at"]);

$split_timestamp = explode("-", $split_timestamp[0]);

$year = $split_timestamp[0] - 2000;

$month = $split_timestamp[1];

$day = $split_timestamp[2];

$cmt["message"] = Decrypt($cmt["message"], $year, $month, $day);


$user = array();
$user['user_id'] = $row['user_id'];
$user['username'] = $row['username'];
$cmt['user'] = $user;

array_push($result["messages"], $cmt);

}

if($type === "old")
$result["messages"] = array_reverse($result["messages"]);


$sql_1 = "SELECT COUNT(*) as final_message_id FROM feb_chat_messages WHERE chat_room_id = $chat_room_id";

$res_1 = mysqli_query($con, $sql_1);

$row_1 = mysqli_fetch_array($res_1);

$tmp['final_message_id'] = $row_1['final_message_id'];

$result['final_message_id'] = $tmp;

	
echo json_encode($result);
 
mysqli_close($con);
 
}

?>