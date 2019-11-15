package c.bilgin.anipal.Adapters.Post;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindType(AnipalAbstractPost post);
}
