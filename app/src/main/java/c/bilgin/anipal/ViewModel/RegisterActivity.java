package c.bilgin.anipal.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName,editTextSurname, editTextEmail, editTextPassword, editTextPasswordConfirmation;
    private CheckBox checkBoxContract;
    private Button buttonRegister;
    private AnipalUser anipalUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailAddress = editTextEmail.getText().toString();
                final String firstName = editTextName.getText().toString();
                final String lastName = editTextSurname.getText().toString();
                // check if password's confirmed or not !
                final String password = editTextPassword.getText().toString();
                final String passwordConfirmation = editTextPasswordConfirmation.getText().toString();
                // is contract checked
                boolean isContractChecked = checkBoxContract.isChecked();

                // firebase authentication
                if(isPasswordConfirmed(password,passwordConfirmation) && isContractChecked){
                    mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                System.out.println("All good create user at firebase");
                                // create anipal user and store information at Firebase.
                                anipalUser = new AnipalUser().createUser(task.getResult().getUser().getUid(),emailAddress,firstName,lastName);
                                createUserAtFirebase(anipalUser);
                                // When user created use intent and register activity!
                                Intent i =new Intent(RegisterActivity.this,NavigationActivity.class);
                                startActivity(i);
                            }else{
                                System.out.println("Something went wrong use text view or some kind of thing");
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isPasswordConfirmed(String pass1,String pass2){
        return pass1.equals(pass2);
    }

    private void initialize(){
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = findViewById(R.id.editTextPasswordConfirmation);
        checkBoxContract = findViewById(R.id.checkBoxContract);
        buttonRegister = findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();
    }

    private void createUserAtFirebase(AnipalUser anipalUser){
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(anipalUser.getUserUUID()).setValue(anipalUser);
    }
}
