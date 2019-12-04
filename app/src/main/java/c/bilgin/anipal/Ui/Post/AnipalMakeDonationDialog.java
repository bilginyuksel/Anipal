package c.bilgin.anipal.Ui.Post;

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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.Model.User.NoMoneyException;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalMakeDonationDialog extends Dialog {

    private Context mContext;
    private ImageButton imageButtonCloseDialog;
    private Button buttonMakeDonation;
    private AnipalDonationPost post;
    private EditText editTextDonationQuantity;

    public AnipalMakeDonationDialog(@NonNull Context context, AnipalDonationPost post) {
        super(context);
        this.mContext = context;
        this.post = post;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_make_donation);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = metrics.widthPixels;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // initialization
        imageButtonCloseDialog = findViewById(R.id.imageButtonCloseDialog);
        buttonMakeDonation = findViewById(R.id.buttonMakeDonation);
        editTextDonationQuantity = findViewById(R.id.editTextDonationQuantity);




        imageButtonCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        buttonMakeDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make the donation
                // Update donation post!!!
                // You have to write listener for the donation post or changings.
                // Look at the firebase functions you wrote a listener for create but i think it is necessary for
                // everything because when you try to update donation you have to update for all !!!!
                int quantity = Integer.parseInt(editTextDonationQuantity.getText().toString());
                int maxQuantity = post.getDonationPrice();
                int currentDonationQuantity = post.getCurrentDonation();

                if((quantity+currentDonationQuantity)>maxQuantity || MainActivity.currentUser.getCoin().getCoin()<quantity){
                    dismiss();
                    Toast.makeText(mContext, "Üzgünüz bağış yapamazsınız.", Toast.LENGTH_SHORT).show();
                }
                else {
                    AnipalHomeFragment.postAdapter.notifyDataSetChanged();
                    try{
                        MainActivity.currentUser.makeDonation(quantity);
                        Toast.makeText(mContext, "Bağışınız kontrole alındı.", Toast.LENGTH_SHORT).show();

                        Map<String,Object> map = new HashMap<>();
                        map.put("donations/"+post.getPostUUID(),quantity);
                        map.put("coin/",MainActivity.currentUser.getCoin());

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(MainActivity.currentUser.getUserUUID()).updateChildren(map);

                    }catch (NoMoneyException e){
                        Toast.makeText(mContext, "Yeterli paranız yok.", Toast.LENGTH_SHORT).show();
                    }

                    dismiss();
                }



            }
        });

    }


}
