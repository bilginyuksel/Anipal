package c.bilgin.anipal.Model.User;


import com.google.firebase.Timestamp;


import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;

public class AnipalUser implements AnipalCreateUser,AnipalUserController,AnipalUserLogin{
    private String userUUID,firstName,lastName,emailAddress,job,hobies,pet,citySchool;


    private Timestamp birthday;
    private List<String> followers,following;
    private AnipalUserLevel level; // number of levels
    private List<AnipalAbstractPost> posts;
    private AnipalCoin coin;
    // it goes like that... I dont know the exact profile informations
    private List<String> donations;
    private List<String> likedPosts;
    private boolean isActive ;

    @Override
    public AnipalUser login(String userUUID){
        // load anipal user from db.
        return this;
    }

    @Override
    public AnipalUser createUser(String userUUID,String emailAddress,String firstName,String lastName){

        // control isCreated situation...
        this.coin = new AnipalCoin(1000); // give 1000 anipal coin when account created or don't give any.
        // if there is a transformation on money like anipal to -> real money, don't give any anipal coin to user.
        this.level = new AnipalUserLevel(); // initialize at user creation

        // you can control it for email verification or if you start money process
        this.isActive = true;

        // Arrays
        this.posts = new ArrayList<>();
        this.donations = new ArrayList<>();
        this.likedPosts = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();

        // Store user informations
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.userUUID = userUUID;

        return this;
    }

    @Override
    public void followAnipalUser(AnipalUser anipalUser) {
        this.following.add(anipalUser.userUUID);
        // update firebase.
    }

    @Override
    public void unFollowAnipalUser(AnipalUser anipalUser) {
        this.following.remove(anipalUser.userUUID);
        // update firebase
    }



    public String getUserUUID() {
        return userUUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getJob() {
        return job;
    }

    public String getHobies() {
        return hobies;
    }

    public String getPet() {
        return pet;
    }

    public String getCitySchool() {
        return citySchool;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public AnipalUserLevel getLevel() {
        return level;
    }

    public List<AnipalAbstractPost> getPosts() {
        return posts;
    }

    public AnipalCoin getCoin() {
        return coin;
    }

    public List<String> getDonations() {
        return donations;
    }

    public List<String> getLikedPosts() {
        return likedPosts;
    }

    public boolean isActive() {
        return isActive;
    }
}
