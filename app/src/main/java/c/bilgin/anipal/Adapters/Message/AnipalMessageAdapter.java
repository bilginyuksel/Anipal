package c.bilgin.anipal.Adapters.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.MainActivity;

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
        private TextView txtSender, txtMessage;
        private LinearLayout  linearLayoutSender;
        public ViewHolderMessage(@NonNull View itemView,int type) {
            super(itemView);
            this.type = type;

            linearLayoutSender = itemView.findViewById(R.id.linearLayoutSender);
            txtSender = itemView.findViewById(R.id.txtSender);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }

        public void bindType(boolean isContinue, AnipalMessage m){
            txtMessage.setText(m.getMessage());
            txtSender.setText(type==MESSAGE_IN?host:guest);
            if(isContinue) linearLayoutSender.removeAllViews();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()) ? MESSAGE_OUT:MESSAGE_IN;
    }

    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case MESSAGE_IN:
                view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message,parent,false);
                return new ViewHolderMessage(view,MESSAGE_IN);
            case MESSAGE_OUT:
                view = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_message_out,parent,false);
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
