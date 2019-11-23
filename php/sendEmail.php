<?php

require_once('init.php');

$method = $_POST['method'];
$from_email = $_POST['from_email'];
$to_email = $_POST['to_email'];
$subject = $_POST['subject'];
$message = $_POST['message'];

if($method === "ContactUs"){
    
    $headers = 'From: <' . $from_email . '>' . "\r\n" .
        'Reply-To: <' . $from_email . '>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();
	
    if(mail($to_email, $subject, $message, $headers))
		echo "Thank You for your message, kindly wait until our Team responds to it !";
	
	else
		echo "Sending Email Failed !";
    
}

else if($method === "RecoverPassword"){
    
    $headers = 'From: Feb <fmt.febulous@gmail.com>' . "\r\n" .
        'Reply-To: Feb <fmt.febulous@gmail.com>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();
	
	$sql = "SELECT username, password FROM feb_profile WHERE email = '".addslashes($to_email)."'";

	$res = mysqli_query($con, $sql);

	if(mysqli_num_rows($res)>0){
		
		$row = mysqli_fetch_array($res);
		
		$receiver_username = $row['username'];
		
		$receiver_password = $row['password'];
		
		
		$message = str_replace("BATMAN", $receiver_username, $message);
		$message = str_replace("JOKER", $receiver_password, $message);
		
		
		if(mail($to_email, $subject, $message, $headers))
			echo "A recovery email has been sent to your Email-Id !";
		
		else
			echo "Sending Email Failed !";
		
	}
	
	else
		echo "An Account with this Email-Id does not exist !";

}

else if($method === "SendEmail"){
    
    $headers = 'From: <' . $from_email . '>' . "\r\n" .
        'Reply-To: <' . $from_email . '>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();
	
    if(mail($to_email, $subject, $message, $headers))
		echo "Your Email has been sent !";
	
	else
		echo "Sending Email Failed !";
    
}
	
mysqli_close($con); 
 
?>