package c.bilgin.anipal.Ui.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import c.bilgin.anipal.Adapters.Message.AnipalMessageAdapter;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.Model.Message.PhotoMessage;
import c.bilgin.anipal.Model.Message.TextMessage;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalMessageActivity extends AppCompatActivity {

    private TextView textfullname;
    private ImageButton imageButtonBack, imageButtonSendMessage, imageButtonPickFromGallery;
    private String userFullname, userUUID, userPhotoURL;
    private EditText editTextMessage;
    private final static int GET_PICTURE = 10;
    private boolean isFirst;
    private RecyclerView recyclerView;
    private List<AnipalMessage> messages;
    private AnipalMessageAdapter adapter;



    private void firstMessageStuff(){
        AnipalChatRoom r = new AnipalChatRoom(userUUID,userFullname,userPhotoURL);
        AnipalChatRoom r2 = new AnipalChatRoom(MainActivity.currentUser.getUserUUID(),
         MainActivity.currentUser.getFirstName()+" "+MainActivity.currentUser.getLastName()
        ,MainActivity.currentUser.getPhotoURL());
        createChatRooms(r,r2);
        isFirst = false;
    }

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

                AnipalMessage m = new TextMessage(userUUID,MainActivity.currentUser.getUserUUID(),message );

                // if this is a first message
                // create chatrooms
                if(isFirst) {
                    firstMessageStuff();
                }

                sendMessage(m);
                updateUI();
            }
        });
    }

    /*private void readMessage(AnipalMessage m){
        // control this function...
        if(m.getReceiverUUID().equals(MainActivity.currentUser.getUserUUID())){
            Map<String,Object> map = new HashMap<>();
            map.put("read",true);
            FirebaseDatabase.getInstance().getReference("Messages").child(m.getMessageUUID()).updateChildren(map);
        }
    }*/

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
        adapter = new AnipalMessageAdapter(AnipalMessageActivity.this, messages);
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
                startActivityForResult(i,GET_PICTURE);
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
                    if(snapshot.hasChild("photoURL"))
                        messages.add(snapshot.getValue(PhotoMessage.class));
                    else
                        messages.add(snapshot.getValue(TextMessage.class));
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messages.size());
                }

                /* When all messages downloaded
                 No need to do all that stuff. Counter is easy.
                 for(int i = messages.size()-1;i>-1;i--){
                     if(messages.get(i).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()) && messages.get(i).isRead()) break;
                     if(messages.get(i).getReceiverUUID().equals(MainActivity.currentUser.getUserUUID()) && !messages.get(i).isRead()) readMessage(messages.get(i));
                 }*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
            if (requestCode==GET_PICTURE){

                Uri uri = data.getData();

                // convert uri to bitmap
                try {
                    Bitmap map  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    final int width = map.getWidth();
                    final int height = map.getHeight();
                    map.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                    byte bytes[] = byteArrayOutputStream.toByteArray();

                    String uid = UUID.randomUUID().toString();
                    uid += ".jpeg";
                    FirebaseStorage.getInstance().getReference("Messages").child(uid).putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String photoURL = task.getResult().toString();
                                    AnipalMessage m = new PhotoMessage(userUUID
                                            ,MainActivity.currentUser.getUserUUID(),photoURL,width,height);
                                    m.setMessage("Fotoğraf");
                                    if(isFirst)firstMessageStuff();

                                    sendMessage(m);
                                }
                            });
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    }
}
