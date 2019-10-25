package c.bilgin.anipal.Model.Post;

import android.view.View;

import androidx.annotation.NonNull;

public class ViewHolderDonation extends ViewHolder {
    public ViewHolderDonation(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindType(AnipalAbstractPost post) {
        AnipalDonationPost donationPost= (AnipalDonationPost)post;
    }
}
