package fmt.febe.model;

import java.io.Serializable;


public class ChatUser implements Serializable {


    private String user_id;


    public ChatUser(String user_id) {

        this.user_id = user_id;

    }

    public String getId() {
        return user_id;
    }

}