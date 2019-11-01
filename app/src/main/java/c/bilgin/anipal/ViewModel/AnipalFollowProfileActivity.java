package c.bilgin.anipal.ViewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import c.bilgin.anipal.CircleTransform;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalFollowProfileActivity extends AppCompatActivity {

    private TextView textViewFullname, textViewLevel
            ,textViewStartedDonationCount, textViewFedAnimals, textViewFollowerCount
            ,textViewYear, textViewDayOfMonth, textViewMonthOfYear, textViewCurrentAnipalCoin;
    private ImageView imageViewProfilePhoto;
    private ImageButton imageButtonProfileEdit;
    private ScrollView linearLayout;
    public static AnipalUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_follow_profile);

        initialize();
    }

    private void initialize(){
        textViewFullname = findViewById(R.id.textViewFullname);
        textViewLevel = findViewById(R.id.textViewLevel);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        textViewStartedDonationCount = findViewById(R.id.textViewStartedDonationCount);
        textViewFedAnimals = findViewById(R.id.textViewFedAnimals);
        textViewFollowerCount = findViewById(R.id.textViewFollowerCount);
        imageButtonProfileEdit = findViewById(R.id.imageButtonProfileEdit);
        textViewYear = findViewById(R.id.textViewYear);
        textViewMonthOfYear = findViewById(R.id.textViewMonthOfYear);
        textViewDayOfMonth = findViewById(R.id.textViewDayOfMonth);
        textViewCurrentAnipalCoin = findViewById(R.id.textViewCurrentAnipalCoin);



        user = new AnipalUser();
        // ----------------------------------------------------

        textViewFullname.setText(user.getFirstName() + " "+user.getLastName());
        // textViewLevel.setText(user.getLevel().getAnipalLevel().toString()+" Hayvansever");
        // textViewCurrentAnipalCoin.setText(""+user.getCoin().getCoin());
        Picasso.get().load(user.getPhotoURL()).fit().transform(new CircleTransform()).into(imageViewProfilePhoto);
        textViewFollowerCount.setText(""+user.getFollowers().size());
        // Update these values this values are complex.
        textViewStartedDonationCount.setText(""+user.getPosts().size());
        textViewFedAnimals.setText(""+user.getDonations().size());

        Calendar c = Calendar.getInstance();
        // c.setTimeInMillis(user.getBirthday().getTime());

        textViewDayOfMonth.setText(""+c.get(Calendar.DAY_OF_MONTH));
        textViewMonthOfYear.setText(""+c.get(Calendar.MONTH));
        textViewYear.setText(""+c.get(Calendar.YEAR));
    }
}
