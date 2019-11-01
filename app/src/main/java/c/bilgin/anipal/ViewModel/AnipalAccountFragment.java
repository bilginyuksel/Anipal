package c.bilgin.anipal.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import c.bilgin.anipal.CircleTransform;
import c.bilgin.anipal.R;


public class AnipalAccountFragment extends Fragment {

    private TextView textViewFullname, textViewLevel
            ,textViewStartedDonationCount, textViewFedAnimals, textViewFollowerCount
            ,textViewYear, textViewDayOfMonth, textViewMonthOfYear, textViewCurrentAnipalCoin
            ,textViewCitySchool, textViewJob, textViewPet, textViewHobies;
    private ImageView imageViewProfilePhoto;
    private ImageButton imageButtonProfileEdit;
    private ScrollView linearLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void initialize(){
        textViewFullname = linearLayout.findViewById(R.id.textViewFullname);
        textViewLevel = linearLayout.findViewById(R.id.textViewLevel);
        imageViewProfilePhoto = linearLayout.findViewById(R.id.imageViewProfilePhoto);
        textViewStartedDonationCount = linearLayout.findViewById(R.id.textViewStartedDonationCount);
        textViewFedAnimals = linearLayout.findViewById(R.id.textViewFedAnimals);
        textViewFollowerCount = linearLayout.findViewById(R.id.textViewFollowerCount);
        imageButtonProfileEdit = linearLayout.findViewById(R.id.imageButtonProfileEdit);
        textViewYear = linearLayout.findViewById(R.id.textViewYear);
        textViewMonthOfYear = linearLayout.findViewById(R.id.textViewMonthOfYear);
        textViewDayOfMonth = linearLayout.findViewById(R.id.textViewDayOfMonth);
        textViewCurrentAnipalCoin = linearLayout.findViewById(R.id.textViewCurrentAnipalCoin);
        textViewCitySchool = linearLayout.findViewById(R.id.textViewCitySchool);
        textViewJob = linearLayout.findViewById(R.id.textViewJob);
        textViewHobies = linearLayout.findViewById(R.id.textViewHobies);
        textViewPet = linearLayout.findViewById(R.id.textViewPet);


        // ----------------------------------------------------
        textViewFullname.setText(MainActivity.currentUser.getFirstName() + " "+MainActivity.currentUser.getLastName());
        textViewLevel.setText(MainActivity.currentUser.getLevel().getAnipalLevel().toString()+" Hayvansever");
        textViewCurrentAnipalCoin.setText(""+MainActivity.currentUser.getCoin().getCoin());
        Picasso.get().load(MainActivity.currentUser.getPhotoURL()).fit().transform(new CircleTransform()).into(imageViewProfilePhoto);
        textViewFollowerCount.setText(""+MainActivity.currentUser.getFollowers().size());
        // Update these values this values are complex.
        textViewStartedDonationCount.setText(""+MainActivity.currentUser.getPosts().size());
        textViewFedAnimals.setText(""+MainActivity.currentUser.getDonations().size());

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(MainActivity.currentUser.getBirthday().getTime());

        textViewDayOfMonth.setText(""+c.get(Calendar.DAY_OF_MONTH));
        textViewMonthOfYear.setText(""+c.get(Calendar.MONTH));
        textViewYear.setText(""+c.get(Calendar.YEAR));

        textViewPet.setText(MainActivity.currentUser.getPet()!=null?MainActivity.currentUser.getPet():"");
        textViewHobies.setText(MainActivity.currentUser.getHobies()!=null?MainActivity.currentUser.getHobies():"");
        textViewJob.setText(MainActivity.currentUser.getJob()!=null?MainActivity.currentUser.getJob():"");
        textViewCitySchool.setText(MainActivity.currentUser.getCitySchool()!=null?MainActivity.currentUser.getCitySchool():"");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_account,null);
        initialize();

        imageButtonProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do what do you want when edit profile
                // update profile
                AnipalEditProfileDialog dialog = new AnipalEditProfileDialog(getContext());
                dialog.show();
                /*MainActivity.currentUser.addFollower("iDizYltsWsdCY7TKxvvGxx7Iru82");
                MainActivity.currentUser.addFollower("tMDDuURrPKSWPIhoL0Dq7jHj3R02");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.currentUser.getUserUUID());
                ref.setValue(MainActivity.currentUser);*/
            }
        });

        return linearLayout;
    }
}
