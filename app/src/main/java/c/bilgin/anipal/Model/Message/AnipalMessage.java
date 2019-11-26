package c.bilgin.anipal.Model.Message;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AnipalMessage implements Serializable,MessageType {
    private String message,receiverUUID, messageUUID, senderUUID;
    private long sendDate;
    private boolean isRead;

    public AnipalMessage(){

    }

    public AnipalMessage(String receiverUUID,String senderUUID,String message){
        isRead = false;
        this.receiverUUID = receiverUUID;
        this.senderUUID = senderUUID;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}
