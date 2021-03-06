package c.bilgin.anipal.Ui.Post;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

import static android.app.Activity.RESULT_OK;


public class AnipalAddPostFragment extends Fragment {

    private Context mContext;
    private EditText editTextDonationPurpose, editTextDonationPrice;
    private Button buttonCreateDonationBar;
    private ScrollView linearLayout;
    private LinearLayout layoutOpenCamera, layoutPickFromGallery;
    private AnipalFirebase anipalFirebase ;
    private int locationCode;

    // use this for db adding operations
    private AnipalAbstractPost post;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final int CAPTURE_FROM_CAMERA = 10;
    private static final int PICK_FROM_GALLERY = 1;

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
        /*editTextDonationPurpose = linearLayout.findViewById(R.id.editTextDonationPurpose);
        buttonCreateDonationBar = linearLayout.findViewById(R.id.buttonCreateDonationBar);
        editTextDonationPrice = linearLayout.findViewById(R.id.editTextDonationPrice);*/
        layoutOpenCamera = linearLayout.findViewById(R.id.layoutOpenCamera);
        layoutPickFromGallery = linearLayout.findViewById(R.id.layoutPickFromGallery);

        /*buttonCreateDonationBar.setOnClickListener(new View.OnClickListener() {
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
        });*/


        layoutPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Go to android gallery intent and pick a photo
                if(hasLocationPermission(getContext()))
                pickPhotoFromGallery();
                else givePermission(0);
            }
        });

        layoutOpenCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Go to camera intent and take a photo
                if(hasLocationPermission(getContext()))
                takePhotoFromCamera();
                else givePermission(1);
            }
        });


        return linearLayout;
    }

    private void givePermission(int location_code){
        locationCode = location_code;
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermission(Context context){
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            return false;
        }
        return true;
    }

    private void takePhotoFromCamera(){
        /*
        * direct to CropActivity with request code 2001
        * capture image via camera then crop */
        /*Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,CAPTURE_FROM_CAMERA);*/
        dispatchTakePictureIntent();
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timestamp+"_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "c.bilgin.anipal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_FROM_CAMERA);
            }
        }

    }
    private void pickPhotoFromGallery(){
        /*
        * direct to CropActivity with request code 2000
        * pick image from gallery then crop*/
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,PICK_FROM_GALLERY);
    }
    private void createDonationBar(){
        // Create donation bar.
        String donationPurpose = editTextDonationPurpose.getText().toString();
        // it could be double too !
        int donationPrice = Integer.parseInt(editTextDonationPrice.getText().toString());

        // Now with those information create donation bar.
        post = new AnipalDonationPost(MainActivity.currentUser.getUserUUID(),donationPurpose,donationPrice);
        post.setUser(MainActivity.currentUser);
        // Add Donation post to post
        // And update current user values.
        MainActivity.currentUser.getPosts().add(post.getPostUUID());
        Map<String,Object> obj = new HashMap<>();
        obj.put("posts",MainActivity.currentUser.getPosts());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(MainActivity.currentUser.getUserUUID()).updateChildren(obj);
        anipalFirebase.publish(post);
        editTextDonationPrice.getText().clear();
        editTextDonationPurpose.getText().clear();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // When user comes from gallery or taking photo
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == PICK_FROM_GALLERY){
            Uri uri= data.getData();
            if(uri!=null){
                Intent i = new Intent(getActivity(), AnipalPostUploadActivity.class);
                i.setData(uri);
                startActivity(i);
            }
         }else if(requestCode == CAPTURE_FROM_CAMERA){

            File f = new File(currentPhotoPath);
            Uri uri = Uri.fromFile(f);
            System.out.println(uri);
            // Capture image from camera and send it to upload activity
            Intent i = new Intent(getActivity(),AnipalPostUploadActivity.class);
            i.setData(uri);
            startActivity(i);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        // do what ever you want here.
                        // pick one of them -- you can use magic string
                        // this code is not working but it doesn't matter
                        if(locationCode==0)
                        pickPhotoFromGallery();
                        else if(locationCode==1)
                        takePhotoFromCamera();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mContext, "Yükleme yapabilmeniz için uygulama konum bilgisini açmalısınız.", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
