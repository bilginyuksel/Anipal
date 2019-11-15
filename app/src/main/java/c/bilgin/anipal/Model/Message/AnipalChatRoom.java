package c.bilgin.anipal.Model.Message;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnipalChatRoom implements Serializable {
    private String userUUID;
    private String lastMessage, userFullname, userPhotoURL;
    private long lastMessageDate;
    private Map<String,AnipalMessage> messages;

    public AnipalChatRoom(String userUid,String userFullname,String userPhotoURL){
        userUUID = userUid;
        this.userFullname  = userFullname;
        this.userPhotoURL = userPhotoURL;
        lastMessage = "";
        messages = new HashMap<>();
        lastMessageDate = new Date().getTime();
    }

    public AnipalChatRoom(){

    }

    public String getUserUUID() {
        return userUUID;
    }

    public int getNotReadMessagesCount(){
        int count = 0;
        AnipalMessage[] m = new AnipalMessage[messages.size()];
        messages.values().toArray(m);
        for(int i = m.length-1;i>-1;i--) if(m[i].isRead())break; else ++count;
        return count;
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

    public String getUserPhotoURL() {
        return userPhotoURL;
    }


}
