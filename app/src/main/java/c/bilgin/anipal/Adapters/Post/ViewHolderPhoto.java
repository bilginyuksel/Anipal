package c.bilgin.anipal.Adapters.Post;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.Model.Post.AnipalPostComment;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.AnipalAccountFragment;
import c.bilgin.anipal.Ui.Account.AnipalFollowProfileFragment;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.NavigationActivity;
import c.bilgin.anipal.Ui.Post.CommentActivity;

public class ViewHolderPhoto extends ViewHolder {

    private ImageView img;
    private ImageButton imageButtonLike, imageButtonComment;
    private CircularImageView imageButtonProfilePhoto;
    private TextView textViewUploadTime, textViewFullname, textViewComment,
    textViewLikes,textViewDescription;
    private boolean isLiked = false;
    private Context mContext;
    private LinearLayout linearLayoutLikeCommentDescription;

    public ViewHolderPhoto(@NonNull View itemView,Context context) {
        super(itemView);
        img = itemView.findViewById(R.id.imageViewPhoto);
        imageButtonProfilePhoto = itemView.findViewById(R.id.imageButtonProfilePhoto);
        textViewFullname = itemView.findViewById(R.id.textViewFullname);
        textViewUploadTime = itemView.findViewById(R.id.textViewUploadTime);
        textViewLikes = itemView.findViewById(R.id.textViewLikes);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);
        imageButtonComment = itemView.findViewById(R.id.imageButtonComment);
        imageButtonLike = itemView.findViewById(R.id.imageButtonLike);
        linearLayoutLikeCommentDescription = itemView.findViewById(R.id.linearLayoutLikeCommentDescription);
        textViewComment = itemView.findViewById(R.id.textViewComment);
        mContext = context;
    }

    @Override
    public void bindType(final AnipalAbstractPost post) {
        final AnipalPhotoPost photoPost = (AnipalPhotoPost)post;
        // Load photo and user informations here...
        // img.setImageResource(R.drawable.anipallogo);
        // System.out.println("PHOTO URL : "+photoPost.getPhotoURL());

        // set photo width, height
        LinearLayout.LayoutParams i = new LinearLayout.LayoutParams(img.getLayoutParams());
        i.height = photoPost.getHeight();
        i.width = photoPost.getWidth();
        img.setLayoutParams(i);
        // img.setMinimumWidth(photoPost.getWidth());
        // img.setMinimumHeight(photoPost.getHeight());
        Picasso.get().load(photoPost.getPhotoURL()).fit().centerCrop().into(img);

        Date d = post.getUploadTime();
        long milliSeconds = (d.getTime() - Timestamp.now().toDate().getTime());
        double minutes = milliSeconds * (1.66666667 / 100000);

        int m = (int)(Math.abs(minutes));
        if(m<60) textViewUploadTime.setText(""+m+" dakika önce");
        else if(m<1440)textViewUploadTime.setText(""+(m/60) +" saat önce");
        else if(m<10080)textViewUploadTime.setText(""+(m/1440)+" gün önce");
        else if(m<43200)textViewUploadTime.setText(""+(m/10080)+" hafta önce");
        else textViewUploadTime.setText(""+(m/41760)+ "ay önce");

        // if you liked that
        imageButtonLike.setBackgroundResource(R.drawable.like);
        final List<String> likers = photoPost.getLikers();
        if(likers.contains(MainActivity.currentUser.getUserUUID())) {
            imageButtonLike.setBackgroundResource(R.drawable.like_pink); isLiked = true;
        }
        imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiked){
                    imageButtonLike.setBackgroundResource(R.drawable.like);
                    isLiked = false;
                    photoPost.getLikers().remove(MainActivity.currentUser.getUserUUID());
                    updatePost(photoPost);
                    textViewLikes.setText(""+photoPost.getLikers().size()+" beğeni");
                }else{
                    imageButtonLike.setBackgroundResource(R.drawable.like_pink);
                    isLiked = true;
                    photoPost.getLikers().add(MainActivity.currentUser.getUserUUID());
                    updatePost(photoPost);
                    textViewLikes.setText(""+photoPost.getLikers().size()+" beğeni");
                }
            }
        });
        /*
        * if you click the button it has to update
        * post and show it on the ui
        * */

        // Add user properties when user loaded
        textViewFullname.setText(photoPost.getAnipalUser()!=null?(photoPost.getAnipalUser().getFirstName() + " "+photoPost.getAnipalUser().getLastName()):"");
        textViewDescription.setText(photoPost.getPhotoDescription());
        textViewLikes.setText(""+photoPost.getLikers().size()+" beğeni");
        // Set image with picasso.
        if(photoPost.getAnipalUser()!=null && photoPost.getAnipalUser().getPhotoURL()!=null)
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




        // Comment click listener

        imageButtonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send user to comment intent...
                // with informations like postUUID
                gotoCommentIntent(photoPost);
            }
        });
        if(photoPost.getComments().size() != 0){
            textViewComment.setText(photoPost.getComments().size()+" yorumu görüntüle");
        }else{
            linearLayoutLikeCommentDescription.removeView(textViewComment);
        }
        textViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCommentIntent(photoPost);
            }
        });
    }

    private void gotoCommentIntent(AnipalPhotoPost post){
        Intent i = new Intent(mContext, CommentActivity.class);
        i.putExtra("postUUID",post.getPostUUID());
        i.putExtra("receiverUUID",post.getUserUUID());
        TreeMap<String, AnipalPostComment> sortedComments = new TreeMap<>(post.getComments());
        i.putExtra("comments",sortedComments);
        mContext.startActivity(i);
    }

    private void updatePost(AnipalAbstractPost post){
        FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostUUID()).setValue(post);
    }


}
