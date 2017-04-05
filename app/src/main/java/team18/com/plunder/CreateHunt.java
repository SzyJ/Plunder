package team18.com.plunder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import team18.com.plunder.utils.Waypoint;

/**
 * Created by Szymon Jackiewicz on 25/3/2017.
 */

public class CreateHunt extends AppCompatActivity implements OnMapReadyCallback{


    private final int PRIMARY_CIRCLE_FILL_COLOUR = 0x30FFA000;
    private final int PRIMARY_CIRCLE_OUTLINE_COLOUR = 0xFFFF8F00;
    private final int SECONDARY_CIRCLE_FILL_COLOUR = 0x30BDBDBD;
    private final int SECONDARY_CIRCLE_OUTLINE_COLOUR = 0xFF757575;
    private final double CIRCLE_RADIUS = 100.0;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.waypoint_selector_map);
        mapFragment.getMapAsync(this);

        initialize();
        setTitle("New Hunt: " + huntName);

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
                if (/*descriptionText != null && */descriptionText.isEmpty()) {
                    descriptionInput.setError("Field cannot be left empty");
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
                    Toast.makeText(getApplicationContext(), "Waypoint selected, ready to add!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Waypoint is not selected, Pick a Place",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        clueInputIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDescriptionSelected()) {
                    Toast.makeText(getApplicationContext(), "This clue Looks Good!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "The Clue field cannot be left empty, enter a waypoint clue!",
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
                    CreateHunt.super.onBackPressed();
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


                    map.getHeight();
                    hunt.addWaypoint(new Waypoint(selectedWaypoint, descriptionText));
                    statusText.setText(String.format("%s%d", "Choose waypoint ", (++pointsAdded + 1)));
                    selectedWaypoint = null;
                    descriptionText = null;
                    resetCirlces();
                    descriptionInput.clearFocus();
                    descriptionInput.setText("");
                    placePickerButton.setText("Pick a Place");
                    resetMapCamera();
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
        huntName = getIntent().getExtras().getString("hunt_name");
        hunt = new Hunt(huntName);
        descriptionInput = (EditText) findViewById(R.id.hunt_clue_input);
        statusText = (TextView) findViewById(R.id.status_text);
        pickerButtonIcon = (ImageView) findViewById(R.id.place_picker_btn_icon);
        clueInputIcon = (ImageView) findViewById(R.id.clue_input_icon);
        placePickerButton = (Button) findViewById(R.id.place_picker_button);
        addPointAndSave = (Button) findViewById(R.id.save_hunt_button);
        addAnotherPointButton = (Button) findViewById(R.id.add_another_point_button);
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
                resetCirlces();
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
                placePickerButton.setText("Selected waypoint: " +
                        (place.getId().toUpperCase().startsWith("CH")
                                ? place.getName()
                                : ("near " + place.getAddress())
                        )
                );
            }
        }
        updateIcons();
    }


    private void resetMapCamera() {
        CameraUpdate camera;

        if (pointsAdded > 1) {

            /*
             * Relocates the Camera to show all currently selected waypoints
             * This will only trgger if more than one waypoint is selected
             */
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            // Add all waypoints to the builder
            for (Waypoint wp : hunt.getWaypointList()) {
                builder.include(wp.getCoords());
            }
            LatLngBounds bounds = builder.build();

            // Use builder with all the waypoints to generate new camera pos.
            int padding = 200;
            camera = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        } else {

            /*
             * If only one point is selected, camera is centered on it in the
             * same way as it would be when a new point is selected.
             */
            camera = CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(hunt.getWaypointList().get(0).getCoords()).zoom(DEFAULT_CAMERA_ZOOM).build());
        }


        /*
         * Error can occur if the virtual keyboard is active and not
         * enough room is found to fit all the waypoints on the screen.
         * (When more than one waypoint is on the map)
         */
        try {
            mMap.animateCamera(camera);
        } catch (IllegalStateException ise) {
            //mMap.moveCamera(camera);
        }
    }

    private void resetCirlces() {
        mMap.clear();
        int stepper = 1;
        for (Waypoint wp : hunt.getWaypointList()) {
            addCircle(wp.getCoords(), "Waypoint " + (stepper++) + ": \"" + wp.getDescription() + "\"", false);
        }
        if (selectedWaypoint != null) {
            addCircle(selectedWaypoint, "New Waypoint", true);
        }
    }

    /**
     * Draws a circle on the map and places a marker in the middle of it.
     * Circle radius and colours are defined as final veriables at the top.
     *
     * @param coords the coordinates for the centre of the new circle
     * @param description Text that is displayed when a marker is touched
     * @param primary true if place is selected but not yet added to bring attention to it.
     */
    private void addCircle(LatLng coords, String description, boolean primary) {
        CircleOptions circle = new CircleOptions();
        circle.center(coords);
        circle.radius(CIRCLE_RADIUS);
        MarkerOptions marker;
        if (primary) {
            circle.fillColor(PRIMARY_CIRCLE_FILL_COLOUR);
            circle.strokeColor(PRIMARY_CIRCLE_OUTLINE_COLOUR);
            marker = new MarkerOptions()
                    .position(coords)
                    .title(description)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location));
        } else {
            circle.fillColor(SECONDARY_CIRCLE_FILL_COLOUR);
            circle.strokeColor(SECONDARY_CIRCLE_OUTLINE_COLOUR);
            marker = new MarkerOptions()
                    .position(coords)
                    .title(description)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
        }
        mMap.addCircle(circle);
        mMap.addMarker(marker);
    }

    /**
     * Called when map is ready to be used
     * @param googleMap map object that is used in this class
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
        );
    }

    /**
     * Called when the back button is pressed.
     *
     * Used to warn users before gong back and losing their progress
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Go Back?");
        alertDialog.setMessage("You have pressed the back button, if you go back now, all progress will be lost. Go back and lose progress?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
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
