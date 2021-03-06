package c.bilgin.anipal.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Message.AnipalMessagesFragment;
import c.bilgin.anipal.Ui.Post.AnipalHomeFragment;

import static c.bilgin.anipal.Ui.Account.MainActivity.currentUser;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIMEOUT =2500;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Intent infoIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initialize();

    }


    private void initialize(){
        infoIntent =getIntent();
        String logType = infoIntent.getStringExtra("LogType");

        switch (logType){
            case "other":
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                findUser(user.getUid());
                break;
            case "normal":
                String email = infoIntent.getStringExtra("Email");
                String password = infoIntent.getStringExtra("Password");

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // find the user information here.
                            FirebaseUser u = task.getResult().getUser();
                            findUser(u.getUid());
                        }
                    }
                });
                break;
        }


    }

    private void updateMessageToken(final String uid){
        final Map<String,Object> map = new HashMap<>();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                map.put("messageToken",task.getResult().getToken());
                FirebaseDatabase.getInstance().getReference("Users").child(uid).updateChildren(map);
            }
        });
    }


    private void findUser(String uid) {
        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if it shows problem
                        // use progress bar when data loading.
                        // because of fastly opening profile!!!
                        currentUser = dataSnapshot.getValue(AnipalUser.class);
                        updateMessageToken(currentUser.getUserUUID());
                        //progressDialog.dismiss();
                        AnipalMessagesFragment.getInstance();
                        AnipalHomeFragment.getInstance();
                        Intent i = new Intent(SplashScreen.this, NavigationActivity.class);
                        startActivity(i);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("CANCELLED");
                        System.out.println(databaseError.getDetails());
                    }
                });
    }

}
