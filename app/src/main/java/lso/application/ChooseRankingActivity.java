package lso.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseRankingActivity extends AppCompatActivity {
    private ImageButton return_button;
    private Button month_ranking_button;
    private Button previous_month_ranking_button;
    private Button all_time_ranking_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ranking_activity);

        return_button = (ImageButton)findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRankingActivity.this, MainActivity.class));
            }
        });

        month_ranking_button = (Button)findViewById(R.id.month_ranking_button);
        month_ranking_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(ChooseRankingActivity.this, MonthRankingActivity.class));
            }
        });

        previous_month_ranking_button = (Button)findViewById(R.id.previous_month_ranking_button);
        previous_month_ranking_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(ChooseRankingActivity.this, PreviousMonthRankingActivity.class));
            }
        });

        all_time_ranking_button = (Button)findViewById(R.id.all_time_ranking_button);
        all_time_ranking_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(ChooseRankingActivity.this, AllTimeRankingActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChooseRankingActivity.this, MainActivity.class));
    }
}
