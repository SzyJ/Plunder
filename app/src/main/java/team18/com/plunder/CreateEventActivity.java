package team18.com.plunder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import team18.com.plunder.utils.Event;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Validator;

public class CreateEventActivity extends AppCompatActivity implements OnItemClickListener{

    Calendar myCalendar = Calendar.getInstance();
    private final String GET_HUNT_ARRAY_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/hunt_array.php";
    private final String ADD_EVENT_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/add_event.php";
    private ArrayList<String> hunt_names = new ArrayList<String>();
    private ArrayList<String> hunt_ids = new ArrayList<String>();
    private ArrayList<Map<String, String>> listTrans;
    private int huntSelected;
    EditText etHunt;
    ListPopupWindow huntList;
    private String eventId;
    private LatLng selectedStartLocation = null;
    private Button placePickerButton;
    private String startLat;
    private String startLng;

    private final int PLACE_PICKER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final EditText etEventName = (EditText) findViewById(R.id.etEventName);
        final EditText etDescription = (EditText) findViewById(R.id.etDescription);
        final EditText etStartDate = (EditText) findViewById(R.id.etStartDate);
        final EditText etPrize = (EditText) findViewById(R.id.etPrize);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        etHunt = (EditText) findViewById(R.id.etHunt);
        final Event event = (Event) getIntent().getExtras().getSerializable("new_event_obj");

        final CompoundButton PrivateSwitch = (CompoundButton) findViewById(R.id.PrivateSwitch);
        placePickerButton = (Button) findViewById(R.id.place_picker_button);
        final Button bSubmit = (Button) findViewById(R.id.bSubmit);

        etEventName.setText(event.getName());

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/mm/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                etStartDate.setText(sdf.format(myCalendar.getTime()));

            }

        };

        etStartDate.setOnClickListener(new OnClickListener() {
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

        placePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAPlace();
            }
        });

        loadData("59065529979bf");
        huntList = new ListPopupWindow(CreateEventActivity.this);
        huntList.setAdapter(new ArrayAdapter<>(CreateEventActivity.this, R.layout.list_item, hunt_names));
        huntList.setAnchorView(etHunt);
        huntList.setWidth(1000);
        huntList.setHeight(800);
        huntList.setModal(true);
        huntList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                huntSelected = huntList.getSelectedItemPosition();
            }
        });
        huntList.setOnItemClickListener(CreateEventActivity.this);
        etHunt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                huntList.show();
            }
        });

        bSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Validator validator = new Validator();
                validator.validateEvent(etEventName, etHunt, etStartDate, etDescription);

                if(validator.isValid()){
                    final String eventName = etEventName.getText().toString();
                    final String description = etDescription.getText().toString();
                    final String startDate = etStartDate.getText().toString();
                    final String prize = etPrize.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String huntId = hunt_ids.get(huntSelected);
                    final String userId = "59065529979bf";
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
                        final Date parsedDate = formatter.parse(startDate);
                        final long startEpoch = parsedDate.getTime();
                        final String startDateEpoch = Long.toString(startEpoch);

                        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
                            ProgressDialog dialog = ProgressDialog.show(CreateEventActivity.this, "",
                                    "Uploading Event", true);

                            @Override
                            protected Void doInBackground(Integer... params) {

                                dialog.create();
                                dialog.show();

                                OkHttpClient client = new OkHttpClient();
                                RequestBody formBody = new FormBody.Builder()
                                        .add("user_id", userId)
                                        .add("name", eventName)
                                        .add("hunt_id", huntId)
                                        .add("start_date", startDateEpoch)
                                        .add("description", description)
                                        .add("prize", prize)
                                        .add("password", password)
                                        .add("start_lat", startLat)
                                        .add("start_lng", startLng)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(ADD_EVENT_URL)
                                        .post(formBody)
                                        .build();
                                try {
                                    Response response = client.newCall(request).execute();
                                    JSONObject obj = new JSONObject(response.body().string());
                                    event.fillEvent(obj.getString("event_id"));
                                    Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                                    intent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAP);
                                    CreateEventActivity.this.startActivity(intent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                dialog.hide();
                            }
                        };
                        task.execute();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadData(final String userId) {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            ProgressDialog dialog = ProgressDialog.show(CreateEventActivity.this, "",
                    "Fetching your hunts...", true);
            @Override
            protected Void doInBackground(Integer... params) {

                dialog.show();

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("user_id", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(GET_HUNT_ARRAY_URL)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        hunt_names.add(object.getString("hunt_name"));
                        hunt_ids.add(object.getString("hunt_id"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.hide();
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etHunt.setText(hunt_names.get(position));
        huntList.dismiss();
    }
    private void pickAPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent= builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                selectedStartLocation = place.getLatLng();
                startLat = Double.toString(selectedStartLocation.latitude);
                startLng = Double.toString(selectedStartLocation.longitude);
                /*
                 * Displays contextual info about the selected location on the button.
                 * Provides nearest address when arbitrary locaton is chosen
                 * Provides place name if a place of interest is chosen.
                 */
                if (place.getAddress() == null) {
                    placePickerButton.setText(getString(R.string.context_address_not_found));
                } else {
                    placePickerButton.setText(getString(R.string.context_selected_waypoint) + ": " +
                            (place.getId().toUpperCase().startsWith("CH")
                                    ? place.getName()
                                    : (getString(R.string.context_address_prefix) + " " + place.getAddress())
                            )
                    );
                }

            }
        }
    }
}
