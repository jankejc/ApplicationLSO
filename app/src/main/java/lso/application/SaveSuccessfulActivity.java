package lso.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SaveSuccessfulActivity extends AppCompatActivity {
    private ImageButton return_btn_from_succ_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_successful_activity);

        return_btn_from_succ_save = (ImageButton)findViewById(R.id.return_btn_from_succ_save);
        return_btn_from_succ_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveSuccessfulActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SaveSuccessfulActivity.this, MainActivity.class));
    }
}
