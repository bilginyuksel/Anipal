package c.bilgin.anipal.Adapters.Post;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.AnipalAccountFragment;
import c.bilgin.anipal.Ui.Account.AnipalFollowProfileFragment;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.NavigationActivity;
import c.bilgin.anipal.Ui.Post.AnipalMakeDonationDialog;

public class ViewHolderDonation extends ViewHolder {

    private TextView textViewDonationPrice, textViewDonationPurpose, textViewUploadTime,
            textViewFullname, textViewProgressCount;
    private CircularImageView imgButtonProfilePhoto;
    private ProgressBar progressBar;
    private Button buttonMakeDonation;
    private Context mContext;
    private AnipalDonationPost donationPost;

    public ViewHolderDonation(@NonNull View itemView) {
        super(itemView);
        textViewDonationPrice = itemView.findViewById(R.id.textViewDonationPrice);
        textViewDonationPurpose = itemView.findViewById(R.id.textViewDonationPurpose);
        progressBar = itemView.findViewById(R.id.contentLoadingProgressBar);
        textViewUploadTime = itemView.findViewById(R.id.textViewUploadTime);
        textViewFullname = itemView.findViewById(R.id.textViewFullname) ;
        imgButtonProfilePhoto = itemView.findViewById(R.id.imageButtonProfilePhoto);
        textViewProgressCount = itemView.findViewById(R.id.textViewProgressCounter);
        buttonMakeDonation = itemView.findViewById(R.id.buttonMakeDonation);
        this.mContext = itemView.getContext();
    }

    @Override
    public void bindType(final AnipalAbstractPost post) {
        donationPost= (AnipalDonationPost)post;
        // Also get user information too
        textViewDonationPurpose.setText(donationPost.getDonationPurpose());
        textViewDonationPrice.setText(""+donationPost.getDonationPrice()+" TL");

        // Do the progress bar calculation
        // if currentDonation 0 easily write 0 else make the calculation
        int progress = 0;
        if(donationPost.getCurrentDonation() == 0) progressBar.setProgress(donationPost.getCurrentDonation());
        else{
            // donationPrice is %100
            // currentDonation is % ?
            progress =( (100*donationPost.getCurrentDonation())/donationPost.getDonationPrice());
            progressBar.setProgress(progress);
        }
        textViewProgressCount.setText("% "+progress);

        // Compare upload date data with current time data
        // 1.66666667 × 100000 minutes
        long milliSeconds = (post.getTimestamp() - Timestamp.now().toDate().getTime());
        double minutes = milliSeconds * (1.66666667 / 100000);

        int m = (int)(Math.abs(minutes));
        if(m<60) textViewUploadTime.setText(""+m+" dakika önce");
        else if(m<1440)textViewUploadTime.setText(""+(m/60) +" saat önce");
        else if(m<10080)textViewUploadTime.setText(""+(m/1440)+" gün önce");
        else if(m<43200)textViewUploadTime.setText(""+(m/10080)+" hafta önce");
        else textViewUploadTime.setText(""+(m/41760)+ "ay önce");


        // Add user properties when user loaded
        textViewFullname.setText(donationPost.getAnipalUser()!=null?
                (donationPost.getAnipalUser().getFirstName() + " "+donationPost.getAnipalUser().getLastName()):"");
        // Set image with picasso.
        if(donationPost.getAnipalUser()!=null && donationPost.getAnipalUser().getPhotoURL() != null)
            Picasso.get().load(donationPost.getAnipalUser().getPhotoURL()).fit().into(imgButtonProfilePhoto);
        else imgButtonProfilePhoto.setImageResource(R.drawable.user);


        buttonMakeDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnipalMakeDonationDialog dialog = new AnipalMakeDonationDialog(mContext,donationPost);
                dialog.show();
            }
        });

        imgButtonProfilePhoto.setOnClickListener(new View.OnClickListener() {
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
