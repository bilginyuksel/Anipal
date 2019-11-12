package c.bilgin.anipal.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.AnipalAccountFragment;
import c.bilgin.anipal.ViewModel.Account.AnipalFollowProfileFragment;
import c.bilgin.anipal.ViewModel.Account.MainActivity;
import c.bilgin.anipal.ViewModel.NavigationActivity;

public class ViewHolderPhoto extends ViewHolder {

    private ImageView img;
    private CircularImageView imageButtonProfilePhoto;
    private TextView textViewUploadTime, textViewFullname,
    textViewLikes,textViewDescription;

    public ViewHolderPhoto(@NonNull View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.imageViewPhoto);
        imageButtonProfilePhoto = itemView.findViewById(R.id.imageButtonProfilePhoto);
        textViewFullname = itemView.findViewById(R.id.textViewFullname);
        textViewUploadTime = itemView.findViewById(R.id.textViewUploadTime);
        textViewLikes = itemView.findViewById(R.id.textViewLikes);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);
    }

    @Override
    public void bindType(final AnipalAbstractPost post) {
        AnipalPhotoPost photoPost = (AnipalPhotoPost)post;
        // Load photo and user informations here...
        // img.setImageResource(R.drawable.anipallogo);
        // System.out.println("PHOTO URL : "+photoPost.getPhotoURL());
        Picasso.get().load(photoPost.getPhotoURL()).fit().into(img);

        Date d = post.getUploadTime();
        long milliSeconds = (d.getTime() - Timestamp.now().toDate().getTime());
        double minutes = milliSeconds * (1.66666667 / 100000);

        int m = (int)(Math.abs(minutes));
        if(m<60) textViewUploadTime.setText(""+m+" dakika önce");
        else if(m<1440)textViewUploadTime.setText(""+(m/60) +" saat önce");
        else if(m<10080)textViewUploadTime.setText(""+(m/1440)+" gün önce");
        else if(m<43200)textViewUploadTime.setText(""+(m/10080)+" hafta önce");
        else textViewUploadTime.setText(""+(m/41760)+ "ay önce");



        // Add user properties when user loaded
        textViewFullname.setText(photoPost.getAnipalUser().getFirstName() + " "+photoPost.getAnipalUser().getLastName());
        textViewDescription.setText(photoPost.getPhotoDescription());
        textViewLikes.setText(""+photoPost.getLikers().size()+" beğeni");
        // Set image with picasso.
        if(photoPost.getAnipalUser().getPhotoURL()!=null)
            Picasso.get().load(photoPost.getAnipalUser().getPhotoURL()).fit().into(imageButtonProfilePhoto);

        imageButtonProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if the user is the user which uses the application right now
                // you have to manage that user to real profile page.
                NavigationActivity.fragmentTransaction = NavigationActivity.fragmentManager.beginTransaction();
                NavigationActivity.fragmentTransaction.replace(R.id.main_frame_layout,
                        post.getAnipalUser()
                                .getUserUUID().equals(MainActivity.currentUser.getUserUUID())?
                                new AnipalAccountFragment():
                                new AnipalFollowProfileFragment(post.getAnipalUser()));
                NavigationActivity.fragmentTransaction.commit();
            }
        });
    }
}
