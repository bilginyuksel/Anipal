package c.bilgin.anipal.Model.Message;

public class PhotoMessage extends AnipalMessage {

    private String photoURL;
    private int width,height;

    public PhotoMessage(){}

    public PhotoMessage(String receiverUUID, String senderUUID, String message,int width,int height) {
        super(receiverUUID, senderUUID, message);
        this.photoURL = message;
        this.width = width; this.height = height;
    }

    @Override
    public int getMessageType() {
        return MessageType.PHOTO_MESSAGE;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPhotoURL() {
        return photoURL;
    }
}
