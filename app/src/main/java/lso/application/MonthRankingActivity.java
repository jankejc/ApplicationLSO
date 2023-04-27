package lso.application;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class MonthRankingActivity extends AppCompatActivity {
    private ImageButton return_button;
    private DatabaseReference mPostReference;
    private DatabaseReference mAltarBoysReference;
    private ArrayList<AltarBoy> altar_boys_list = new ArrayList<>();
    private AltarBoy sorted_altar_boys_array[];
    private TextView month_ranking_view;
    private static final String db_url = "https://lso-application-default-rtdb.europe-west1.firebasedatabase.app/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_ranking_activity);

        return_button = (ImageButton)findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonthRankingActivity.this, ChooseRankingActivity.class));
            }
        });

        month_ranking_view = (TextView)findViewById(R.id.month_ranking_view);

        getAltarBoysListAndCreateList();
    }

    private void getAltarBoysListAndCreateList(){
        mPostReference = FirebaseDatabase.getInstance(db_url).getReference();
        mAltarBoysReference = mPostReference.child("altar_boys");
        ValueEventListener postAltarBoysListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Log.d("TAG", name);
                    AltarBoy temp_boy = new AltarBoy(ds.child("fullname").getValue(String.class),
                            ds.child("this_month_points").getValue(Integer.class),
                            ds.child("this_month_points").getValue(Integer.class));
                    altar_boys_list.add(temp_boy);
                }
                createRanking();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        //add asynchronous listener
        mAltarBoysReference.addValueEventListener(postAltarBoysListener);
    }

    private void createRanking(){
        sorted_altar_boys_array = new AltarBoy[altar_boys_list.size()];

        Iterator boys_iter = altar_boys_list.iterator();
        for(int i = 0; i < altar_boys_list.size(); i++){
            sorted_altar_boys_array[i] = (AltarBoy) boys_iter.next();
        }

        quickSort(sorted_altar_boys_array, 0, altar_boys_list.size()-1);
        Integer last_points = -1;
        for (int i = 0; i < altar_boys_list.size(); i++){
            StringBuilder sb = new StringBuilder();

            if(sorted_altar_boys_array[i].getThis_month_points() != last_points){
                last_points = sorted_altar_boys_array[i].getThis_month_points();
                sb.append(i+1);
                sb.append(". ");
            }else
                sb.append("    ");

            sb.append(sorted_altar_boys_array[i].getFullname());
            sb.append(" ");
            sb.append(sorted_altar_boys_array[i].getThis_month_points());
            sb.append("\n");
            String s = sb.toString();
            month_ranking_view.append(s);
            month_ranking_view.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    private void quickSort(AltarBoy arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(AltarBoy arr[], int begin, int end) {
        int pivot = arr[end].getThis_month_points();
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j].getThis_month_points() >= pivot) {
                i++;

                AltarBoy swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        AltarBoy swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i+1;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MonthRankingActivity.this, ChooseRankingActivity.class));
    }
}
