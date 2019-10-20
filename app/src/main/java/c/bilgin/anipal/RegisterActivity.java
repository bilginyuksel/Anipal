package c.bilgin.anipal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName,editTextSurname, editTextUsername, editTextPassword, editTextPasswordConfirmation;
    private CheckBox checkBoxContract;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
    }


    private void initialize(){
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = findViewById(R.id.editTextPasswordConfirmation);
        checkBoxContract = findViewById(R.id.checkBoxContract);
        buttonRegister = findViewById(R.id.buttonRegister);

    }
}
