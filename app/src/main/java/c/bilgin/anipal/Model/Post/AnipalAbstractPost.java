package c.bilgin.anipal.Model.Post;

import android.renderscript.Sampler;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Date;
import java.util.UUID;

import c.bilgin.anipal.Adapters.Post.AnipalPostAdapter;
import c.bilgin.anipal.Model.User.AnipalUser;


public abstract class AnipalAbstractPost implements ListItem{

    private String userUUID,postUUID;
    private AnipalUser anipalUser;
    private long timestamp,lastUpdateTime;
    //private long timestamp;

    public AnipalAbstractPost(){

    }


    public AnipalAbstractPost(String userUUID){
        // Post creation
        this.userUUID = userUUID;
        this.lastUpdateTime = new Date().getTime();
        this.postUUID = UUID.randomUUID().toString();
        this.timestamp = new Date().getTime();
        //this.timestamp = Timestamp.now().toDate().getTime();
    }

    public AnipalAbstractPost(AnipalAbstractPost post){
        this.userUUID = post.userUUID;
        this.postUUID = post.postUUID;
        // Update thing.
        this.lastUpdateTime = post.lastUpdateTime;
        this.timestamp = post.timestamp;
        //this.timestamp = post.timestamp;

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
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AnipalUser getAnipalUser() {
        return anipalUser;
    }
    /*public long getTimestamp() {
        return timestamp;
    }*/

    public void setUser(AnipalUser u){
        this.anipalUser = u;
    }
    public void setAnipalUser(final AnipalPostAdapter adapter,final String uid) {
        // I didn't like the usage !!!
        // But i think i have to do that right now. :(
        anipalUser = new AnipalUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                anipalUser = dataSnapshot.getValue(AnipalUser.class);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void findUser(final String uid){
        // anipalUser = new AnipalUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        anipalUser = dataSnapshot.getValue(AnipalUser.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
