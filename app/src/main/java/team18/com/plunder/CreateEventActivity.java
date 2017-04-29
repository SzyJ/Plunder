package team18.com.plunder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final EditText etEventName = (EditText) findViewById(R.id.etEventName);
        final EditText etDescription = (EditText) findViewById(R.id.etDescription);
        final EditText etStartDate = (EditText) findViewById(R.id.etStartDate);
        final Spinner huntSpinner = (Spinner) findViewById(R.id.HuntSpinner);
        final CompoundButton PrivateSwitch = (CompoundButton) findViewById(R.id.PrivateSwitch);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MMM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                etStartDate.setText(sdf.format(myCalendar.getTime()));

            }

        };


        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateEventActivity.this, datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        PrivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton Switch, boolean isChecked) {
                if (isChecked) {
                    etPassword.setVisibility(View.VISIBLE);
                } else {
                    etPassword.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


}
