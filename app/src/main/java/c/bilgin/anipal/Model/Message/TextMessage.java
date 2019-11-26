package c.bilgin.anipal.Model.Message;

public class TextMessage extends AnipalMessage {

    public TextMessage(){}
    public TextMessage(String receiverUUID, String senderUUID, String message) {
        super(receiverUUID, senderUUID, message);
    }

    @Override
    public int getMessageType() {
        return MessageType.TEXT_MESSAGE;
    }
}
