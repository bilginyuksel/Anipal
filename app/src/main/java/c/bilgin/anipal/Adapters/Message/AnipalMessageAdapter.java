package c.bilgin.anipal.Adapters.Message;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.Model.Message.MessageType;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalMessageAdapter extends RecyclerView.Adapter<ViewHolder> {


    private List<AnipalMessage> messages;
    private Context mContext;
    private static final int MESSAGE_OUT = 1;
    private static final int MESSAGE_IN = 2;


    public AnipalMessageAdapter(Context context, List<AnipalMessage> messages){
        mContext = context;
        this.messages = messages;
    }


    @Override
    public int getItemViewType(int position) {
        int type;
        if(messages.get(position).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()))type = MESSAGE_OUT; // +1
        else type=MESSAGE_IN; // +2
        return (type*messages.get(position).getMessageType());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        int type = MessageType.TEXT_MESSAGE;
        if(viewType<0){
            // That means it is photo message...
            // fix viewType status
            type = MessageType.PHOTO_MESSAGE;
            viewType*=-1;
        }
        switch (viewType){
            case MESSAGE_IN:
                if(type==MessageType.PHOTO_MESSAGE){
                    view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_photo,parent,false);
                    return new ViewHolderPhoto(view,MESSAGE_IN);
                }
                view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message,parent,false);
                return new ViewHolderText(view,MESSAGE_IN);
            case MESSAGE_OUT:
                if(type==MessageType.PHOTO_MESSAGE){
                    view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_photo_out,parent,false);
                    return new ViewHolderPhoto(view,MESSAGE_OUT);
                }
                view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_out,parent,false);
                return new ViewHolderText(view,MESSAGE_OUT);
        }
        // no one works case.
        view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message,parent,false);
        return new ViewHolderText(view,MESSAGE_IN);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnipalMessage m = messages.get(position);
        holder.bindType(m);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
