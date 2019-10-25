package c.bilgin.anipal.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import c.bilgin.anipal.R;


public class AnipalAccountFragment extends Fragment {

    private TextView textViewFullname, textViewLevel;

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
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_account,null);
        textViewFullname = linearLayout.findViewById(R.id.textViewFullname);
        textViewLevel = linearLayout.findViewById(R.id.textViewLevel);

        textViewFullname.setText(MainActivity.currentUser.getFirstName() + " "+MainActivity.currentUser.getLastName());
        textViewLevel.setText(MainActivity.currentUser.getLevel().getAnipalLevel().toString());
        return linearLayout;
    }
}
