package c.bilgin.anipal.Model.Message;

import java.util.Date;
import java.util.UUID;

public class AnipalMessage {
    private String message,receiverUUID, messageUUID;
    private long sendDate;
    private boolean isRead;

    public AnipalMessage(){

    }
    public AnipalMessage(String receiverUUID,String message){
        isRead = false;
        this.receiverUUID = receiverUUID;
        // this should not happen here.
        this.messageUUID = UUID.randomUUID().toString();
        this.message = message;
        sendDate = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public String getReceiverUUID() {
        return receiverUUID;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public long getSendDate() {
        return sendDate;
    }

    public boolean isRead() {
        return isRead;
    }
}
