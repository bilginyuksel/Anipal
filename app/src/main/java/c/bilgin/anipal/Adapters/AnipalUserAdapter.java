package c.bilgin.anipal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import c.bilgin.anipal.CircleTransform;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalUserAdapter extends RecyclerView.Adapter<AnipalUserAdapter.ViewHolderUser> {


    private Context mContext;
    private List<AnipalUser> anipalUsers;

    public static class ViewHolderUser extends RecyclerView.ViewHolder{

        private ImageView imageViewProfilePhoto;
        private TextView textViewFullName, textViewEmail;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            textViewFullName = itemView.findViewById(R.id.textViewFullname);
            imageViewProfilePhoto = itemView.findViewById(R.id.imageButtonProfilePhoto);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
        }

        public void bindType(final AnipalUser user){
            textViewFullName.setText(user.getFirstName() + " "+ user.getLastName());
            Picasso.get().load(user.getPhotoURL()).fit().transform(new CircleTransform()).into(imageViewProfilePhoto);
            textViewEmail.setText(user.getEmailAddress());


        }
    }


    public AnipalUserAdapter(Context context,List<AnipalUser> anipalUsers){
        this.mContext = context;
        this.anipalUsers = anipalUsers;
    }

    @NonNull
    @Override
    public ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_anipal_user,parent,false);

        ViewHolderUser viewHolderUser = new ViewHolderUser(v);
        return viewHolderUser;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUser holder, int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element

        AnipalUser user = anipalUsers.get(position);
        holder.bindType(user);

    }

    @Override
    public int getItemCount() {
        return anipalUsers.size();
    }


}
