package fmt.febe.model;

import java.io.Serializable;


public class ChatUser implements Serializable {


    private String user_id, username;


    public ChatUser(String user_id, String username) {

        this.user_id = user_id;
        this.username = username;
    }

    public String getId() {
        return user_id;
    }

    public String getUser_name() {
        return username;
    }

}