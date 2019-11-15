package c.bilgin.anipal.ViewModel.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.Message.AnipalMessageAdapter;
import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.MainActivity;

public class AnipalMessageActivity extends AppCompatActivity {

    private TextView textfullname;
    private ImageButton imageButtonBack, imageButtonSendMessage, imageButtonPickFromGallery;
    private String userFullname, userUUID, userPhotoURL;
    private EditText editTextMessage;

    private boolean isFirstMessage = false;
    private RecyclerView recyclerView;
    private List<AnipalMessage> messages;
    private AnipalMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_message);

        // configure all
        initialize();

        if(messages.size()==0) isFirstMessage = true;

        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // control message here
                String message = editTextMessage.getText().toString();
                if(message.isEmpty()) // don't send it
                    return;

                AnipalMessage m = new AnipalMessage(userUUID,message );

                // if this is a first message
                // create chatrooms
                if(isFirstMessage) {
                    AnipalChatRoom r = new AnipalChatRoom(userUUID,userFullname,userPhotoURL); //sender chatroom
                    AnipalChatRoom r2 = new AnipalChatRoom(MainActivity.currentUser.getUserUUID()
                    ,MainActivity.currentUser.getFirstName() +" "+MainActivity.currentUser.getLastName()
                    ,MainActivity.currentUser.getPhotoURL()); // receiver chatroom
                    createChatRooms(r,r2);
                }

                sendMessage(m);
                updateUI();

            }
        });

    }

    private void initialize(){
        textfullname = findViewById(R.id.textfullname);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonSendMessage = findViewById(R.id.imageButtonSendMessage);
        imageButtonPickFromGallery = findViewById(R.id.imageButtonPickFromGallery);
        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerView = findViewById(R.id.recyclerViewMessages);

        /*
         * these intent models comes if you are trying to start a new chat
         * */
        userFullname = getIntent().getStringExtra("fullname");
        userUUID = getIntent().getStringExtra("uuid");
        userPhotoURL = getIntent().getStringExtra("photourl");

        messages = new ArrayList<>();
        adapter = new AnipalMessageAdapter(AnipalMessageActivity.this
                , messages,MainActivity.currentUser.getFirstName()+" "+MainActivity.currentUser.getLastName(),userFullname);
        loadMessages(messages,adapter);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);



        textfullname.setText(userFullname);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void createChatRooms(AnipalChatRoom r, AnipalChatRoom r2){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatRooms");
        // sender chatroom
        ref.child(MainActivity.currentUser.getUserUUID()).child(userUUID).setValue(r);
        // receiver chatroom
        ref.child(userUUID).child(MainActivity.currentUser.getUserUUID()).setValue(r2);
    }
    private void sendMessage(AnipalMessage m){
        // by the way upload the current status
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");
        ref.child(m.getMessageUUID()).setValue(m);
    }

    private void loadMessages(final List<AnipalMessage> messages, final AnipalMessageAdapter adapter){
        // We're on a chat room right now.
        // so load messages from a chatRoom
        Query q= FirebaseDatabase.getInstance().getReference("Messages").orderByChild("sendDate");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot  : dataSnapshot.getChildren()){
                    messages.add(snapshot.getValue(AnipalMessage.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(){
        editTextMessage.getText().clear();
        // messages.add();
    }
}
