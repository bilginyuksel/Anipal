package c.bilgin.anipal.Model.Post;

public class AnipalPhotoPost extends AnipalAbstractPost{

    private String photoURL;

    public AnipalPhotoPost(String userUUID,String photoUrl) {
        super(userUUID);
        this.photoURL = photoUrl;
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
}
