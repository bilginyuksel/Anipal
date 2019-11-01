package c.bilgin.anipal.ViewModel;

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

import java.util.ArrayList;
import java.util.List;

import c.bilgin.anipal.Adapters.AnipalPostAdapter;
import c.bilgin.anipal.Model.Firebase.AnipalFirebase;
import c.bilgin.anipal.Model.Post.AnipalAbstractPost;
import c.bilgin.anipal.R;

public class AnipalHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    public static AnipalPostAdapter postAdapter;
    private List<AnipalAbstractPost> posts;
    private Context mContext;
    private AnipalFirebase anipalFirebase;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        posts = new ArrayList<>();
        // Create random posts.
        // Firebase post GET operation here...
        // Also get posts according to user.
        postAdapter = new AnipalPostAdapter(mContext,posts);
        anipalFirebase.getPosts(posts,postAdapter);
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
}
