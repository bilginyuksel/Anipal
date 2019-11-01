package c.bilgin.anipal.Model.User;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AnipalUser implements AnipalCreateUser,AnipalUserController,AnipalUserLogin{

    private String userUUID,firstName,lastName,emailAddress,job,hobies,pet,citySchool,photoURL;
    private Date birthday,registerDate,lastLoginDate;
    private List<String> followers,following;
    private AnipalUserLevel level; // number of levels
    private List<String> posts;
    private AnipalCoin coin;
    // it goes like that... I dont know the exact profile informations
    private List<String> donations;
    private List<String> likedPosts;
    private boolean isActive ;


    public AnipalUser(){
        donations = new ArrayList<>();
        likedPosts =new ArrayList<String>();
        posts = new ArrayList<>();
        followers = new ArrayList<>();
        following = new ArrayList<>();
    }

    @Override
    public AnipalUser login(String userUUID){
        // load anipal user from db.
        return this;
    }

    @Override
    public AnipalUser createUser(String userUUID,String emailAddress,String firstName
            ,String lastName,String photoURL,Date birthday){

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
        this.photoURL = photoURL;
        this.birthday = birthday;
        this.registerDate = Timestamp.now().toDate();

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
    public Date getBirthday() {
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
    public List<String> getPosts() {
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
    public String getPhotoURL() { return photoURL;}
    public Date getRegisterDate() { return registerDate; }
    public void addFollower(String followerUUID){ this.followers.add(followerUUID); }
    public int spendAnipalCoin(int c){
        return c>coin.getCoin()?-1:coin.spendCoin(c);
    }
    public void setJob(String job) {
        this.job = job;
    }

    public void setCitySchool(String citySchool) {
        this.citySchool = citySchool;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public void setHobies(String hobies) {
        this.hobies = hobies;
    }
}
