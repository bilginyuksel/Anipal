package c.bilgin.anipal.ViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.MainActivity;
import c.bilgin.anipal.ViewModel.Post.AnipalPostUploadActivity;

public class CropActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private LoadCallback mLoadCallBack;
    private CropCallback mCropCallBack;
    private Button btn;
    private Uri uri;
    private ProgressDialog progressDialog;
    private int code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        cropImageView = findViewById(R.id.cropImageView);
        configureCropImageViewListeners();
        btn = findViewById(R.id.btn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor...");
        checkWriteExternalStoragePermission();

        // get the code and configure which operation you are going to do
        code = getIntent().getIntExtra("code",-1);

        switch(code){

            case 1000:
                // you came to change profile picture
                // go to gallery intent and pick a profile picture
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,1);

                break;
            case 1001:
                // you came to change profile picture
                // go to camera intent and capture an image
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),2);
                break;
            case 2000:
                /*
                * to add post go to gallery intent and pick a photo*/
                System.out.println("I am here !");
                uri = getIntent().getData();
                Intent i1 = new Intent(CropActivity.this,AnipalPostUploadActivity.class);
                i1.setData(uri);
                startActivity(i1);
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
                break;
            case 2001:
                /*
                * to add post go to camera intent and capture image*/
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),200);
                break;
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                cropImageView.crop(uri).execute(mCropCallBack);
            }
        });




    }


    private void configureCropImageViewListeners(){
        // Loading function
        mLoadCallBack = new LoadCallback() {
            @Override
            public void onSuccess() {
                // data successfully loaded.
            }

            @Override
            public void onError(Throwable e) {
                // Some error occured.
            }
        };

        // Cropping functions.
        mCropCallBack = new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                // Cropping is OK!
                // add data to firebase and update profile picture
                if(code==1000 || code ==1001) updateProfilePicture(cropped);
                else if(code==2000 || code ==2001)sendPictureToPostUpload(cropped);
            }

            @Override
            public void onError(Throwable e) {
                // Some error occured.
            }
        };
    }

    /*
    * if picture came for adding post.
    * then when you cropped send it to uploadactivity for final job.*/
    private void sendPictureToPostUpload(@NotNull Bitmap cropped){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;
        options.inPurgeable = true;
        options.inPreferQualityOverSpeed = true;
        options.inTempStorage=new byte[32 * 1024];


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.PNG, 80, stream);
        byte []bytes = stream.toByteArray();
        Intent i = new Intent(CropActivity.this, AnipalPostUploadActivity.class);
        i.putExtra("bytes",bytes);
        startActivity(i);
    }
    private void checkWriteExternalStoragePermission(){
        if (ContextCompat.checkSelfPermission(CropActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(CropActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(CropActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    private void updateProfilePicture(Bitmap u){

        StorageReference ref = FirebaseStorage.getInstance().getReference("Users");
        ref.child(MainActivity.currentUser.getUserUUID()).delete();
        // Change bitmap to bytearray then add to firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        u.compress(Bitmap.CompressFormat.PNG,70,baos);
        byte[] data = baos.toByteArray();
        // add bytes to firebase
        String uid = MainActivity.currentUser.getUserUUID();
        uid+= ".png";
        ref.child(uid).putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                MainActivity.currentUser.setPhotoURL(uri.toString());
                                updateUser();
                            }
                        });
                    }
                });
    }

    private void updateUser(){
        DatabaseReference r= FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.currentUser.getUserUUID());
        r.setValue(MainActivity.currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                // After all ends send me back to accounts page.
                Intent i = new Intent(CropActivity.this,NavigationActivity.class);
                startActivity(i); // by the way don't forget to set fragment to accounts
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        switch (requestCode){
            case 1:
                // You got the uri of that file
                uri = data.getData();
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                break;
            case 2:
                // I think so there might be an issue here !
                // You got the thumbnail of that file
                // First save the profile picture then get the uri
                // with that uri fill cropImageView and do the operations !
                File f = saveImage((Bitmap)data.getExtras().get("data"));
                uri = Uri.fromFile(f);
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                break;

            case 100:
                // you got the uri of post image
                uri = data.getData();
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
                /*
                * min frames has to update*/
                break;
            case 200:
                File f1 = saveImage((Bitmap)data.getExtras().get("data"));
                uri = Uri.fromFile(f1);
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
                /*
                * min frames has to update */
                break;
            default:
                System.out.println("I don't know it is important or not");
                break;

        }
    }

    private File saveImage(Bitmap thumbnail){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        // Create Anipal Directory in the phone
        // if it is not exists
        File imageDirectory = new File(
                Environment.getExternalStorageDirectory() + "/Anipal"
        );
        if(!imageDirectory.exists()){
            imageDirectory.mkdirs();
        }

        try{
            File f = new File(imageDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fous = new FileOutputStream(f);
            fous.write(byteArrayOutputStream.toByteArray());
            MediaScannerConnection.scanFile(
                    this,new String[]{f.getPath()},
                    new String[]{"image/jpeg"},null
            );
            fous.close();

            return f;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
