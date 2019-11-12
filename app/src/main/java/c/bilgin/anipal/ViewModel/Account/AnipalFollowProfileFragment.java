package c.bilgin.anipal.ViewModel.Account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalFollowProfileFragment extends Fragment {

    private Context mContext;
    private TextView textViewFullname
            ,textViewStartedDonationCount, textViewFedAnimals, textViewFollowerCount
            ,textViewYear, textViewDayOfMonth, textViewMonthOfYear
            ,textViewCitySchool, textViewJob, textViewPet, textViewHobies;
    private CircularImageView imageViewProfilePhoto;
    private ImageButton imageButtonProfileEdit;
    private ScrollView linearLayout;
    private Button buttonSendMessage, buttonFollowUser;
    private AnipalUser user;


    public AnipalFollowProfileFragment(AnipalUser u){

        this.user = u;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout bla bla
        linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_follow_profile,null);
        initialize();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the messaging activity with the user.
            }
        });

        buttonFollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // follow user. easy commands.
            }
        });

        return linearLayout;
    }


    private void initialize(){
        textViewFullname = linearLayout.findViewById(R.id.textViewFullname);
        imageViewProfilePhoto = linearLayout.findViewById(R.id.imageViewProfilePhoto);
        textViewStartedDonationCount = linearLayout.findViewById(R.id.textViewStartedDonationCount);
        textViewFedAnimals = linearLayout.findViewById(R.id.textViewFedAnimals);
        textViewFollowerCount = linearLayout.findViewById(R.id.textViewFollowerCount);
        imageButtonProfileEdit = linearLayout.findViewById(R.id.imageButtonProfileEdit);
        textViewYear = linearLayout.findViewById(R.id.textViewYear);
        textViewMonthOfYear = linearLayout.findViewById(R.id.textViewMonthOfYear);
        textViewDayOfMonth = linearLayout.findViewById(R.id.textViewDayOfMonth);
        textViewCitySchool = linearLayout.findViewById(R.id.textViewCitySchool);
        textViewJob = linearLayout.findViewById(R.id.textViewJob);
        textViewHobies = linearLayout.findViewById(R.id.textViewHobies);
        textViewPet = linearLayout.findViewById(R.id.textViewPet);
        buttonFollowUser = linearLayout.findViewById(R.id.buttonFollowUser);
        buttonSendMessage = linearLayout.findViewById(R.id.buttonSendMessage);

        // ----------------------------------------------------
        textViewFullname.setText(user.getFirstName() + " "+user.getLastName());
        if(user.getPhotoURL() != null)
            Picasso.get().load(user.getPhotoURL()).fit().into(imageViewProfilePhoto);

        textViewFollowerCount.setText(""+user.getFollowers().size());
        // Update these values this values are complex.
        textViewStartedDonationCount.setText(""+user.getPosts().size());
        textViewFedAnimals.setText(""+user.getDonations().size());

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(user.getBirthday().getTime());

        textViewDayOfMonth.setText(""+c.get(Calendar.DAY_OF_MONTH));
        textViewMonthOfYear.setText(""+c.get(Calendar.MONTH));
        textViewYear.setText(""+c.get(Calendar.YEAR));

        textViewPet.setText(user.getPet()!=null?user.getPet():"");
        textViewHobies.setText(user.getHobies()!=null?user.getHobies():"");
        textViewJob.setText(user.getJob()!=null?user.getJob():"");
        textViewCitySchool.setText(user.getCitySchool()!=null?user.getCitySchool():"");


        // if you are following that account highlight button and right 'You are following'
        if(MainActivity.currentUser.getFollowing().contains(user.getUserUUID()))
            buttonFollowUser.setText("Takibi Bırak");

    }

}
