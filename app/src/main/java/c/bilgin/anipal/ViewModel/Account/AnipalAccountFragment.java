package c.bilgin.anipal.ViewModel.Account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.api.Distribution;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.CropActivity;


public class AnipalAccountFragment extends Fragment {

    private TextView textViewFullname
            ,textViewStartedDonationCount, textViewFedAnimals, textViewFollowerCount
            ,textViewYear, textViewDayOfMonth, textViewMonthOfYear, textViewCurrentAnipalCoin
            ,textViewCitySchool, textViewJob, textViewPet, textViewHobies;
    private CircularImageView imageViewProfilePhoto;
    private ImageButton imageButtonProfileEdit;
    private Button buttonGetCoin;
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
        buttonGetCoin = linearLayout.findViewById(R.id.buttonGetCoin);


        // ----------------------------------------------------
        textViewFullname.setText(MainActivity.currentUser.getFirstName() + " "+MainActivity.currentUser.getLastName());
        textViewCurrentAnipalCoin.setText(""+MainActivity.currentUser.getCoin().getCoin());
        if(MainActivity.currentUser.getPhotoURL() != null)
            Picasso.get().load(MainActivity.currentUser.getPhotoURL()).fit().into(imageViewProfilePhoto);

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

        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoOption();
            }
        });


        buttonGetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),AnipalBuyCoinActivity.class);
                startActivity(i);
            }
        });

        return linearLayout;
    }

    private void choosePhotoOption(){
        Dialog d = new Dialog(getContext());
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout l = new LinearLayout(d.getContext());
        l.setBackgroundResource(R.drawable.rounded_white_layout);
        l.setPadding(20,20,20,20);
        l.setOrientation(LinearLayout.VERTICAL);
        // Btn get photo from camerea
        Button btn = new Button(l.getContext());
        btn.setText("Kamera ile fotograf çek");
        btn.setAllCaps(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Camera intent
                // Issue
                Intent i = new Intent(getContext(),CropActivity.class);
                // code 1 means that you are coming this intent
                // to change profile picture via capturing image by camera
                i.putExtra("code",1001);
                startActivity(i);
            }
        });

        // Btn get photo from gallery
        Button btn2 = new Button(l.getContext());
        btn2.setText("Galeriden fotograf seç");
        btn2.setAllCaps(false);
        l.addView(btn);
        l.addView(btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gallery intent
                Intent i = new Intent(getContext(),CropActivity.class);
                i.putExtra("code",1000); // code 0 means that you are coming to
                // change the profile picture with image from gallery
                startActivity(i);
            }
        });

        d.setContentView(l);
        d.show();
    }
}
