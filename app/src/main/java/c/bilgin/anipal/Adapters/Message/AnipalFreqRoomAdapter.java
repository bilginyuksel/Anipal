package c.bilgin.anipal.Adapters.Message;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalFreqRoomAdapter extends RecyclerView.Adapter<AnipalFreqRoomAdapter.ViewHolderFreqRoom> {

    private Context mContext;
    private List<AnipalChatRoom> rooms;
    private OnItemClickListener onItemClickListener;

    public AnipalFreqRoomAdapter(Context context, List<AnipalChatRoom> rooms, OnItemClickListener onItemClickListener){
        mContext = context;
        this.rooms = rooms;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderFreqRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(mContext).inflate(R.layout.card_frequent_chat_room,parent,false);
        final ViewHolderFreqRoom viewHolderFreqRoom = new ViewHolderFreqRoom(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view,viewHolderFreqRoom.getAdapterPosition());
            }
        });
        return viewHolderFreqRoom;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFreqRoom holder, int position) {
        holder.bindType(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class ViewHolderFreqRoom extends RecyclerView.ViewHolder{

        private CircularImageView circularImageView;
        private TextView textViewName;

        public ViewHolderFreqRoom(@NonNull View itemView) {
            super(itemView);
            circularImageView = itemView.findViewById(R.id.imageViewProfilePhoto);
            textViewName = itemView.findViewById(R.id.textViewName);
        }

        public void bindType(AnipalChatRoom room){
            // Picasso.get().load(room.getUserPhotoURL()).fit().into(circularImageView);
            String fullname = room.getUserFullname();
            textViewName.setText(fullname.split(" ")[0]);

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(room.getUserUUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.getValue(AnipalUser.class).getPhotoURL();
                    if(url!=null)
                    Picasso.get().load(dataSnapshot.getValue(AnipalUser.class).getPhotoURL()).fit().into(circularImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
