package c.bilgin.anipal.Ui.Post;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.CropActivity;

import static android.app.Activity.RESULT_OK;


public class AnipalAddPostFragment extends Fragment {

    private Context mContext;
    private EditText editTextDonationPurpose, editTextDonationPrice;
    private Button buttonCreateDonationBar;
    private ScrollView linearLayout;
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
        linearLayout=(ScrollView) inflater.inflate(R.layout.fragment_publish_post,null);
        editTextDonationPurpose = linearLayout.findViewById(R.id.editTextDonationPurpose);
        buttonCreateDonationBar = linearLayout.findViewById(R.id.buttonCreateDonationBar);
        editTextDonationPrice = linearLayout.findViewById(R.id.editTextDonationPrice);
        layoutOpenCamera = linearLayout.findViewById(R.id.layoutOpenCamera);
        layoutPickFromGallery = linearLayout.findViewById(R.id.layoutPickFromGallery);

        buttonCreateDonationBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Bağış Barı");
                builder.setMessage("Bağış barı oluşturma işleminiz tamamlanıyor. Onaylıyor musunuz?");
                builder.setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createDonationBar();
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });


        layoutPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to android gallery intent and pick a photo
                pickPhotoFromGallery();
            }
        });

        layoutOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to camera intent and take a photo
                takePhotoFromCamera();
            }
        });


        return linearLayout;
    }

    private void takePhotoFromCamera(){
        /*
        * direct to CropActivity with request code 2001
        * capture image via camera then crop */
        Intent i = new Intent(getContext(),CropActivity.class);
        i.putExtra("code",2001);
        startActivity(i);
    }
    private void pickPhotoFromGallery(){
        /*
        * direct to CropActivity with request code 2000
        * pick image from gallery then crop*/
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,1);
    }
    private void createDonationBar(){
        // Create donation bar.
        String donationPurpose = editTextDonationPurpose.getText().toString();
        // it could be double too !
        int donationPrice = Integer.parseInt(editTextDonationPrice.getText().toString());

        // Now with those information create donation bar.
        post = new AnipalDonationPost(MainActivity.currentUser.getUserUUID(),donationPurpose,donationPrice);
        post.setUser(MainActivity.currentUser);
        anipalFirebase.publish(post);
        editTextDonationPrice.getText().clear();
        editTextDonationPurpose.getText().clear();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // When user comes from gallery or taking photo
        if(resultCode != RESULT_OK)
            return;

        // Request code == 1
        if(requestCode == 1){
            Uri uri= data.getData();
            if(uri!=null){
                Intent i = new Intent(getActivity(), AnipalPostUploadActivity.class);
                i.setData(uri);
                startActivity(i);
            }

         }

    }
}
