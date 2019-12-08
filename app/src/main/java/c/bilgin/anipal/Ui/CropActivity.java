package c.bilgin.anipal.Ui;

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
import c.bilgin.anipal.Ui.Account.MainActivity;

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

                uri = getIntent().getData();
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);

                break;
            case 1001:
                /* Get the thumbnail
                Save the thumbnail to device
                get uri data from file
                 */
                Bitmap map = (Bitmap)getIntent().getExtras().get("data");
                File savedImage = saveImage(map);

                uri = Uri.fromFile(savedImage);
                cropImageView.load(uri).execute(mLoadCallBack);
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
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
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void updateProfilePicture(Bitmap u){

        StorageReference ref = FirebaseStorage.getInstance().getReference("Users");
        ref.child(MainActivity.currentUser.getUserUUID()+".jpeg").delete();
        // Change bitmap to bytearray then add to firebase
        Bitmap map = getResizedBitmap(u,200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // try to solve, photo size problem
        // I reduced quality 30 to 15,, photo size was 4.~7.mb
        map.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte[] data = baos.toByteArray();
        // add bytes to firebase
        String uid = MainActivity.currentUser.getUserUUID();
        uid+= ".jpeg";
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
            File f = new File(imageDirectory, Calendar.getInstance().getTimeInMillis() + ".jpeg");
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
