package c.bilgin.anipal.Model.Post;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AnipalPostComment implements Serializable, Comparable<AnipalPostComment> {
    private String comment, senderUUID, postUUID, senderName, receiverUUID, commentUUID;
    private long timestamp;

    public AnipalPostComment(String postUUID,String senderUUID,String receiverUUID, String senderName,String comment){
        this.postUUID = postUUID;
        this.senderUUID = senderUUID;
        this.comment = comment;
        this.commentUUID = UUID.randomUUID().toString();
        this.timestamp = new Date().getTime();
        this.senderName = senderName;
        this.receiverUUID = receiverUUID;
    }

    public AnipalPostComment(){}

    public String getSenderUUID() {
        return senderUUID;
    }

    public String getSenderName() {
        return senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getReceiverUUID() {
        return receiverUUID;
    }

    public String getCommentUUID() {
        return commentUUID;
    }

    public String getComment() {
        return comment;
    }

    public String getPostUUID() {
        return postUUID;
    }

    @Override
    public int compareTo(AnipalPostComment anipalPostComment) {
        return (int)(this.getTimestamp()-anipalPostComment.getTimestamp());
    }
}
