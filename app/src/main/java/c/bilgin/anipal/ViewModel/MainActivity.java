package c.bilgin.anipal.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                String emailAddress = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Giriş Yapılıyor...");

                progressDialog.show();
                mAuth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userFuture(task.getResult().getUser().getUid());

                            // this progress bar almost never working.
                            progressDialog.dismiss();
                            Intent i = new Intent(MainActivity.this,NavigationActivity.class);
                            startActivity(i);
                        }else{
                            // if not success
                            System.out.println("Something went wrong !");
                            task.getException().printStackTrace();
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("CANCELLED");
                        System.out.println(databaseError.getDetails());
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
}
