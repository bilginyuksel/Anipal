package c.bilgin.anipal.Ui.Post;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.Collections;
import java.util.List;

import c.bilgin.anipal.Adapters.Post.AnipalPostAdapter;
import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.Model.Post.AnipalDonationPost;
import c.bilgin.anipal.Model.Post.AnipalPhotoPost;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.HomeFragment;

public class AnipalHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    public static AnipalPostAdapter postAdapter;
    private List<AnipalAbstractPost> posts;
    private Context mContext;
    private AnipalFirebase anipalFirebase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnMap;
    private boolean isLoading = false;

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

        postAdapter = new AnipalPostAdapter(getContext(),posts);
        loadPosts(posts);
    }

    public void kill(){
        instance = null;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_home,null);
        recyclerView = linearLayout.findViewById(R.id.recyclerViewPosts);
        swipeRefreshLayout = linearLayout.findViewById(R.id.swipeRefreshLayout);
        btnMap = linearLayout.findViewById(R.id.btnMap);
        anipalFirebase = new AnipalFirebase(mContext,"UserPosts");

        // posts = new ArrayList<>();
        // Create random posts.
        // Firebase post GET operation here...
        // Also get posts according to user.

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.getInstance().pager.setCurrentItem(1);
            }
        });

        // postAdapter = new AnipalPostAdapter(getContext(),posts);
        // anipalFirebase.getPosts(posts,postAdapter);
        recyclerView.setAdapter(postAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        postAdapter.setContext(getContext());

        // Update posts.
        synchronized (posts){
            postAdapter.notifyDataSetChanged();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                anipalFirebase.getPosts(posts,postAdapter);
                // Normaly and refreshing when all posts came !
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first_visible = linearLayoutManager.findFirstVisibleItemPosition();
                int last_visible = linearLayoutManager.findLastVisibleItemPosition();

                System.out.println("Last Visible : "+last_visible);
                System.out.println("Item Count : "+(postAdapter.getItemCount()-1));
                System.out.println("Is Loading : "+isLoading);
                if( last_visible == postAdapter.getItemCount()-1){
                    // it should'nt be async, it should wait.
                    if(!isLoading && posts.size()>0) {
                        loadMorePosts(posts);
                    }
                }

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
                .child(MainActivity.currentUser.getUserUUID()).orderByChild("timestamp")
                .limitToLast(5);
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

                Collections.reverse(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadMorePosts(final List<AnipalAbstractPost> posts){
        isLoading = true;
        /*
         * UserPosts
         *   - UserId
         *       - Post1
         *       - Post2
         *   - UserId2
         *   - UserId3*/
        final ArrayList<AnipalAbstractPost> tmpPosts = new ArrayList<>();
        Query q1 = FirebaseDatabase.getInstance().getReference("UserPosts")
                .child(MainActivity.currentUser.getUserUUID()).orderByChild("timestamp")
                .endAt(posts.get(posts.size()-1).getTimestamp()).limitToLast(5);
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
                    tmpPosts.add(post);
                }

                Collections.reverse(tmpPosts);
                tmpPosts.remove(0);
                if(tmpPosts.size()>0) posts.addAll(tmpPosts);
                postAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
