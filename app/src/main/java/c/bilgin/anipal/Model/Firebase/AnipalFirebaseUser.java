package c.bilgin.anipal.Model.Firebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Future;

import c.bilgin.anipal.Model.User.AnipalUser;

public class AnipalFirebaseUser extends AnipalFirebase {

    private AnipalUser user;

    public AnipalFirebaseUser(Context context, String ref) {
        super(context, ref);
    }

    public Future<AnipalUser> findUser(String userUUID){
        user = new AnipalUser();

        getDatabaseReference().child(userUUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(AnipalUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return (Future<AnipalUser>) user;
    }
}
