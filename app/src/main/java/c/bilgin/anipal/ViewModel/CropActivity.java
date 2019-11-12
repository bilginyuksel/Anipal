package c.bilgin.anipal.ViewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImageView;

import c.bilgin.anipal.R;

public class CropActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        cropImageView = findViewById(R.id.cropImageView);
        btn = findViewById(R.id.btn);
        cropImageView.setImageResource(R.drawable.anipallogo);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                cropImageView.setImageBitmap(result.getBitmap());
            }
        });
    }
}
