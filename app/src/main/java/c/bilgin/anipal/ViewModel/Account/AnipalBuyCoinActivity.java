package c.bilgin.anipal.ViewModel.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import c.bilgin.anipal.R;

public class AnipalBuyCoinActivity extends AppCompatActivity {

    private ImageButton buttonEscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anipal_buy_coin);

        initialize();

        buttonEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        buttonEscape = findViewById(R.id.imageButtonBack);
    }
}
