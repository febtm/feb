package fmt.febe.model;


public class Notification {


    private String id, chat_id, post_userread, notify_userid, notify_username, timestamp;


    public String getId() {
        return id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getPost_userread() {
        return post_userread;
    }

    public String getNotify_userid() {
        return notify_userid;
    }

    public String getNotify_username() {
        return notify_username;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public void setNotDetails(String id, String chat_id, String post_userread, String notify_userid, String notify_username,
                              String timestamp) {

        this.id = id;
        this.chat_id = chat_id;
        this.post_userread = post_userread;
        this.notify_userid = notify_userid;
        this.notify_username = notify_username;
        this.timestamp = timestamp;

    }

    public void deleteNot() {

        this.id = null;
        this.chat_id = null;
        this.post_userread = null;
        this.notify_userid = null;
        this.notify_username = null;
        this.timestamp = null;

    }

}