package c.bilgin.anipal.Ui.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Adapters.User.AnipalUserAdapter;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalFindUsersToMessage extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private AnipalUserAdapter anipalUserAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private List<AnipalUser> anipalUsers;
    private ImageButton imageButtonBack;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_find_users_to_message);

        initialize();
    }

    private void initialize(){
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        anipalUsers = new ArrayList<>();
        anipalUserAdapter = new AnipalUserAdapter(this, anipalUsers, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // Open a chatroom when clicked
                gotoChatRoom(anipalUsers.get(pos));
            }
        });
        loadData(anipalUsers,anipalUserAdapter);
        recyclerViewUsers.setAdapter(anipalUserAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewUsers.setLayoutManager(linearLayoutManager);


        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void loadData(final List<AnipalUser> users,final AnipalUserAdapter adapter){
        /*
        * find users which this -> followers or following
        * I don't know if it is the efficient way of doing it.
        * */
        List<String> followers = MainActivity.currentUser.getFollowers();
        for(String s : followers){
            ref.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users.add(dataSnapshot.getValue(AnipalUser.class));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }
    private void gotoChatRoom(AnipalUser u){
        Intent i = new Intent(AnipalFindUsersToMessage.this,AnipalMessageActivity.class);
        i.putExtra("fullname",u.getFirstName()+" "+u.getLastName());
        i.putExtra("uuid",u.getUserUUID());
        i.putExtra("photourl",u.getPhotoURL());

        boolean first = true;
        boolean hasMessage = false;
        for(AnipalChatRoom r : AnipalMessagesFragment.chatRooms){
            if(r.getUserUUID().equals(u.getUserUUID())) {
                first = false;
                hasMessage = !r.getLastMessage().equals("");
            }
        }
        if(hasMessage) i.putExtra("first",first);
        startActivity(i);
    }
}
