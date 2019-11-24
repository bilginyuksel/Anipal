package c.bilgin.anipal.Model.Message;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AnipalMessage implements Serializable {
    private String message,receiverUUID, messageUUID, senderUUID;
    private long sendDate;
    private boolean isRead;
    private MessageType type;

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
        this.type = MessageType.TEXT_MESSAGE;
    }

    public AnipalMessage(String receiverUUID, String senderUUID, String message, MessageType type){
        this(receiverUUID,senderUUID,message);
        this.type = type;
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

    public MessageType getType() {
        return type;
    }

    public String getSenderUUID() {
        return senderUUID;
    }
}
