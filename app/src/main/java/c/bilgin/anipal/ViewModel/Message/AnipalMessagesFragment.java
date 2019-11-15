package c.bilgin.anipal.ViewModel.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.Message.AnipalChatRoomAdapter;
import c.bilgin.anipal.Adapters.Message.AnipalFreqRoomAdapter;
import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.Message.AnipalChatRoom;
import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.MainActivity;

public class AnipalMessagesFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private ImageButton im;
    private RecyclerView recyclerViewChatRooms, recyclerViewFreqRooms;
    private List<AnipalChatRoom> chatRooms;
    private AnipalChatRoomAdapter chatRoomAdapter;
    private AnipalFreqRoomAdapter freqRoomAdapter;
    private ScrollView scrollView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scrollView= (ScrollView) inflater.inflate(R.layout.fragment_messages,null);

        initialize();

        return scrollView;
    }


    private void initialize(){
        im = scrollView.findViewById(R.id.openUsers);
        recyclerViewChatRooms = scrollView.findViewById(R.id.chatRooms);
        recyclerViewFreqRooms = scrollView.findViewById(R.id.frequentRooms);

        chatRooms = new ArrayList<>();
        chatRoomAdapter = new AnipalChatRoomAdapter(getContext(), chatRooms, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // go to chat screen... with that chatroom
                // if we transfer chatrooms it will be so good !
                // because the chatrooms has the messages ! and we don't need to load again
                gotoChatRoom(chatRooms.get(pos));
            }
        });
        freqRoomAdapter = new AnipalFreqRoomAdapter(getContext(), chatRooms, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                gotoChatRoom(chatRooms.get(pos));
            }
        });
        loadChatRooms(chatRooms,chatRoomAdapter);

        // horizontal recycler view
        recyclerViewFreqRooms.setAdapter(freqRoomAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewFreqRooms.setLayoutManager(manager);

        // vertical recycler view
        recyclerViewChatRooms.setAdapter(chatRoomAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewChatRooms.setLayoutManager(linearLayoutManager);


        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go find a user to message
                Intent i = new Intent(getContext(),AnipalFindUsersToMessage.class);
                startActivity(i);
            }
        });
    }


    private void gotoChatRoom(AnipalChatRoom anipalChatRoom){
        Intent i = new Intent(getContext(),AnipalMessageActivity.class);
        i.putExtra("fullname",anipalChatRoom.getUserFullname());
        i.putExtra("uuid",anipalChatRoom.getUserUUID());
        i.putExtra("photourl",anipalChatRoom.getUserPhotoURL());
        startActivity(i);
    }
    private void loadChatRooms(final List<AnipalChatRoom> chatRooms, final AnipalChatRoomAdapter adapter){
        // now search chatRooms
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatRooms")
                .child(MainActivity.currentUser.getUserUUID());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRooms.clear();
                adapter.notifyDataSetChanged();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    chatRooms.add(snapshot.getValue(AnipalChatRoom.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
