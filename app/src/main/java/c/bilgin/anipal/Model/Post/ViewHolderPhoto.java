package c.bilgin.anipal.Model.Post;

import android.view.View;

import androidx.annotation.NonNull;

public class ViewHolderPhoto extends ViewHolder {
    public ViewHolderPhoto(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindType(AnipalAbstractPost post) {
        AnipalPhotoPost photoPost = (AnipalPhotoPost)post;
    }
}
