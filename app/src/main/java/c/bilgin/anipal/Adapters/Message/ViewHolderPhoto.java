package c.bilgin.anipal.Adapters.Message;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.util.DisplayMetrics;
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
    private Context mContext;

    public ViewHolderPhoto(@NonNull View itemView,int type) {
        super(itemView,type);
        this.txtSendDate = itemView.findViewById(R.id.txtSendDate);
        this.imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        mContext = itemView.getContext();
    }

    @Override
    public void bindType(AnipalMessage m) {

        photoMessage = (PhotoMessage) m;

        LinearLayout.LayoutParams i = new LinearLayout.LayoutParams(imageViewPhoto.getLayoutParams());

        // DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int max_height = 800;

        if(photoMessage.getHeight()>max_height){
            int coefficient = photoMessage.getHeight()/max_height;
            int width  = photoMessage.getWidth() / coefficient;
            int height = photoMessage.getHeight() / coefficient;
            i.height = height;
            i.width = width;
            imageViewPhoto.setLayoutParams(i);
        }
        Picasso.get().load(photoMessage.getPhotoURL()).fit().into(imageViewPhoto);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        txtSendDate.setText(""+dateFormat.format(new Date(photoMessage.getSendDate())));

        imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open dialog show pictures real size and also save it to local machine
                Dialog d = new Dialog(mContext);
                ImageView v = new ImageView(d.getContext());
                v.setMinimumHeight(photoMessage.getHeight());
                v.setMinimumWidth(photoMessage.getWidth());
                Picasso.get().load(photoMessage.getPhotoURL()).into(v);
                d.setContentView(v);
                d.show();
            }
        });
    }
}
