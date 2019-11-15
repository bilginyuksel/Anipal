package c.bilgin.anipal.Model.Firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

import c.bilgin.anipal.Adapters.Post.AnipalPostAdapter;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.ViewModel.Account.MainActivity;


public class AnipalFirebase implements AnipalFirebasePosts{

    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private Context mContext;

    public AnipalFirebase(Context context,String ref){
        databaseReference = database.getReference(ref);
        mContext = context;
    }

    @Override
    public void publish(final AnipalAbstractPost post) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserPersonalPosts");
        databaseReference.child(post.getPostUUID()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Gönderi başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                ref.child(MainActivity.currentUser.getUserUUID()).child(post.getPostUUID()).setValue(post);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Gönderi oluştururken bir problem ile karşılaşıldı !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getPosts(final List<AnipalAbstractPost> posts, final AnipalPostAdapter adapter){

        // orderBy timestamp it is important!!!
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AnipalAbstractPost post;
                    if(snapshot.hasChild("photoURL")){
                        post = snapshot.getValue(AnipalPhotoPost.class);
                        // find user and add it to posts.add() you can use task for that.
                        post.setAnipalUser(adapter,post.getUserUUID());
                        //findUser(post,post.getUserUUID());
                        posts.add(post);
                    }else{
                        post = snapshot.getValue(AnipalDonationPost.class);
                        // find user and add it to posts.add() you can use task for that.
                        post.setAnipalUser(adapter,post.getUserUUID());
                        //findUser(post,post.getUserUUID());
                        posts.add(post);
                    }
                    adapter.notifyDataSetChanged();

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public Context getmContext() {
        return mContext;
    }

    private void findUser(final AnipalAbstractPost post, String uid){
        DatabaseReference r = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post.setUser(dataSnapshot.getValue(AnipalUser.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
