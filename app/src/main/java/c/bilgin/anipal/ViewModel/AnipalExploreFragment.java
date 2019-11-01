package c.bilgin.anipal.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

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

import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.AnipalUserAdapter;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;

public class AnipalExploreFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerViewUsers;
    private AnipalUserAdapter anipalUserAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private List<AnipalUser> anipalUsers;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_explore,null);
        recyclerViewUsers = linearLayout.findViewById(R.id.recyclerViewUsers);
        autoCompleteTextView = linearLayout.findViewById(R.id.autoCompleteTextView);
        anipalUsers = new ArrayList<>();
        anipalUserAdapter = new AnipalUserAdapter(getContext(), anipalUsers);

        recyclerViewUsers.setAdapter(anipalUserAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewUsers.setLayoutManager(linearLayoutManager);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.orderByChild("firstName").startAt(autoCompleteTextView.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot s : dataSnapshot.getChildren()){
                            anipalUsers.add(s.getValue(AnipalUser.class));
                            anipalUserAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*recyclerViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = recyclerViewUsers.getChildLayoutPosition(view);
                AnipalFollowProfileActivity.user = anipalUsers.get(i);
                Intent intent = new Intent(getActivity(),AnipalFollowProfileActivity.class);
                startActivity(intent);
            }
        });*/

        return linearLayout;
    }
}
