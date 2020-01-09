package c.bilgin.anipal.Adapters.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalUserAdapter extends RecyclerView.Adapter<AnipalUserAdapter.ViewHolderUser> {


    private Context mContext;
    private List<AnipalUser> anipalUsers;
    private OnItemClickListener onItemClickListener;

    public static class ViewHolderUser extends RecyclerView.ViewHolder{

        private CircularImageView imageViewProfilePhoto;
        private TextView textViewFullName, textViewEmail;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            textViewFullName = itemView.findViewById(R.id.textViewFullname);
            imageViewProfilePhoto = itemView.findViewById(R.id.imageButtonProfilePhoto);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
        }

        public void bindType(final AnipalUser user){
            if(user!=null){
                textViewFullName.setText(user.getFirstName()!=null?user.getFirstName():"" + " "+ user.getLastName()!=null?user.getLastName():"");
                if(user.getPhotoURL()!=null)
                    Picasso.get().load(user.getPhotoURL()).fit().into(imageViewProfilePhoto);
                else imageViewProfilePhoto.setImageResource(R.drawable.user);

                textViewEmail.setText(user.getEmailAddress());
            }



        }
    }


    public AnipalUserAdapter(Context context,List<AnipalUser> anipalUsers, OnItemClickListener onItemClickListener){
        this.mContext = context;
        this.anipalUsers = anipalUsers;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_anipal_user,parent,false);


        final ViewHolderUser viewHolderUser = new ViewHolderUser(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, viewHolderUser.getAdapterPosition());
            }
        });
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
