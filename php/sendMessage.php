<?php 

function Cipher($ch, $key){
    
	if (!ctype_alpha($ch))
		return $ch;

	$offset = ord(ctype_upper($ch) ? 'A' : 'a');
	
	return chr(fmod(((ord($ch) + $key) - $offset), 26) + $offset);
	
}

function Encrypt($input, $year, $month, $day){
    
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
	        
		$output .= Cipher($ch, $key);
		
		$count += 1;
		
	}

	return $output;
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

$user_id = $_GET['user_id'];

$message = urldecode($_GET['message']);

$timestamp = $_GET['timestamp'];


$split_timestamp = explode(" ", $timestamp);

$split_timestamp = explode("-", $split_timestamp[0]);

$year = $split_timestamp[0] - 2000;

$month = $split_timestamp[1];

$day = $split_timestamp[2];

$message = Encrypt($message, $year, $month, $day);


require_once('init.php');

$result['message'] = array();

$result['user'] = array();

$sql = "INSERT INTO feb_chat_messages (chat_room_id, user_id, message, created_at) values('$chat_room_id', '$user_id', '".addslashes($message)."', '$timestamp')";

if(mysqli_query($con, $sql)){

    $message_id = mysqli_insert_id($con);

    $tmp = array();
    $tmp['message_id'] = $message_id;
    $tmp['chat_room_id'] = $chat_room_id;
    $tmp['message'] = $message;
    
    $tmp['message'] = Decrypt($tmp['message'], $year, $month, $day);
    
    $tmp['created_at'] = $timestamp;

    $result['message'] = $tmp;
	
	$user = array();
	
	$sql_2 = "SELECT username FROM feb_profile WHERE userid = '$user_id'";

	$res_2 = mysqli_query($con, $sql_2);

	$row_2 = mysqli_fetch_array($res_2);

	$user['user_id'] = $user_id;
	$user['username'] = $row_2['username'];
	
	$result['user'] = $user;
	
	$sql_3 = "UPDATE feb_chat_rooms SET created_at = '$timestamp' WHERE chat_room_id = '$chat_room_id';";

	$res_3 = mysqli_query($con, $sql_3);

	$sql_4 = "SELECT * FROM feb_chat_rooms WHERE user1_id = '$user_id' AND chat_room_id = '$chat_room_id'";

	$res_4 = mysqli_query($con, $sql_4);

	$sql_5 = "SELECT * FROM feb_chat_rooms WHERE user2_id = '$user_id' AND chat_room_id = '$chat_room_id'";

	$res_5 = mysqli_query($con, $sql_5);

    if(mysqli_num_rows($res_4)>0)
    $sql_6 = "UPDATE feb_chat_rooms SET user1_read = 1, user2_read = 0 WHERE chat_room_id = '$chat_room_id';";

    else if(mysqli_num_rows($res_5)>0)
    $sql_6 = "UPDATE feb_chat_rooms SET user1_read = 0, user2_read = 1 WHERE chat_room_id = '$chat_room_id';";

    else
    $sql_6 = "SELECT * FROM feb_profile";

    if(mysqli_query($con, $sql_6)){
	

	require_once('fcmNotPush.php');

	require_once('fcmNotFirebase.php');


	$push = null; 

	$sql_7 = "SELECT username FROM feb_profile WHERE userid = '$user_id'";

	$res_7 = mysqli_query($con, $sql_7);

	$row_7 = mysqli_fetch_array($res_7);

	$push = new Push("Feb", $row_7['username']. " has sent you a Message !", null);
	 
	$mPushNotification = $push->getPush(); 
	


	$devicetoken = array();
	
	$sql_8 = "SELECT user1_id FROM feb_chat_rooms WHERE chat_room_id = '$chat_room_id' AND user2_id = '$user_id'";

	$res_8 = mysqli_query($con, $sql_8);
	
	$sql_9 = "SELECT user2_id FROM feb_chat_rooms WHERE chat_room_id = '$chat_room_id' AND user1_id = '$user_id'";

	$res_9 = mysqli_query($con, $sql_9);

	if(mysqli_num_rows($res_8)>0){
		
	$row_8 = mysqli_fetch_array($res_8);
	
	$receiver_user_id = $row_8['user1_id'];	
	
	}
	
	else{
		
	$row_9 = mysqli_fetch_array($res_9);

	$receiver_user_id = $row_9['user2_id'];

	}
	
	$sql_10 = "SELECT device_token FROM feb_profile WHERE userid = '$receiver_user_id'";

	$res_10 = mysqli_query($con,$sql_10);

	$row_10 = mysqli_fetch_array($res_10);

	array_push($devicetoken, $row_10['device_token']);

	 
	$firebase = new Firebase(); 
	 
	$firebase->send($devicetoken, $mPushNotification);
	
	
	$sql_11 = "SELECT COUNT(*) as final_message_id FROM feb_chat_messages WHERE chat_room_id = $chat_room_id";

	$res_11 = mysqli_query($con, $sql_11);

	$row_11 = mysqli_fetch_array($res_11);

	$tmp['final_message_id'] = $row_11['final_message_id'];

	$result['final_message_id'] = $tmp;
	
	
	echo json_encode($result);

	}
    }

mysqli_close($con);

}

?>