package c.bilgin.anipal.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import c.bilgin.anipal.Model.Message.AnipalMessage;
import c.bilgin.anipal.R;
import c.bilgin.anipal.Ui.Account.AnipalAccountFragment;
import c.bilgin.anipal.Ui.Account.AnipalExploreFragment;
import c.bilgin.anipal.Ui.Account.MainActivity;
import c.bilgin.anipal.Ui.Message.AnipalMessagesFragment;
import c.bilgin.anipal.Ui.Post.AnipalAddPostFragment;
import c.bilgin.anipal.Ui.Post.AnipalHomeFragment;

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
                    fragmentTransaction.replace(R.id.main_frame_layout, AnipalMessagesFragment.getInstance());
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
                    fragmentTransaction.replace(R.id.main_frame_layout,AnipalHomeFragment.getInstance());
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
        fragmentTransaction.add(R.id.main_frame_layout,AnipalHomeFragment.getInstance());
        fragmentTransaction.commit();
        navView.getMenu().findItem(R.id.navigation_home).setChecked(true);

    }


    @Override
    public void onBackPressed() {
        // smile
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Çıkış");
        builder.setMessage("Uygulamadan çıkış yapmak istiyor musunuz ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // forget shared preferences // delete temp db
                Intent i1 = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(i1);
            }
        });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });

        builder.show();
    }
}
