package c.bilgin.anipal.Ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import c.bilgin.anipal.Adapters.HomePageAdapter;
import c.bilgin.anipal.R;

public class HomeFragment extends Fragment {
    private static HomeFragment instance = new HomeFragment();
    private HomeFragment(){ }

    public static HomeFragment getInstance(){
        return instance;
    }
    public ViewPager pager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewPager linearLayout = (ViewPager)inflater.inflate(R.layout.home_page_adapter,null);

        pager =linearLayout.findViewById(R.id.pager_linear_layout);
        pager.setAdapter(new HomePageAdapter(getChildFragmentManager()));

        return linearLayout;
    }
}
