package team18.com.plunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Waypoint;

/**
 * Created by Szymon Jackiewicz on 25/3/2017.
 */

public class CreateHunt extends AppCompatActivity implements OnMapReadyCallback {

    private String ADD_HUNT_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/add_hunt.php";

    private final float DEFAULT_CAMERA_ZOOM = 15f;

    private int pointsAdded = 0;

    private EditText descriptionInput;
    private TextView statusText;
    private GoogleMap mMap;

    private Button placePickerButton;
    private Button addPointAndSave;
    private Button addAnotherPointButton;

    private ImageView pickerButtonIcon;
    private ImageView clueInputIcon;

    private final int PLACE_PICKER_REQUEST = 1;

    private String huntName;

    private Hunt hunt;

    private String descriptionText = null;
    private LatLng selectedWaypoint = null;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.waypoint_selector_map);
        mapFragment.getMapAsync(this);

        initialize();
        setTitle(getString(R.string.title_prefix) + ": " + huntName);

        /****************************************/
        /* Listener for description Input field */
        /****************************************/

        descriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                descriptionText = descriptionInput.getText().toString();
                if (descriptionText.isEmpty()) {
                    descriptionInput.setError(getString(R.string.error_empty_description));
                }
                updateIcons();
            }
        });

        /******************************/
        /* Button and textInput icons */
        /******************************/
        pickerButtonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWaypointSelected()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.selected_waypoint_toast),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.unselected_waypoint_toast),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        clueInputIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDescriptionSelected()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.legal_description_toast),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.illegal_description_toast),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /************************/
        /* Place Picker Button! */
        /************************/
        placePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAPlace();
            }
        });

        /****************************/
        /* Add Point and Save Hunt! */
        /* (Currently only returns to previous page) */
        /****************************/
        addPointAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWaypointSelected() && isDescriptionSelected()) {
                    // Create waypoint, create hunt and save it to the database and take user to hunt view screen
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    hunt.addWaypoint(new Waypoint(selectedWaypoint, descriptionText));

                    uploadHunt(view);
                }
            }
        });

        /*******************************/
        /* Add Point and refresh page! */
        /*******************************/
        addAnotherPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWaypointSelected() && isDescriptionSelected()) {
                    // Create waypint, add it to the hunt and refresh page for user to give next waypoint
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    View map = findViewById(R.id.waypoint_selector_map);

                    //map.getHeight();
                    hunt.addWaypoint(new Waypoint(selectedWaypoint, descriptionText));
                    statusText.setText(String.format("%s%d", getString(R.string.status_text_prefix) + " ", (++pointsAdded + 1)));
                    selectedWaypoint = null;
                    descriptionText = null;
                    mMap = MapUtil.resetCirlces(mMap, hunt);
                    descriptionInput.clearFocus();
                    descriptionInput.setText("");
                    placePickerButton.setText(getString(R.string.place_picker_button_text));
                    MapUtil.resetMapCamera(hunt, mMap, true);
                }
            }
        });

        // Buttons initially dsabled until info is provided
        addAnotherPointButton.setEnabled(false);
        addPointAndSave.setEnabled(false);
    }


    /**
     * Gets the hunt name from previous activity.
     * Initialaises the widgets on the screen by finsing by ID
     */
    private void initialize() {
        hunt = (Hunt) getIntent().getExtras().getSerializable("new_hunt_obj");
        huntName = hunt.getName();
        descriptionInput = (EditText) findViewById(R.id.hunt_clue_input);
        statusText = (TextView) findViewById(R.id.status_text);
        pickerButtonIcon = (ImageView) findViewById(R.id.place_picker_btn_icon);
        clueInputIcon = (ImageView) findViewById(R.id.clue_input_icon);
        placePickerButton = (Button) findViewById(R.id.place_picker_button);
        addPointAndSave = (Button) findViewById(R.id.save_hunt_button);
        addAnotherPointButton = (Button) findViewById(R.id.add_another_point_button);



        // Test!!!!!!!!!!!!
        /*
        huntName = getIntent().getExtras().getString("hunt_name");
        hunt = new Hunt(huntName);
        */
    }

    private boolean isDescriptionSelected() {
        return !(descriptionText == null || descriptionText.isEmpty());
    }

    private boolean isWaypointSelected() {
        return selectedWaypoint != null;
    }

    /**
     * Changes the icons by the user input area to refelct wether an input was given
     */
    private void updateIcons() {
        boolean activeButtons = true;
        if (isDescriptionSelected()) {
            clueInputIcon.setImageResource(R.drawable.ic_check_box_tick);
        } else {
            clueInputIcon.setImageResource(R.drawable.ic_check_box_outline);
            activeButtons = false;
        }
        if (isWaypointSelected()) {
            pickerButtonIcon.setImageResource(R.drawable.ic_check_box_tick);
        } else {
            pickerButtonIcon.setImageResource(R.drawable.ic_check_box_outline);
            activeButtons = false;
        }

        addAnotherPointButton.setEnabled(activeButtons);
        addPointAndSave.setEnabled(activeButtons);
    }

    /**
     * Google's Place Picker API methods
     */
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
                selectedWaypoint = place.getLatLng();
                mMap = MapUtil.resetCirlces(mMap, hunt, selectedWaypoint);
                try {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(selectedWaypoint).zoom(DEFAULT_CAMERA_ZOOM).build()));
                } catch (IllegalStateException ise) {
                    //Error can occur if the virtual keyboard is active and not enough room is found to fit all the waypoints on the screen
                    //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(selectedWaypoint).zoom(DEFAULT_CAMERA_ZOOM).build()));
                }

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
        updateIcons();
    }

    /**
     * Called when map is ready to be used
     * @param googleMap map object that is used in this class
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = MapUtil.styleMap(googleMap, this);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the back button is pressed.
     *
     * Used to warn users before gong back and losing their progress
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.go_back_alert_title));
        alertDialog.setMessage(getString(R.string.go_back_alert_text));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.go_back_alert_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CreateHunt.super.onBackPressed();
                    }
                }
        );
        alertDialog.show();
    }

    private void uploadHunt(final View view) {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            /*
            ProgressDialog dialog = ProgressDialog.show(v.getContext(), "",
                    "Please wait", true);*/
            @Override
            protected Void doInBackground(Integer... params) {

                //dialog.show();

                StringBuilder latitudeBuilder = new StringBuilder();
                StringBuilder longitudeBuilder = new StringBuilder();
                StringBuilder scanCodeBuilder = new StringBuilder();
                StringBuilder clueBuilder = new StringBuilder();

                String latitude = "";
                String longitude = "";
                String scanCode = "";
                String clue = "";


                String splitter = "";

                for (Waypoint wp : hunt.getWaypointList()) {
                    latitude += splitter;
                    longitude += splitter;
                    scanCode += splitter;
                    clue += splitter;

                    latitude += String.format("%.5f", wp.getCoords().latitude);
                    longitude += String.format("%.5f", wp.getCoords().longitude);
                    scanCode += wp.getScanCode();
                    clue += wp.getDescription();

                    splitter = "" + ((char)007);
                }



                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("admin", "59065c66d596c")
                        .add("name", huntName)
                        .add("latitude", latitude)
                        .add("longitude", longitude)
                        .add("scan_code", scanCode)
                        .add("clue", clue)

                        .build();
                Request request = new Request.Builder()
                        .url(ADD_HUNT_URL)
                        .post(formBody)
                        .build();


                //"lat:" + latitude + "| lon:" + longitude + "| scan:" + scanCode + "| clue" + clue


                try {
                    okhttp3.Response response = client.newCall(request).execute();

                    //Snackbar.make(v,response.body().string() , Snackbar.LENGTH_INDEFINITE)
                    //.setAction("Action", null).show();

                    System.out.println("Response: " + response.body().string());
                    System.out.println("admin: 59065c66d596c");
                    System.out.println("name: " + huntName);
                    System.out.println("latitude: " + latitude);
                    System.out.println("longitude: " + longitude);
                    System.out.println("scan_code: " + scanCode);
                    System.out.println("clue: " + clue);


                    JSONObject obj = new JSONObject(response.body().string());
                    Boolean success = obj.getBoolean("success");
                    String huntID = obj.getString("hunt_id");

                    if (success) {
                        Intent intent = new Intent(CreateHunt.this, ViewHunt.class);
                        intent.putExtra("hunt_obj", hunt);
                        startActivity(intent);
                    } else {
                        //warning.show();
                        Snackbar.make(view, "An error has occured, Please try again later", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // End of content reached
                    e.printStackTrace();
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                    Intent intent = new Intent(CreateHunt.this, ViewHunt.class);
                    intent.putExtra("hunt_obj", hunt);
                    startActivity(intent);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //dialog.hide();
            }
        };

        task.execute();
    }

}
