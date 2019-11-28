package c.bilgin.anipal.Ui.Post;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.Post.AnipalPostAdapter;
import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;

public class AnipalHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    public static AnipalPostAdapter postAdapter;
    private List<AnipalAbstractPost> posts;
    private Context mContext;
    private AnipalFirebase anipalFirebase;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static AnipalHomeFragment instance = null;

    public static AnipalHomeFragment getInstance(){
        if(instance == null)
            instance = new AnipalHomeFragment();

        return instance;
    }
    private AnipalHomeFragment(){
        // load posts here.
         if(posts == null)
             posts = new ArrayList<>();

         loadPosts(posts);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_home,null);
        recyclerView = linearLayout.findViewById(R.id.recyclerViewPosts);
        swipeRefreshLayout = linearLayout.findViewById(R.id.swipeRefreshLayout);
        anipalFirebase = new AnipalFirebase(mContext,"UserPosts");
        // posts = new ArrayList<>();
        // Create random posts.
        // Firebase post GET operation here...
        // Also get posts according to user.
        postAdapter = new AnipalPostAdapter(mContext,posts);
        // anipalFirebase.getPosts(posts,postAdapter);
        recyclerView.setAdapter(postAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                anipalFirebase.getPosts(posts,postAdapter);
                // Normaly and refreshing when all posts came !
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return linearLayout;
    }

    private void loadPosts(final List<AnipalAbstractPost> posts){
        /*
         * UserPosts
         *   - UserId
         *       - Post1
         *       - Post2
         *   - UserId2
         *   - UserId3*/
        Query q1 = FirebaseDatabase.getInstance().getReference("UserPosts")
                .child(MainActivity.currentUser.getUserUUID()).orderByChild("timestamp").limitToFirst(20);
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AnipalAbstractPost post ;
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    if(snap.hasChild("photoURL")){
                        // Photo post
                        post = snap.getValue(AnipalPhotoPost.class);
                        post.findUser(post.getUserUUID());
                    }else{
                        // Donation post
                        post = snap.getValue(AnipalDonationPost.class);
                        post.findUser(post.getUserUUID());
                    }
                    posts.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
