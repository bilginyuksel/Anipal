package c.bilgin.anipal.Ui.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Message.AnipalMessagesFragment;
import c.bilgin.anipal.Ui.NavigationActivity;
import c.bilgin.anipal.Ui.Post.AnipalHomeFragment;
import c.bilgin.anipal.Ui.SplashScreen;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayoutRegister;
    // Change username information to email information.
    private EditText editTextUsername,editTextPassword;
    private TextView textViewForgotPassword;
    private Button buttonLogin;
    public static AnipalUser currentUser = new AnipalUser();
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences =this.getPreferences(Context.MODE_PRIVATE);
        String email =sharedPreferences.getString("Email","abipal@gmail.com");
        String password = sharedPreferences.getString("Password","0000000");

        if(!email.equals("abipal@gmail.com")){
            Intent i = new Intent(MainActivity.this, SplashScreen.class);
            i.putExtra("Email",email);
            i.putExtra("Password",password);
            startActivity(i);
        }

        initialize();

        linearLayoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Register button open register activity
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnipalForgotPasswordDialog dialog = new AnipalForgotPasswordDialog(MainActivity.this);
                dialog.show();
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailAddress = editTextUsername.getText().toString();
                final String password = editTextPassword.getText().toString();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Giriş Yapılıyor...");

                progressDialog.show();
                mAuth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userFuture(task.getResult().getUser().getUid());
                            editor =sharedPreferences.edit();
                            editor.putString("Email",emailAddress);
                            editor.putString("Password",password);
                            editor.commit();
                            // this progress bar almost never working.
                            // progressDialog.dismiss();

                        }else{
                            // if not success
                            System.out.println("Something went wrong !");
                            task.getException().printStackTrace();
                            Toast.makeText(MainActivity.this, "Kullanıcı adı veya şifre yanlış.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }


    private void userFuture(String uid) {
        databaseReference.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if it shows problem
                        // use progress bar when data loading.
                        // because of fastly opening profile!!!
                        currentUser = dataSnapshot.getValue(AnipalUser.class);
                        updateMessageToken(currentUser.getUserUUID());
                        progressDialog.dismiss();
                        AnipalMessagesFragment.getInstance();
                        AnipalHomeFragment.getInstance();
                        Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                        startActivity(i);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("CANCELLED");
                        System.out.println(databaseError.getDetails());
                    }
                });
    }

    private void updateMessageToken(final String uid){
        final Map<String,Object> map = new HashMap<>();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                map.put("messageToken",task.getResult().getToken());
                databaseReference.child(uid).updateChildren(map);
            }
        });
    }

    private void initialize(){
        linearLayoutRegister = findViewById(R.id.linearLayoutRegister);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
    }


    @Override
    public void onBackPressed() {
        // do nothing. it won't work
    }
}
