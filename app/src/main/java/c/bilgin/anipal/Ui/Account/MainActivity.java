package c.bilgin.anipal.Ui.Account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileManager;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import c.bilgin.anipal.Model.User.AnipalCreateUser;
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
    private ImageButton btnFacebook,btnTwitter,btnGoogle;
    private TextView textViewForgotPassword;
    private Button buttonLogin;
    public static AnipalUser currentUser = new AnipalUser();
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;
    private final static int GOOGLE_SIGN_IN = 11;
    public static GoogleSignInClient mGoogleSignInClient;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FACEBOOK LOGIN CONFIGURATION
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        // FACEBOOK LOGIN CONFIGURATION

        // GOOGLE LOGIN CONFIGURATION
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        // GOOGLE LOGIN CONFIGURATION

        sharedPreferences =this.getPreferences(Context.MODE_PRIVATE);
        String email =sharedPreferences.getString("Email","abipal@gmail.com");
        String password = sharedPreferences.getString("Password","0000000");

        if(!email.equals("abipal@gmail.com")){
            Intent i = new Intent(MainActivity.this, SplashScreen.class);
            i.putExtra("Email",email);
            i.putExtra("Password",password);
            i.putExtra("LogType","normal");
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
                prepareLoginProgress();

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


        // CONTROL FACEBOOK AND FACEBOOK LOGIN BUTTON
        //***************************************
        if(controlFacebookLogin()){
            // if facebook logged in...
            // enter application
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        }

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackManager = CallbackManager.Factory.create();
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("email");
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,permissions);
                facebookLogin();
            }
        });
        //***************************************
        // CONTROL FACEBOOK AND FACEBOOK LOGIN BUTTON


        // CONTROL GOOGLE AND GOOGLE LOGIN BUTTON
        // **************************************
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
        // **************************************
        // CONTROL GOOGLE AND GOOGLE LOGIN BUTTON

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Twitter girişi şuanda kullanılamıyor. Lütfen google veya facebook hizmetini kullanınız veya manuel kayıt olunuz.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void outsideLoginFirebaseSync(final AnipalUser u){
        databaseReference.child(u.getUserUUID()).setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userFuture(u.getUserUUID());
            }
        });
    }

    private void prepareLoginProgress() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Giriş Yapılıyor...");

        progressDialog.show();
    }

    // FACEBOOK AUTHENTICATION
    // ***********************
    private void handleFacebookAccessToken(final AccessToken token){

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println("Display Name : "+user.getDisplayName());
                            getFbDetails(token);

                        } else {
                            // If sign in fails, display a message to the user.
                            task.getException().printStackTrace();
                        }

                    }
                });

    }
    private void facebookLogin(){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        prepareLoginProgress();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    private boolean controlFacebookLogin(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return  isLoggedIn;
    }
    private void getFbDetails(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        //   Toast.makeText(Login.this, object.toString(), Toast.LENGTH_LONG).show();
                        Log.v("FB Details", object.toString());
                        if (object != null) {
                            String name_fb = object.optString("name");
                            String email_fb = object.optString("email");
                            String first_name = object.optString("first_name");
                            String last_name = object.optString("last_name");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Date d = new Date();
                            d.setTime(user.getMetadata().getCreationTimestamp());

                            outsideLoginFirebaseSync(new AnipalUser().createUser(user.getUid(),email_fb,
                                    first_name,last_name,
                                    null,d));

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name, last_name, email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }
    // ***************************
    // FACEBOOK AUTHENTICATION

    // GOOGLE AUTHENTICATION
    //***********************
    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,GOOGLE_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Google sign-in success.
                            FirebaseUser user = task.getResult().getUser();
                            Date d = new Date();
                            d.setTime(user.getMetadata().getCreationTimestamp());

                            outsideLoginFirebaseSync(new AnipalUser().createUser(user.getUid(),account.getEmail(),
                                    account.getGivenName(),account.getFamilyName(),
                                    null,d));

                        }else{
                            // If signIn failes, display a message to the user.
                            Toast.makeText(MainActivity.this, "Giriş başarısız.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // **************************
    // GOOGLE AUTHENTICATION
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
        btnFacebook = findViewById(R.id.btnFacebook);
        btnTwitter = findViewById(R.id.btnTwitter);
        btnGoogle = findViewById(R.id.btnGoogle);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();

        // logout
        // mGoogleSignInClient.signOut();
        // LoginManager.getInstance().logOut();
        // mAuth.signOut();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent i = new Intent(MainActivity.this,SplashScreen.class);
            i.putExtra("LogType","other");
            startActivity(i);
        }
        else System.out.println("No user signed...");
    }


    @Override
    public void onBackPressed() {
        // do nothing. it won't work
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // google on activity result
        if (requestCode == GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                prepareLoginProgress();
                // Google signin was succesfull, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                System.out.println("GOOGLE SIGN IN FAILED !");
                e.printStackTrace();
            }
            return;
        }else{
            // facebook on activity result
            callbackManager.onActivityResult(requestCode,resultCode,data);
            return;
        }
    }
}
