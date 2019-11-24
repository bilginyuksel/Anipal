package c.bilgin.anipal.Model.Message;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AnipalChatRoom implements Serializable {
    private String userUUID;
    private String lastMessage, userFullname, userPhotoURL;
    private long lastMessageDate;
    private int isReadCounter;
    private Map<String,AnipalMessage> messages;

    public AnipalChatRoom(String userUid,String userFullname,String userPhotoURL){
        userUUID = userUid;
        this.userFullname  = userFullname;
        this.userPhotoURL = userPhotoURL;
        lastMessage = "";
        messages = new HashMap<>();
        lastMessageDate = new Date().getTime();
        isReadCounter=0;
    }

    public AnipalChatRoom(){

    }

    public String getUserUUID() {
        return userUUID;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public long getLastMessageDate() {
        return lastMessageDate;
    }
    public Map<String,AnipalMessage> getMessages() {
        return messages;
    }
    public String getUserFullname() {
        return userFullname;
    }

    public int getIsReadCounter() {
        return isReadCounter;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }


}
