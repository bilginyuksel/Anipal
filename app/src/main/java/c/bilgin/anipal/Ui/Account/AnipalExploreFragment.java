package c.bilgin.anipal.Ui.Account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import c.bilgin.anipal.Adapters.User.AnipalUserAdapter;
import c.bilgin.anipal.Adapters.OnItemClickListener;
import c.bilgin.anipal.Model.User.AnipalUser;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.NavigationActivity;

public class AnipalExploreFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerViewUsers;
    private AnipalUserAdapter anipalUserAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private List<AnipalUser> anipalUsers;
    private Context mContext;
    private TextView txtClear;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

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
        txtClear = linearLayout.findViewById(R.id.txtClear);
        anipalUsers = new ArrayList<>();
        anipalUserAdapter = new AnipalUserAdapter(getContext(), anipalUsers, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // check if the user is the user which uses the application right now
                // you have to manage that user to real profile page.
                NavigationActivity.fragmentTransaction = NavigationActivity.fragmentManager.beginTransaction();
                NavigationActivity.fragmentTransaction.replace(R.id.main_frame_layout,
                        anipalUsers.get(pos)
                                .getUserUUID().equals(MainActivity.currentUser.getUserUUID())?
                                new AnipalAccountFragment():
                                new AnipalFollowProfileFragment(anipalUsers.get(pos)));
                NavigationActivity.fragmentTransaction.commit();

            }
        });

        recyclerViewUsers.setAdapter(anipalUserAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewUsers.setLayoutManager(linearLayoutManager);

        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.getText().clear();
            }
        });


        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                anipalUsers.clear();
                anipalUserAdapter.notifyDataSetChanged();
                // Progress bar....
                loadData(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return linearLayout;
    }

    private void loadData(CharSequence charSequence){
        if(!charSequence.toString().isEmpty()){
            ref.orderByChild("fullname").startAt(charSequence.toString().toLowerCase()).endAt(charSequence.toString().toLowerCase()+"\uf8ff")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            anipalUsers.clear();
                            for(DataSnapshot s : dataSnapshot.getChildren()){
                                anipalUsers.add(s.getValue(AnipalUser.class));
                                anipalUserAdapter.notifyDataSetChanged();
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }
}
