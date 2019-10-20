package c.bilgin.anipal.Model.Post;

import java.util.TimeZone;

public abstract class AnipalAbstractPost implements AnipalPostController{
    private String userUUID,postUUID;
    private TimeZone uploadTime,updateTime;

    public AnipalAbstractPost delete(String postUUID){
        // delete post
        return null;
    }
}
