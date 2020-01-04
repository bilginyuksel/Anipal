package c.bilgin.anipal.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

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
    public static String LIVING_FRAGMENT;

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
                    LIVING_FRAGMENT = "MESSAGE";
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_explore:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalExploreFragment());
                    LIVING_FRAGMENT = "EXPLORE";
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_add_post:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalAddPostFragment());
                    LIVING_FRAGMENT = "POST";
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_account:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,new AnipalAccountFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_home:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,HomeFragment.getInstance());
                    LIVING_FRAGMENT = "HOME";
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
        fragmentTransaction.add(R.id.main_frame_layout,HomeFragment.getInstance());
        fragmentTransaction.commit();
        LIVING_FRAGMENT = "HOME";
        navView.getMenu().findItem(R.id.navigation_home).setChecked(true);

    }


    @Override
    public void onBackPressed() {
        // smile
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Çıkış");
        builder.setMessage("Hesabınızdan çıkış yapmak istiyor musunuz ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // forget shared preferences // delete temp db

                FirebaseAuth.getInstance().signOut();
                // Facebook logout
                LoginManager.getInstance().logOut();
                // Google logout
                MainActivity.mGoogleSignInClient.signOut();

                // maybe it can't clean look how we did that
                MainActivity.sharedPreferences.edit().clear().commit();
                Intent i1 = new Intent(NavigationActivity.this, MainActivity.class);
                // kill the old fragments.
                // and singleton objects.
                fragmentManager = null;
                fragmentTransaction = null;
                AnipalHomeFragment.getInstance().kill();
                AnipalMessagesFragment.getInstance().kill();
                HomeFragment.getInstance().kill();

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
