package c.bilgin.anipal.Model.User;

import java.util.List;
import java.util.TimeZone;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;

public class AnipalUser {
    private String userUUID,firstName,lastName,emailAddress,username,job,hobies,pet,citySchool;
    private TimeZone birthday;
    private List<String> followers,following;
    private AnipalUserLevel level; // number of levels
    private int anipalUserLevel; // this is like 3000/4000...
    private List<AnipalAbstractPost> posts;
    private AnipalCoin coin;
    // it goes like that... I dont know the exact profile informations
    private List<String> donations;
    private List<String> likedPosts;
    private boolean isCreated ;

    public AnipalUser(){
        if(!isCreated)
            coin = new AnipalCoin(1000); // give some coins on user creation.
    }
}
