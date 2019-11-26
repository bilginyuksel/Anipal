package c.bilgin.anipal.Ui.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Firebase.AnipalFirebasePosts;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.NavigationActivity;

public class AnipalPostUploadActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonUploadApprove;
    private ImageButton imageButtonBack;
    private boolean isUri = false;
    private AnipalFirebasePosts firebasePosts;
    private StorageReference reference;
    private EditText editTextDescription;
    private TextView textViewPublish;
    private ProgressDialog dialog;
    int code = -1;

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
        textViewPublish = findViewById(R.id.textViewPublish);

        dialog = new ProgressDialog(this);
        firebasePosts = new AnipalFirebase(this, "Posts");
        reference = FirebaseStorage.getInstance().getReference("Posts");
        dialog.setTitle("Gönderi");
        dialog.setMessage("Gönderi paylaşılıyor. Lütfen Bekleyiniz...");


        // Control it are we getting here !!!! ???
        if (getIntent().getByteArrayExtra("bytes") != null) {
            byte[] bytes = getIntent().getByteArrayExtra("bytes");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            map = bitmap;
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageURI(getIntent().getData());
            code = 0;
        }

        textViewPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int width = imageView.getWidth();
                int height = imageView.getHeight();

                System.out.println("Width : "+width);
                System.out.println("Height : "+height);
            }
        });


        buttonUploadApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if(code == 0) uploadPhotoPostFirebaseURI(getIntent().getData());
                else uploadPhotoPostFirebaseBitmap(map);
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Issue #1 : It goes back to crop activity we don't want this.
                * It has to go to addPostFragment so we need to use fragment managers here
                * */
                // by the way you can set the fragments too.
                startActivity(new Intent(AnipalPostUploadActivity.this, NavigationActivity.class));
                // onBackPressed();
            }
        });

    }

    private void uploadPhotoPostFirebaseURI(Uri uri){

        /*
        Create uuid for unique photos. and add .png or .jpg to end of the file
         If needed you can store names as same uid's with posts.
         Post's uid and photos uid can be same.
         Reduce photographs quality ! At here or at the server side.
         */

        String uid = UUID.randomUUID().toString();
        uid += ".png";

        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        reference.child(uid).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                AnipalAbstractPost post = new AnipalPhotoPost(MainActivity.currentUser.getUserUUID(),
                                        uri.toString(),editTextDescription.getText().toString(),width,height);
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

        final int width = imageView.getWidth();
        final int height = imageView.getHeight();

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
                                AnipalAbstractPost post = new AnipalPhotoPost(MainActivity.currentUser.getUserUUID(),
                                        uri.toString(),editTextDescription.getText().toString(),width,height);
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
