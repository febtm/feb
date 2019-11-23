package fmt.febe.model;


import java.io.Serializable;


public class ChatRoom implements Serializable {


    private String chatroom_id, user1_id, user2_id, user1_name, user2_name,
            user1_online, user2_online, user1_read, user2_read, timestamp;


    public ChatRoom() {
    }


    public String getId() {
        return chatroom_id;
    }

    public void setId(String id) {
        this.chatroom_id = id;
    }


    public String getUser1_id() {
        return user1_id;
    }

    public void setUser1_id(String user1_id) {
        this.user1_id = user1_id;
    }


    public String getUser2_id() {
        return user2_id;
    }

    public void setUser2_id(String user2_id) {
        this.user2_id = user2_id;
    }


    public String getUser1_name() {
        return user1_name;
    }

    public void setUser1_name(String user1_name) {
        this.user1_name = user1_name;
    }


    public String getUser2_name() {
        return user2_name;
    }

    public void setUser2_name(String user2_name) {
        this.user2_name = user2_name;
    }


    public String getUser1_online() {
        return user1_online;
    }

    public void setUser1_online(String user1_online) {
        this.user1_online = user1_online;
    }


    public String getUser2_online() {
        return user2_online;
    }

    public void setUser2_online(String user2_online) {
        this.user2_online = user2_online;
    }


    public String getUser1_read() {
        return user1_read;
    }

    public void setUser1_read(String user1_read) {
        this.user1_read = user1_read;
    }


    public String getUser2_read() {
        return user2_read;
    }

    public void setUser2_read(String user2_read) {
        this.user2_read = user2_read;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}