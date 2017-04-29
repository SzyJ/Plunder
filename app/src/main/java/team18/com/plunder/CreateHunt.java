package team18.com.plunder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
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

import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Waypoint;

/**
 * Created by Szymon Jackiewicz on 25/3/2017.
 */

public class CreateHunt extends AppCompatActivity implements OnMapReadyCallback {

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
                    //CreateHunt.super.onBackPressed();

                    Intent intent = new Intent(getApplicationContext(), ViewHunt.class);
                    intent.putExtra("hunt_obj", hunt);
                    startActivity(intent);
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
}
