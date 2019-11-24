package c.bilgin.anipal.Ui.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.bilgin.anipal.Adapters.Post.AnipalCommentAdapter;
import c.bilgin.anipal.Adapters.Post.AnipalPostAdapter;
import c.bilgin.anipal.Model.Post.AnipalPostComment;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class CommentActivity extends AppCompatActivity {

    private String postUUID, receiverUUID;
    private Map<String, AnipalPostComment> comments;
    private ImageButton imageButtonBack;
    private EditText editTextComment;
    private TextView textViewSendComment;
    private AnipalCommentAdapter adapter;
    private RecyclerView recyclerViewComments;
    private List<AnipalPostComment> listComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initialize();

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        textViewSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextComment.getText().toString().isEmpty())return;
                makeComment(new AnipalPostComment(postUUID
                        , MainActivity.currentUser.getUserUUID()
                        , receiverUUID
                        ,MainActivity.currentUser.getFirstName()
                        ,editTextComment.getText().toString()));
                editTextComment.getText().clear();
            }
        });
    }


    private void initialize(){

        imageButtonBack = findViewById(R.id.imageButtonBack);
        editTextComment = findViewById(R.id.editTextComment);
        textViewSendComment = findViewById(R.id.textViewSendComment);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);

        postUUID = getIntent().getStringExtra("postUUID");
        receiverUUID = getIntent().getStringExtra("receiverUUID");

        comments = (Map<String,AnipalPostComment>)getIntent().getSerializableExtra("comments");
        listComments = new ArrayList<>(comments.values());
        adapter = new AnipalCommentAdapter(this,listComments);

        recyclerViewComments.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewComments.setLayoutManager(manager);
    }

    private void makeComment(AnipalPostComment comment){
        // update comment
        listComments.add(comment);
        adapter.notifyDataSetChanged();

        Map<String,Object> map = new HashMap<>();
        map.put(comment.getCommentUUID(),comment);

        FirebaseDatabase.getInstance().getReference("Posts").child(comment.getPostUUID())
                .child("comments").updateChildren(map);
    }
}
