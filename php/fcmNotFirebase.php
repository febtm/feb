<?php

class Firebase {
 
    public function send($registration_ids, $message) {
		
        $fields = array(
            'registration_ids' => $registration_ids,
            'data' => $message,
        );
		
        return $this->sendPushNotification($fields);
		
    }
    
    private function sendPushNotification($fields) {
         
        require_once 'init.php';
        
        $url = 'https://fcm.googleapis.com/fcm/send';
 
        $headers = array(
            'Authorization: key=' . FIREBASE_API_KEY,
            'Content-Type: application/json'
        );
 
        $ch = curl_init();
 
        curl_setopt($ch, CURLOPT_URL, $url);
        
        curl_setopt($ch, CURLOPT_POST, true);
 
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        $result = curl_exec($ch);
        
		if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        curl_close($ch);
 
        return $result;
    }
}

?>