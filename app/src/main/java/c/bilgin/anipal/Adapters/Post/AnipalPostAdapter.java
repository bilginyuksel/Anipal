package c.bilgin.anipal.Adapters.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.ListItem;
import c.bilgin.anipal.R;

public class AnipalPostAdapter extends RecyclerView.Adapter<ViewHolder> {


    private List<AnipalAbstractPost> posts;
    private Context mContext;


    public AnipalPostAdapter(Context context,List<AnipalAbstractPost> posts){
        this.posts = posts;
        mContext = context;
    }
    public AnipalPostAdapter(){

    }

    // Create new views (invoked by the layout manager.)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType){
            case ListItem.TYPE_DONATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_anipal_donation,parent,false);
                return new ViewHolderDonation(view);
            case ListItem.TYPE_PHOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_anipal_photo,parent,false);
                return new ViewHolderPhoto(view,mContext);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_anipal_photo,parent,false);
        ImageView v1 = v.findViewById(R.id.imageViewPhoto);
        v1.setImageResource(R.drawable.anipallogo);
        return new ViewHolderPhoto(v,mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position).getListItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get element from your dataset at this position
        AnipalAbstractPost post = posts.get(position);
        holder.bindType(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setContext(Context ctx){
        mContext = ctx;
    }
}
