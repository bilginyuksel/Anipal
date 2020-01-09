package c.bilgin.anipal.Ui.Account;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName,editTextSurname, editTextEmail, editTextPassword
            , editTextPasswordConfirmation;
    private CheckBox checkBoxContract;
    private Button buttonRegister;
    private TextView buttonBirthday,textViewContract;
    private AnipalUser anipalUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Date birthday;
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
                final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setTitle("Kayıt");
                dialog.setMessage("Kullanıcı oluşturuluyor...");
                if(isPasswordConfirmed(password,passwordConfirmation) && isContractChecked){
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                System.out.println("All good create user at firebase");
                                // create anipal user and store information at Firebase.

                                // don't get photourl here !!!!!! It is important to create 2 constrctros.
                                anipalUser = new AnipalUser().createUser(task.getResult().getUser().getUid(),emailAddress,firstName,lastName,"blank",birthday);
                                createUserAtFirebase(anipalUser);
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Kullanıcı başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                                // When user created use intent and register activity!
                                Intent i =new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                            }else{
                                System.out.println("Something went wrong use text view or some kind of thing");
                                Toast.makeText(RegisterActivity.this, "Kullanıcı oluşturulamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    if(!isContractChecked)
                    Toast.makeText(RegisterActivity.this, "Lütfen sözleşmeyi imzalayınız. ", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(RegisterActivity.this, "Şifreniz 6 veya daha fazla karakterden oluşmalıdır.Büyük, küçük harf hassasiyetine dikkat ederek şifrelerinizi tekrar yazınız.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new Dialog(RegisterActivity.this);
                d.setContentView(R.layout.dialog_user_contract);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
            }
        });
    }

    private boolean isPasswordConfirmed(String pass1,String pass2){
        if(pass1.length()<6 || pass2.length()<6) return false;
        return pass1.equals(pass2);
    }

    private void initialize(){
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonBirthday = findViewById(R.id.buttonBirthday);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = findViewById(R.id.editTextPasswordConfirmation);
        checkBoxContract = findViewById(R.id.checkBoxContract);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewContract = findViewById(R.id.textViewContract);

        mAuth = FirebaseAuth.getInstance();
    }

    private void createUserAtFirebase(AnipalUser anipalUser){
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(anipalUser.getUserUUID()).setValue(anipalUser);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openDatePicker(View v){
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                System.out.println("Year : "+i);
                System.out.println("Month : "+i1);
                System.out.println("Day : "+i2);
                c.set(i,i1,i2,19,0);
                birthday = c.getTime();
                System.out.println(birthday.toString());
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                buttonBirthday.setText(""+format.format(birthday));

            }
        });
        dialog.show();
    }
}
