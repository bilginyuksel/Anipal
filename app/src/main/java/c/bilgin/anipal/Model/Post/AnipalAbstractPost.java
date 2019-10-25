package c.bilgin.anipal.Model.Post;

import com.google.firebase.Timestamp;
import java.util.UUID;

import c.bilgin.anipal.Model.User.AnipalUser;


public abstract class AnipalAbstractPost implements ListItem{

    private String userUUID,postUUID;
    private Timestamp uploadTime,lastUpdateTime;
    private AnipalUser anipalUser;

    public AnipalAbstractPost(){

    }
    public AnipalAbstractPost(String userUUID){
        // Post creation
        this.userUUID = userUUID;
        this.uploadTime = Timestamp.now();
        this.lastUpdateTime = Timestamp.now();
        this.postUUID = UUID.randomUUID().toString();
    }

    public AnipalAbstractPost(AnipalAbstractPost post){
        this.userUUID = post.userUUID;
        this.postUUID = post.postUUID;
        this.uploadTime = post.uploadTime;
        // Update thing.
        this.lastUpdateTime = post.lastUpdateTime;

    }

    public AnipalAbstractPost delete(String postUUID){
        // delete post
        // this method common for all posts.
        return null;
    }


    public String getUserUUID() {
        return userUUID;
    }
    public String getPostUUID() {
        return postUUID;
    }
    public Timestamp getUploadTime() {
        return uploadTime;
    }
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

}
