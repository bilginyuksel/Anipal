package c.bilgin.anipal.Model.Firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import c.bilgin.anipal.Model.Post.AnipalAbstractPost;

public class AnipalFirebase implements AnipalFirebasePosts{

    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private Context mContext;

    public AnipalFirebase(Context context,String ref){
        databaseReference = database.getReference(ref);
        mContext = context;
    }

    @Override
    public void publish(AnipalAbstractPost post) {
        databaseReference.child(post.getPostUUID()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Bağış barı başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Bağış barı oluştururken bir problem ile karşılaşıldı !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public Context getmContext() {
        return mContext;
    }
}
