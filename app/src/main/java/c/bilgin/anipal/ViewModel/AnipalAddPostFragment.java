package c.bilgin.anipal.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.R;


public class AnipalAddPostFragment extends Fragment {

    private Context mContext;
    private EditText editTextDonationPurpose, editTextDonationPrice;
    private Button buttonCreateDonationBar;
    private LinearLayout layoutOpenCamera, layoutPickFromGallery;
    private AnipalFirebase anipalFirebase ;

    // use this for db adding operations
    private AnipalAbstractPost post;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        anipalFirebase = new AnipalFirebase(mContext,"Posts");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_publish_post,null);
        editTextDonationPurpose = linearLayout.findViewById(R.id.editTextDonationPurpose);
        buttonCreateDonationBar = linearLayout.findViewById(R.id.buttonCreateDonationBar);
        editTextDonationPrice = linearLayout.findViewById(R.id.editTextDonationPrice);
        layoutOpenCamera = linearLayout.findViewById(R.id.layoutOpenCamera);
        layoutPickFromGallery = linearLayout.findViewById(R.id.layoutPickFromGallery);

        buttonCreateDonationBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDonationBar();
            }
        });


        layoutPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to android gallery intent and pick a photo
            }
        });

        layoutOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to camera intent and take a photo
            }
        });


        return linearLayout;
    }


    private void createDonationBar(){
        // Create donation bar.
        String donationPurpose = editTextDonationPurpose.getText().toString();
        // it could be double too !
        int donationPrice = Integer.parseInt(editTextDonationPrice.getText().toString());

        // Now with those information create donation bar.
        post = new AnipalDonationPost(MainActivity.currentUser.getUserUUID(),donationPurpose,donationPrice);
        anipalFirebase.publish(post);
    }
}
