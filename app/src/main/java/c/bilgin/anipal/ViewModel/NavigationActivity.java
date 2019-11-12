package c.bilgin.anipal.ViewModel;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import c.bilgin.anipal.R;
import c.bilgin.anipal.ViewModel.Account.AnipalAccountFragment;
import c.bilgin.anipal.ViewModel.Account.AnipalExploreFragment;
import c.bilgin.anipal.ViewModel.Message.AnipalMessagesFragment;
import c.bilgin.anipal.ViewModel.Post.AnipalAddPostFragment;
import c.bilgin.anipal.ViewModel.Post.AnipalHomeFragment;

public class NavigationActivity extends AppCompatActivity {

    // private FrameLayout frameLayout;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;

    // Create all objects here.
    // You can use singleton design pattern for all of them

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_messages:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout, new AnipalMessagesFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_explore:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalExploreFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_add_post:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalAddPostFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_account:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalAccountFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_home:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalHomeFragment());
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // frameLayout = findViewById(R.id.main_frame_layout);
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(R.id.main_frame_layout,new AnipalHomeFragment());
        fragmentTransaction.commit();
        navView.getMenu().findItem(R.id.navigation_home).setChecked(true);

    }

}
