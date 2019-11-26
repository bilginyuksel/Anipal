package c.bilgin.anipal.Model.Message;

public interface MessageType {
    int TEXT_MESSAGE = 1;
    int PHOTO_MESSAGE = -1;

    int getMessageType();
}
