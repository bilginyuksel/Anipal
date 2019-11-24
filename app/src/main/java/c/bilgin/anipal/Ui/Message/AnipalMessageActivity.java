package c.bilgin.anipal.Ui.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.bilgin.anipal.Adapters.Message.AnipalMessageAdapter;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalMessageActivity extends AppCompatActivity {

    private TextView textfullname;
    private ImageButton imageButtonBack, imageButtonSendMessage, imageButtonPickFromGallery;
    private String userFullname, userUUID, userPhotoURL;
    private EditText editTextMessage;

    private boolean isFirst;
    private RecyclerView recyclerView;
    private List<AnipalMessage> messages;
    private AnipalMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_message);

        // configure all
        initialize();


        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // control message here
                String message = editTextMessage.getText().toString();
                if(message.isEmpty()) // don't send it
                    return;

                AnipalMessage m = new AnipalMessage(userUUID,MainActivity.currentUser.getUserUUID(),message );

                // if this is a first message
                // create chatrooms
                if(isFirst) {
                    AnipalChatRoom r = new AnipalChatRoom(userUUID,userFullname,userPhotoURL); //sender chatroom
                    AnipalChatRoom r2 = new AnipalChatRoom(MainActivity.currentUser.getUserUUID()
                    ,MainActivity.currentUser.getFirstName() +" "+MainActivity.currentUser.getLastName()
                    ,MainActivity.currentUser.getPhotoURL()); // receiver chatroom
                    createChatRooms(r,r2);
                    isFirst = false;
                }

                sendMessage(m);
                updateUI();
            }
        });
    }

    private void readMessage(AnipalMessage m){
        // control this function...
        if(m.getReceiverUUID().equals(MainActivity.currentUser.getUserUUID())){
            Map<String,Object> map = new HashMap<>();
            map.put("read",true);
            FirebaseDatabase.getInstance().getReference("Messages").child(m.getMessageUUID()).updateChildren(map);
        }
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
        isFirst = getIntent().getBooleanExtra("first",true);

        messages = new ArrayList<>();
        adapter = new AnipalMessageAdapter(AnipalMessageActivity.this
                , messages,MainActivity.currentUser.getFirstName()+" "+MainActivity.currentUser.getLastName(),userFullname);
        loadMessages(messages,adapter);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.smoothScrollToPosition(messages.size());

        textfullname.setText(userFullname);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        imageButtonPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,10);
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
        Query q= FirebaseDatabase.getInstance().getReference("ChatRooms")
                .child(MainActivity.currentUser.getUserUUID()).child(userUUID)
                .child("messages").orderByChild("sendDate");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                adapter.notifyDataSetChanged();
                for(DataSnapshot snapshot  : dataSnapshot.getChildren()){
                    messages.add(snapshot.getValue(AnipalMessage.class));
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messages.size());
                }

                // When all messages downloaded
                // No need to do all that stuff. Counter is easy.
                // for(int i = messages.size()-1;i>-1;i--){
                    // if(messages.get(i).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()) && messages.get(i).isRead()) break;
                    // if(messages.get(i).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()) && !messages.get(i).isRead()) readMessage(messages.get(i));
                // }
                if(messages.size()!=0) readM();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readM(){
        Map<String,Object> map = new HashMap<>();
        map.put("isReadCounter",0);
        FirebaseDatabase.getInstance().getReference("ChatRooms")
                .child(MainActivity.currentUser.getUserUUID()).child(userUUID).updateChildren(map);
    }

    private void updateUI(){
        editTextMessage.getText().clear();
        // messages.add();
    }
}
