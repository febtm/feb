<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$index = $_GET['index'];

$searchText = $_GET['searchText'];

require_once('init.php');

$sql = "SELECT userid, username FROM febe_profile WHERE username LIKE '%".addslashes($searchText)."%' ORDER BY created_at DESC LIMIT $index, 10";
 	
$res = mysqli_query($con,$sql);
 
$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result,array(	
	 
	 "user_id"=>$row['userid'],
	 "username"=>$row['username'],
	 
 )
 );
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);

}
 
?>