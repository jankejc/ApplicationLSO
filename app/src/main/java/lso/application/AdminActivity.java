package lso.application;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private ImageButton return_button;
    private ImageView altar_add_button;
    private ImageView service_add_button;
    private ImageView altar_delete_button;
    private ImageView service_delete_button;
    private DatabaseReference mDatabase;
    private String fullname;
    private String fullname_to_delete;
    private String service_name;
    private String service_name_to_delete;
    private Integer service_points;
    private static final String db_url = "https://lso-application-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        return_button = (ImageButton) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, MainActivity.class));
            }
        });

        altar_add_button = (ImageView) findViewById(R.id.altar_add_button);
        altar_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                altarAddButtonOnClick();
            }
        });

        altar_delete_button = (ImageView) findViewById(R.id.altar_delete_button);
        altar_delete_button.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                altarDeleteButtonOnClick();
            }
        }));

        service_add_button = (ImageView) findViewById(R.id.service_add_button);
        service_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceAddButtonOnClick();
            }
        });

        service_delete_button = (ImageView) findViewById(R.id.service_delete_button);
        service_delete_button.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceDeleteButtonOnClick();
            }
        }));

    }

    // OPERATIONS
    private void writeNewAltarBoy(String fullname) {
        mDatabase = FirebaseDatabase.getInstance(db_url).getReference();
        AltarBoy altar_boy = new AltarBoy(fullname);
        mDatabase.child("altar_boys").child(fullname).setValue(altar_boy);
        Toast.makeText(AdminActivity.this, "Poprawnie dodano ministranta.", Toast.LENGTH_SHORT).show();
    }

    private void writeNewService(String service_name, Integer service_points) {
        mDatabase = FirebaseDatabase.getInstance(db_url).getReference();
        Services service = new Services(service_name, service_points);
        mDatabase.child("types_of_celebrations").child(service_name).setValue(service);
        Toast.makeText(AdminActivity.this, "Poprawnie dodano uroczystość.", Toast.LENGTH_SHORT).show();
    }

    private void deleteService(String service_name_to_delete) {
        mDatabase = FirebaseDatabase.getInstance(db_url).getReference();
        mDatabase.child("types_of_celebrations").child(service_name_to_delete).removeValue();
        Toast.makeText(AdminActivity.this, "Poprawnie usunięto uroczystość.", Toast.LENGTH_SHORT).show();
    }

    private void deleteAltarBoy(String delete_boy_name) {
        mDatabase = FirebaseDatabase.getInstance(db_url).getReference();
        mDatabase.child("altar_boys").child(delete_boy_name).removeValue();
        Toast.makeText(AdminActivity.this, "Poprawnie usunięto ministranta.", Toast.LENGTH_SHORT).show();
    }

    //BUTTONS
    private void altarAddButtonOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Wprowadź imię i nazwisko:");

        final EditText fullname_input = new EditText(AdminActivity.this);
        fullname_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(fullname_input);

        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fullname = fullname_input.getText().toString();
                if (fullname.length() == 0)
                    Toast.makeText(AdminActivity.this, "Wprowadź imię i nazwisko.", Toast.LENGTH_SHORT).show();
                else
                    writeNewAltarBoy(fullname);
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

    public void altarDeleteButtonOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Imie i nazwisko do usunięcia:");

        final EditText delete_fullname_input = new EditText(AdminActivity.this);
        delete_fullname_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(delete_fullname_input);

        builder.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fullname_to_delete = delete_fullname_input.getText().toString();
                if (fullname_to_delete.length() == 0)
                    Toast.makeText(AdminActivity.this, "Wprowadź imię i nazwisko.", Toast.LENGTH_SHORT).show();
                else
                    deleteAltarBoy(fullname_to_delete);
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

    private void serviceAddButtonOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Wprowadź nazwę uroczystości:");

        final EditText service_input = new EditText(AdminActivity.this);
        service_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(service_input);

        builder.setPositiveButton("Punkty", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                service_name = service_input.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Wprowadź liczbę punktów:");

                final EditText points_input = new EditText(AdminActivity.this);
                points_input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(points_input);

                builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        service_points = Integer.parseInt(points_input.getText().toString());

                        if (service_name.length() == 0 || service_points == 0)
                            Toast.makeText(AdminActivity.this, "Wprowadź nazwę uroczystości i punkty.", Toast.LENGTH_SHORT).show();
                        else
                            writeNewService(service_name, service_points);
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
        });

        builder.setNegativeButton("Cofnij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void serviceDeleteButtonOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Nazwa uroczystości do usunięcia:");

        final EditText delete_service_name_input = new EditText(AdminActivity.this);
        delete_service_name_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(delete_service_name_input);

        builder.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                service_name_to_delete = delete_service_name_input.getText().toString();
                if (service_name_to_delete.length() == 0)
                    Toast.makeText(AdminActivity.this, "Wprowadź nazwę uroczystości.", Toast.LENGTH_SHORT).show();
                else
                    deleteService(service_name_to_delete);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminActivity.this, MainActivity.class));
    }
}
