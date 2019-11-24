package c.bilgin.anipal.Ui.Account;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import c.bilgin.anipal.R;

public class AnipalEditProfileDialog extends Dialog {

    private Context mContext;
    private ImageButton imageButtonCloseDialog;
    private Button buttonSaveProfileInformations;
    private EditText editTextJob, editTextHobies, editTextCitySchool, editTextPet
            ,editTextDayOfMonth, editTextMonthOfYear, editTextYear;


    public AnipalEditProfileDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_edit_profile);

        // initialization
        imageButtonCloseDialog = findViewById(R.id.imageButtonCloseDialog);
        buttonSaveProfileInformations = findViewById(R.id.buttonSaveProfileInformations);
        editTextDayOfMonth = findViewById(R.id.editTextDayOfMonth);
        editTextMonthOfYear = findViewById(R.id.editTextMonthOfYear);
        editTextYear = findViewById(R.id.editTextYear);
        editTextCitySchool = findViewById(R.id.editTextCitySchool);
        editTextPet = findViewById(R.id.editTextPet);
        editTextHobies = findViewById(R.id.editTextHobies);
        editTextJob = findViewById(R.id.editTextJob);


        Date d  = MainActivity.currentUser.getBirthday();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(d.getTime());
        editTextDayOfMonth.setText(""+c.get(Calendar.DAY_OF_MONTH));
        editTextMonthOfYear.setText(""+c.get(Calendar.MONTH));
        editTextYear.setText(""+c.get(Calendar.YEAR));

        // Fill birthday too !
        editTextJob.setText(MainActivity.currentUser.getJob());
        editTextHobies.setText(MainActivity.currentUser.getHobies());
        editTextCitySchool.setText(MainActivity.currentUser.getCitySchool());
        editTextPet.setText(MainActivity.currentUser.getPet());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageButtonCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonSaveProfileInformations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save profile informations here.
                // By the way when you are opening the dialog load the profile contents
                // if they are not empty!
                MainActivity.currentUser.setCitySchool(editTextCitySchool.getText().toString());
                MainActivity.currentUser.setHobies(editTextHobies.getText().toString());
                MainActivity.currentUser.setPet(editTextPet.getText().toString());
                MainActivity.currentUser.setJob(editTextJob.getText().toString());

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(MainActivity.currentUser.getUserUUID()).setValue(MainActivity.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(mContext, "Profil bilgileri başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dismiss();
            }
        });
    }
}
