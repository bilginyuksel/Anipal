package c.bilgin.anipal.ViewModel.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Firebase.AnipalFirebasePosts;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.MainActivity;

public class AnipalPostUploadActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonUploadApprove;
    private ImageButton imageButtonBack;
    private boolean isUri = false;
    private AnipalFirebasePosts firebasePosts;
    private StorageReference reference;
    private EditText editTextDescription;
    private ProgressDialog dialog;

    private Uri uri;
    private Bitmap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_post_upload);

        imageView = findViewById(R.id.imageView);
        buttonUploadApprove = findViewById(R.id.buttonUploadApprove);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        editTextDescription = findViewById(R.id.editTextDescription);
        dialog = new ProgressDialog(this);
        firebasePosts = new AnipalFirebase(this,"Posts");
        reference = FirebaseStorage.getInstance().getReference("Posts");
        dialog.setTitle("Gönderi");
        dialog.setMessage("Gönderi paylaşılıyor. Lütfen Bekleyiniz...");


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Get data from another intent
        // Capture intent data with 2 different contents
        // If user used camera get as a bitmap if user chose from gallery get as uri

        if(getIntent().getStringExtra("imageUri")!=null) {
            uri = Uri.parse(getIntent().getStringExtra("imageUri"));
            imageView.setImageURI(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            File f = new File(uri.getPath());

            BitmapFactory.decodeFile(f.getAbsolutePath(),options);
            int height = options.outHeight;
            int width = options.outWidth;
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            //imageView.setScaleType(ImageView.ScaleType.FIT_END);

            /*int theHeight = (height*metrics.widthPixels) / (width>0?width:1);
            imageView.setMinimumHeight(theHeight);*/

            isUri = true;
        }else{
            Bundle extras = getIntent().getExtras();
            map = (Bitmap)extras.get("data");
            System.out.println("MAP HEIGHT : "+map.getHeight());
            System.out.println("MAP WIDTH : "+map.getWidth());
            int height = map.getHeight();
            int width = map.getWidth();
            imageView.setImageBitmap(map);
            imageView.setMinimumWidth(width);
            imageView.setScaleType((ImageView.ScaleType.FIT_XY));


            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;

            int theHeight = (height*widthPixels)/width;
            imageView.setMinimumHeight(theHeight);

            isUri = false; // i did this because i scare any kind of errors.
        }

        buttonUploadApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if(isUri) uploadPhotoPostFirebaseURI(uri);
                else uploadPhotoPostFirebaseBitmap(map);
            }
        });

        // go back
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back
                onBackPressed();
            }
        });
    }

    private void uploadPhotoPostFirebaseURI(Uri uri){

        // Create uuid for unique photos. and add .png or .jpg to end of the file
        // If needed you can store names as same uid's with posts.
        // Post's uid and photos uid can be same.
        String uid = UUID.randomUUID().toString();
        uid += ".png";

        reference.child(uid).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                AnipalAbstractPost post =
                                        new AnipalPhotoPost(MainActivity.currentUser.getUserUUID(),uri.toString(),editTextDescription.getText().toString());
                                post.setUser(MainActivity.currentUser);
                                firebasePosts.publish(post);
                                dialog.dismiss();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failure operation while getting download link
                                e.printStackTrace();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failure operation for can't download
                        e.printStackTrace();
                    }
                });
    }


    private void uploadPhotoPostFirebaseBitmap(Bitmap map){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte []data= byteArrayOutputStream.toByteArray();

        String uid = UUID.randomUUID().toString();
        uid += ".png";

        reference.child(uid).putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                AnipalAbstractPost post =
                                        new AnipalPhotoPost(MainActivity.currentUser.getUserUUID(),uri.toString(),editTextDescription.getText().toString());
                                post.setUser(MainActivity.currentUser);
                                firebasePosts.publish(post);
                                dialog.dismiss();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failure operation while getting download link
                                e.printStackTrace();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }
}
