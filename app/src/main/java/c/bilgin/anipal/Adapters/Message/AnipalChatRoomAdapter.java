package c.bilgin.anipal.Adapters.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.R;

public class AnipalChatRoomAdapter extends RecyclerView.Adapter<AnipalChatRoomAdapter.ViewHolderChatRoom> {

    private List<AnipalChatRoom> rooms;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public static class ViewHolderChatRoom extends RecyclerView.ViewHolder {
        private CircularImageView circularImageView;
        private TextView textViewFullname, textViewLastMessage;
        private Button btnNotReadMessages;
        public ViewHolderChatRoom(@NonNull View itemView) {
            super(itemView);
            circularImageView = itemView.findViewById(R.id.profilePicture);
            textViewFullname = itemView.findViewById(R.id.textViewFullname);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            btnNotReadMessages = itemView.findViewById(R.id.buttonNotReadMessages);
        }

        public void bindType(AnipalChatRoom room){
            /*
            * load visual data here.
            * Issue #1 :
            *   Should i store data like that ?
            *   Sender and receiver exists but how can i know which one is sender ?
            *   I can compare the differences but is it efficient ?
            * */
            textViewLastMessage.setText(room.getLastMessage());
            textViewFullname.setText(room.getUserFullname());
            Picasso.get().load(room.getUserPhotoURL()).fit().into(circularImageView);
            btnNotReadMessages.setVisibility(View.INVISIBLE);

            if(room.getMessages()!=null){
                int notReadCount = room.getNotReadMessagesCount();
                if(notReadCount!=0) {
                    btnNotReadMessages.setText(""+room.getNotReadMessagesCount());
                    btnNotReadMessages.setVisibility(View.VISIBLE);
                }else{
                    btnNotReadMessages.setText("0");
                    btnNotReadMessages.setVisibility(View.INVISIBLE);
                }

            }

        }
    }

    public AnipalChatRoomAdapter(Context context, List<AnipalChatRoom> rooms, OnItemClickListener onItemClickListener){
        this.rooms = rooms;
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderChatRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat_room,parent,false);

        final ViewHolderChatRoom viewHolderChatRoom = new ViewHolderChatRoom(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view,viewHolderChatRoom.getAdapterPosition());
            }
        });
        return viewHolderChatRoom;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderChatRoom holder, int position) {
        holder.bindType(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


}
