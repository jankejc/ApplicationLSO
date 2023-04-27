package lso.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button save_me_button;
    private ImageButton admin_button;
    private ImageView choose_ranking_button;
    private DatabaseReference mPostReference;
    private DatabaseReference mAltarBoysReference;
    private DatabaseReference mTypesOfServiceReference;
    private DatabaseReference mAdminPasswordReference;
    private DatabaseReference mIfMigratedReference;
    private Spinner altar_list_spinner;
    private Spinner service_list_spinner;
    private String saving_boy_name;
    private String picked_service_name;
    private Integer saving_boy_points;
    private Integer old_all_time_points;
    private Integer saving_service_points;
    private ArrayList<AltarBoy> altar_boys_list = new ArrayList<>();
    private ArrayList<Services> types_of_service_list = new ArrayList<>();
    private String admin_password;
    private String try_password;
    private AltarBoy altar_boys_array[];
    private static final String db_url = "https://lso-application-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPostReference = FirebaseDatabase.getInstance(db_url).getReference();

        admin_button = (ImageButton)findViewById(R.id.admin_button);
        admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminButtonOnClick();
            }
        });

        save_me_button = (Button)findViewById(R.id.save_me_button);
        save_me_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureToSaveMeDialog();
            }
        });

        choose_ranking_button = (ImageView)findViewById(R.id.choose_ranking_button);
        choose_ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChooseRankingActivity.class));
            }
        });

        mAltarBoysReference = mPostReference.child("altar_boys");
        ValueEventListener postAltarBoysListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                altar_boys_list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Log.d("TAG", name);
                    AltarBoy temp_boy = new AltarBoy(ds.child("fullname").getValue(String.class),
                            ds.child("this_month_points").getValue(Integer.class),
                            ds.child("all_time_points").getValue(Integer.class));
                    altar_boys_list.add(temp_boy);
                }
                altarBoysListSpinnerCreate();
                checkFlag();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        // add asynchronous listener
        mAltarBoysReference.addValueEventListener(postAltarBoysListener);

        mTypesOfServiceReference = mPostReference.child("types_of_celebrations");
        ValueEventListener postTypesOfServiceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Services temp_service = new Services(ds.child("name_of_service").getValue(String.class),
                            ds.child("how_many_points").getValue(Integer.class));
                    types_of_service_list.add(temp_service);
                }
                typesOfServiceSpinnerCreate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mTypesOfServiceReference.addValueEventListener(postTypesOfServiceListener);

        mAdminPasswordReference = mPostReference.child("admin_password");
        ValueEventListener postAdminPasswordListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                admin_password = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mAdminPasswordReference.addValueEventListener(postAdminPasswordListener);
    }

    private void checkFlag(){
        mIfMigratedReference = mPostReference.child("if_migrated");
        ValueEventListener postIfMigratedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String flag = dataSnapshot.getValue(String.class);
                if(flag.equals("fałsz"))
                    checkMigrationOfPoints();
                else if(flag.equals("prawda"))
                    checkChangeOfFlag();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        //add asynchronous listener
        mIfMigratedReference.addListenerForSingleValueEvent(postIfMigratedListener);
    }

    private void checkMigrationOfPoints(){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if(day == 1){
            migratePoints();
            changeOfFlag("prawda");
        }
    }

    private void checkChangeOfFlag(){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        //remember that we are GMT+2
        if(day == 2){
            changeOfFlag("fałsz");
        }
    }

    private void changeOfFlag(String option){
        HashMap map = new HashMap();
        map.put("if_migrated", option);
        mPostReference.updateChildren(map);
    }

    private void migratePoints(){
        altar_boys_array = new AltarBoy[altar_boys_list.size()];

        Iterator boys_iter = altar_boys_list.iterator();
        for(int i = 0; i < altar_boys_list.size(); i++){
            altar_boys_array[i] = (AltarBoy) boys_iter.next();
        }

        Iterator iter = altar_boys_list.iterator();
        for(int i = 0; i < altar_boys_list.size(); i++){
            Integer new_previous_points = altar_boys_array[i].getThis_month_points();
            Integer new_this_month_points = 0;
            DatabaseReference mUpdatePointsReference = mPostReference.child("altar_boys").child(altar_boys_array[i].getFullname());
            HashMap map = new HashMap();
            map.put("previous_month_points", new_previous_points);
            map.put("this_month_points", new_this_month_points);
            mUpdatePointsReference.updateChildren(map);
        }
    }

    private void adminButtonOnClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Wprowadź hasło:");

        final EditText password_input = new EditText(MainActivity.this);
        password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(password_input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try_password = password_input.getText().toString();
                if(try_password.equals(admin_password))
                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                else
                    Toast.makeText(MainActivity.this, "Złe hasło, nieładnie...", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Zaniechaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void altarBoysListSpinnerCreate(){
        ArrayList altar_boys_fullname = new ArrayList();
        Iterator i = altar_boys_list.iterator();

        while(i.hasNext()){
            AltarBoy temp_boy = (AltarBoy) i.next();
            altar_boys_fullname.add(temp_boy.getFullname());
        }

        altar_list_spinner = (Spinner) findViewById(R.id.altar_boys_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, altar_boys_fullname);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        altar_list_spinner.setAdapter(adapter);
        altar_list_spinner.setOnItemSelectedListener(this);
    }

    private void typesOfServiceSpinnerCreate(){
        ArrayList service_types_list = new ArrayList();
        Iterator i = types_of_service_list.iterator();

        while(i.hasNext()){
            Services temp_service = (Services) i.next();
            service_types_list.add(temp_service.getName_of_service());
        }

        service_list_spinner = (Spinner) findViewById(R.id.service_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, service_types_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_list_spinner.setAdapter(adapter);
        service_list_spinner.setOnItemSelectedListener(this);
    }

    private void getServicePoints(String service_name){
        Iterator i = types_of_service_list.iterator();
        while(i.hasNext()){
            Services temp_service = (Services) i.next();
            if(temp_service.getName_of_service() == service_name){
                saving_service_points = temp_service.getHow_many_points();
                break;
            }
        }
    }

    private void getBoyPoints(String saving_boy_name){
        Iterator i = altar_boys_list.iterator();
        while(i.hasNext()){
            AltarBoy temp_boy = (AltarBoy) i.next();
            if(temp_boy.getFullname() == saving_boy_name){
                saving_boy_points = temp_boy.getThis_month_points();
                old_all_time_points = temp_boy.getAll_time_points();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.getId() == R.id.altar_boys_spinner){
            saving_boy_name = parent.getItemAtPosition(pos).toString();
            getBoyPoints(saving_boy_name);
            //Toast.makeText(parent.getContext(), saving_boy_points.toString(), Toast.LENGTH_SHORT).show();

        }else if(parent.getId() == R.id.service_spinner){
            picked_service_name = parent.getItemAtPosition(pos).toString();
            getServicePoints(picked_service_name);
            //Toast.makeText(MainActivity.this, saving_service_points.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void sureToSaveMeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("Potwierdź wpisanie się na listę");

        builder.setPositiveButton("Potwierdzam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveMe();
            }
        });

        builder.setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveMe(){
        Integer new_boy_points = saving_boy_points + saving_service_points;
        Integer new_all_time_points = old_all_time_points + saving_service_points;
        DatabaseReference mUpdatePointsReference = mPostReference.child("altar_boys").child(saving_boy_name);
        HashMap map = new HashMap();
        map.put("this_month_points", new_boy_points);
        map.put("all_time_points", new_all_time_points);
        mUpdatePointsReference.updateChildren(map);
        startActivity(new Intent(MainActivity.this, SaveSuccessfulActivity.class));
        //Toast.makeText(MainActivity.this, "Zapisano, a teraz przygotuj się do wyjścia!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {}
}