package c.bilgin.anipal.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.maps.MapFragment;

import c.bilgin.anipal.Ui.MapsFragment;
import c.bilgin.anipal.Ui.Post.AnipalHomeFragment;

public class HomePageAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;

    public HomePageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments = new Fragment[]{
                AnipalHomeFragment.getInstance(),
                MapsFragment.getInstance()
        };
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
