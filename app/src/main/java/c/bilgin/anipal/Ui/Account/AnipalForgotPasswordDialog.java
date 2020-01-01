package c.bilgin.anipal.Ui.Account;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import c.bilgin.anipal.R;

public class AnipalForgotPasswordDialog extends Dialog {

    private Context mContext;
    private FirebaseAuth mAuth;
    private EditText etEmail1, etEmail2;
    private Button btnSend;

    public AnipalForgotPasswordDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_forgot_password);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = metrics.widthPixels;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAuth = FirebaseAuth.getInstance();
        etEmail1 = findViewById(R.id.email1);
        etEmail2 = findViewById(R.id.email2);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mAuth.sendPasswordResetEmail(etEmail1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "Şifre değiştirme emaili e-postanıza başarıyla gönderildi.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(mContext, "Yanlış giden birşeyler var, lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
