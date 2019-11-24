package c.bilgin.anipal.Model.Post;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnipalPhotoPost extends AnipalAbstractPost{

    private String photoURL, photoDescription;
    private List<String> likers;
    // User uid writing comment and comment.
    private HashMap<String,AnipalPostComment> comments;
    private int width,height;



    public AnipalPhotoPost(){
        likers = new ArrayList<>();
        comments = new HashMap<>();
    }

    public AnipalPhotoPost(String userUUID,String photoUrl,String photoDescription,int w,int h) {
        super(userUUID);
        this.photoURL = photoUrl;
        this.likers = new ArrayList<>();
        this.photoDescription = photoDescription;
        this.comments = new HashMap<>();
        width = w; height = h;
    }

    public AnipalPhotoPost(AnipalAbstractPost post) {
        super(post);
    }

    @Override
    public int getListItemType() {
        return ListItem.TYPE_PHOTO;
    }

    public String getPhotoURL() {
        return photoURL;
    }
    public HashMap<String, AnipalPostComment> getComments() { return comments; }
    public String getPhotoDescription() {
        return photoDescription;
    }
    public List<String> getLikers() {
        return likers;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
