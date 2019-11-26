package c.bilgin.anipal.Adapters.Message;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.Model.Message.TextMessage;
import c.bilgin.anipal.R;

public class ViewHolderText extends ViewHolder {

    private TextMessage textMessage;
    private TextView txtMessage, txtSendDate;

    public ViewHolderText(@NonNull View itemView,int type) {
        super(itemView,type);
        this.txtMessage = itemView.findViewById(R.id.txtMessage);
        this.txtSendDate = itemView.findViewById(R.id.txtSendDate);
    }

    @Override
    public void bindType(AnipalMessage m) {
        textMessage = (TextMessage)m;

        txtMessage.setText(textMessage.getMessage());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");

        txtSendDate.setText(""+dateFormat.format(new Date(m.getSendDate())));
    }
}
