package c.bilgin.anipal.Adapters.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.Model.Message.MessageType;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalMessageAdapter extends RecyclerView.Adapter<AnipalMessageAdapter.ViewHolderMessage> {


    private List<AnipalMessage> messages;
    private Context mContext;
    private OnItemClickListener listener;
    private static final int MESSAGE_OUT = 1;
    private static final int MESSAGE_IN = 2;
    private static String host,guest;


    public AnipalMessageAdapter(Context context, List<AnipalMessage> messages,String host,String guest){
        mContext = context;
        this.messages = messages;
        this.host = host;
        this.guest = guest;
    }

    public static class ViewHolderMessage extends RecyclerView.ViewHolder{
        private int type;
        private TextView txtSender, txtMessage, txtSendDate;
        private LinearLayout  linearLayoutSender;
        public ViewHolderMessage(@NonNull View itemView,int type) {
            super(itemView);
            this.type = type;

            // linearLayoutSender = itemView.findViewById(R.id.linearLayoutSender);
            // txtSender = itemView.findViewById(R.id.txtSender);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtSendDate = itemView.findViewById(R.id.txtSendDate);
        }

        public void bindType(boolean isContinue, AnipalMessage m){
            txtMessage.setText(m.getMessage());
            // txtSender.setText(type==MESSAGE_IN?host:guest);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");

            txtSendDate.setText(""+dateFormat.format(new Date(m.getSendDate())));
            // if(isContinue) linearLayoutSender.removeAllViews();
        }
    }

    public static class ViewHolderPhoto extends RecyclerView.ViewHolder{

        private int type;
        private TextView txtSendDate;
        private ImageView imageView;

        public ViewHolderPhoto(@NonNull View itemView,int type) {
            super(itemView);

            // configure here
            txtSendDate = itemView.findViewById(R.id.textViewSendDate);
            imageView = itemView.findViewById(R.id.imageViewPhoto);
        }

        public void bindType(boolean isContinue,AnipalMessage m){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            txtSendDate.setText(""+dateFormat.format(new Date(m.getSendDate())));
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if(messages.get(position).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID())){
            type = MESSAGE_OUT; // +1
        }else{
            type=MESSAGE_IN; // +2
        }

        if(messages.get(position).getType().equals(MessageType.TEXT_MESSAGE)){
            type*=1;
        }else{
            type*=-1;
        }

        return type;
    }

    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        // Understand message type, if it is negative.
        // it is a photo message otherwise it is text message
        // if it is a photo message do viewType*=-1 for finding message_in or out.
        MessageType type = MessageType.TEXT_MESSAGE;
        if(viewType<0) {
            type = MessageType.PHOTO_MESSAGE;
            viewType*=-1;
        }
        switch (viewType){
            case MESSAGE_IN:
                if(type.equals(MessageType.PHOTO_MESSAGE)) view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_photo,parent,false);
                else view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message,parent,false);
                return new ViewHolderMessage(view,MESSAGE_IN);
            case MESSAGE_OUT:
                if(type.equals(MessageType.PHOTO_MESSAGE)) view=LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_photo_out,parent,false);
                else view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_out,parent,false);
                return new ViewHolderMessage(view,MESSAGE_OUT);
        }
        // no one works case.
        view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message,parent,false);
        return new ViewHolderMessage(view,0);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMessage holder, int position) {
        boolean isContinue = false;

        if(position>0 && (messages.get(position).getReceiverUUID().equals(messages.get(position-1).getReceiverUUID())))
            isContinue = true;

        holder.bindType(isContinue,messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
