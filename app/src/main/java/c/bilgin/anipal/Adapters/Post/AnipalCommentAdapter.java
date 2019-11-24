package c.bilgin.anipal.Adapters.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.util.List;

import c.bilgin.anipal.Model.Post.AnipalPostComment;
import c.bilgin.anipal.R;

public class AnipalCommentAdapter extends RecyclerView.Adapter<AnipalCommentAdapter.ViewHolderComment> {

    private List<AnipalPostComment> comments;
    private Context mContext;


    public AnipalCommentAdapter(Context context,List<AnipalPostComment> comments){
        this.comments = comments;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_anipal_comment,parent,false);
        ViewHolderComment vh = new ViewHolderComment(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComment holder, int position) {
        AnipalPostComment apc = comments.get(position);
        holder.bindType(apc);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolderComment extends RecyclerView.ViewHolder{

        private TextView textViewComment,textViewUploadTime, textViewFullname;

        public ViewHolderComment(@NonNull View itemView) {
            super(itemView);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewUploadTime = itemView.findViewById(R.id.textViewUploadTime);
            textViewFullname = itemView.findViewById(R.id.textViewFullname);
        }

        public void bindType(AnipalPostComment apc){
            textViewComment.setText(apc.getComment());
            textViewFullname.setText(apc.getSenderName());

            long milliSeconds = (apc.getTimestamp() - Timestamp.now().toDate().getTime());
            double minutes = milliSeconds * (1.66666667 / 100000);

            int m = (int)(Math.abs(minutes));
            if(m<60) textViewUploadTime.setText(""+m+" dakika önce");
            else if(m<1440)textViewUploadTime.setText(""+(m/60) +" saat önce");
            else if(m<10080)textViewUploadTime.setText(""+(m/1440)+" gün önce");
            else if(m<43200)textViewUploadTime.setText(""+(m/10080)+" hafta önce");
            else textViewUploadTime.setText(""+(m/41760)+ "ay önce");
        }
    }
}
