package c.bilgin.anipal.Ui.Post;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import c.bilgin.anipal.Model.FedAnimalLocation;
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
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Uri uri;
    private Bitmap map;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        dialog.setCancelable(false);


        // Control it are we getting here !!!! ???
        if (getIntent().getExtras()!= null) {
            // byte[] bytes = getIntent().getByteArrayExtra("bytes");
            // Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            map = (Bitmap)getIntent().getExtras().get("data");
            imageView.setImageBitmap(map);
        }else{
            imageView.setImageURI(getIntent().getData());
            code = 0;
        }

        textViewPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if(code == 0) uploadPhotoPostFirebaseURI(getIntent().getData());
                else uploadPhotoPostFirebaseBitmap(map);
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


    private void addLocationToServer(final AnipalPhotoPost post){
        // this method adds photo's location information to server
        // Program thinks as when a person published post it fed animals and
        // the persons activity marking on the map.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            // Update locations while publishing the method.
                            FedAnimalLocation fedAnimalLocation = new FedAnimalLocation(MainActivity.currentUser.getUserUUID(),
                                    MainActivity.currentUser.getFirstName(),MainActivity.currentUser.getLastName(),
                                    MainActivity.currentUser.getPhotoURL(),post.getPostUUID(),
                                    post.getPhotoURL(),post.getPhotoDescription(),location.getLatitude(),location.getLongitude());
                            // Upload to server
                            FirebaseDatabase.getInstance().getReference("FedAnimalLocations")
                                    .child(fedAnimalLocation.getFedAnimalLocationUUID()).setValue(fedAnimalLocation);
                        }
                    }
                });

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
    private void uploadPhotoPostFirebaseURI(Uri uri) {

        String uid = UUID.randomUUID().toString();
        uid += ".jpeg";

        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            bitmap = getResizedBitmap(bitmap,900);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);

            byte data[] = byteArrayOutputStream.toByteArray();
            final int width = imageView.getWidth();
            final int height = imageView.getHeight();


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
                                    // While post publishing
                                    // publish this posts location information too
                                    addLocationToServer((AnipalPhotoPost)post);

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

        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            Toast.makeText(this, "Gönderi paylaşılırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }



    }
    private void uploadPhotoPostFirebaseBitmap(Bitmap map){

        final int width = imageView.getWidth();
        final int height = imageView.getHeight();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
        byte []data= byteArrayOutputStream.toByteArray();

        String uid = UUID.randomUUID().toString();
        uid += ".jpeg";

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
                                // While post publishing
                                // publish this posts location information too
                                addLocationToServer((AnipalPhotoPost)post);

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
