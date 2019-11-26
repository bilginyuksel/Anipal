package c.bilgin.anipal.Adapters.Message;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.Model.Message.PhotoMessage;
import c.bilgin.anipal.R;

public class ViewHolderPhoto extends ViewHolder{

    private TextView txtSendDate;
    private ImageView imageViewPhoto;
    private PhotoMessage photoMessage;

    public ViewHolderPhoto(@NonNull View itemView,int type) {
        super(itemView,type);
        this.txtSendDate = itemView.findViewById(R.id.txtSendDate);
        this.imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
    }

    @Override
    public void bindType(AnipalMessage m) {

        photoMessage = (PhotoMessage) m;

        LinearLayout.LayoutParams i = new LinearLayout.LayoutParams(imageViewPhoto.getLayoutParams());

        i.height = photoMessage.getHeight();
        i.width = photoMessage.getWidth();
        imageViewPhoto.setLayoutParams(i);
        imageViewPhoto.setMinimumWidth(photoMessage.getWidth());
        imageViewPhoto.setMinimumHeight(photoMessage.getHeight());

        Picasso.get().load(photoMessage.getPhotoURL()).fit().into(imageViewPhoto);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        txtSendDate.setText(""+dateFormat.format(new Date(photoMessage.getSendDate())));
    }
}
