package team18.com.plunder.team18.com.fragments;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Date;

import team18.com.plunder.MainActivity;
import team18.com.plunder.R;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Waypoint;
import team18.com.plunder.utils.barcode.BarcodeCaptureActivity;

import static android.content.Context.LOCATION_SERVICE;
import static team18.com.plunder.utils.MapUtil.PERMISSION_REQUEST_CODE;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class PlunderMapFragment extends Fragment implements
        MainActivityFragment {
    // User Location and App permissions
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;

    // Qr Scanner result code
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    // Screen View objects
    private Activity activity;
    private View rootView;
    private MapView mMapView;
    private TextView clueText;
    private TextView timerText;
    private ImageView sensorIcon;
    private TextView sensorText;
    private TextView waypointCountText;
    private FloatingActionButton locationFab;
    private FloatingActionButton scanQrFab;

    // Vars for Hunt control
    private boolean hotter = true;
    private static float HOT_COLD_SENSOR_HOT_LIMIT = 30F;
    private static float HOT_COLD_SENSOR_COLD_LIMIT = 500F;
    //private Hunt currentHunt;
    //private int waypointIndex;
    private LatLng currentWaypoint;
    private Location userLocation;
    private String waypointClue;
    //private Date timeStarted;
    private boolean active = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_plunder_map, container, false);

        rootView.setTag("plunder_map_fragment");
        active = true;

        final LayoutInflater layInf = inflater;
        final ViewGroup viewGroup = container;

        // Find and initialize screen views
        activity = getActivity();
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        clueText = (TextView) rootView.findViewById(R.id.description_text);
        timerText = (TextView) rootView.findViewById(R.id.timer_text);
        sensorIcon = (ImageView) rootView.findViewById(R.id.sensor_icon);
        sensorText = (TextView) rootView.findViewById(R.id.sensor_text);
        waypointCountText = (TextView) rootView.findViewById(R.id.waypoint_text);
        locationFab = (FloatingActionButton) rootView.findViewById(R.id.location_fab);
        scanQrFab = (FloatingActionButton) rootView.findViewById(R.id.scan_qr_fab);

        /*
        // Initialize hunt vars
        if (savedInstanceState != null) {

            currentHunt = (Hunt) savedInstanceState.getSerializable("hunt");
            timeStarted = (Date) savedInstanceState.getSerializable("start_time");
            waypointIndex = savedInstanceState.getInt("waypoint_index") - 1;
            Snackbar.make(rootView, "Test", Snackbar.LENGTH_INDEFINITE)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onShown(Snackbar sb) {
                            super.onShown(sb);
                        }
                    })
                    .setAction("Action", null).show();
        } else {

            currentHunt = ((MainActivity) activity).getActiveHunt();
            timeStarted = new Date();
            waypointIndex = 0;
        }*/
        if (!((MainActivity) activity).initialized) {
            ((MainActivity) activity).initialized = true;
            ((MainActivity) activity).waypointIndex = 0;
            ((MainActivity) activity).timeStarted = new Date();
        } else {
            ((MainActivity) activity).waypointIndex--;
        }


        activity.setTitle(((MainActivity) activity).getActiveHunt().getName());
        updateWaypoint();

        // Set listeners for buttons
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        scanQrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (active) {
                    hotter = (getDistanceFromWaypoint(location)) < (getDistanceFromWaypoint(userLocation));
                    userLocation = location;
                    //sensorText.setText("Dist to WP: " + getDistanceFromWaypoint(userLocation));
                    timerText.setText(formatTime((new Date().getTime()) - ((MainActivity) activity).timeStarted.getTime()));
                    updateSensor();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Snackbar.make(rootView, "Location Services Re-Enabled", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Snackbar.make(rootView, "Location Services Disabled", Snackbar.LENGTH_INDEFINITE)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                            }
                        })
                        .setAction("Action", null).show();
            }
        };

        //Check for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);
            }
        }
        locationManager.requestLocationUpdates("gps", MapUtil.REFRESH_INTERVAL, MapUtil.MIN_DISTANCE, locationListener);

        // Initialize Map
        try {
            MapsInitializer.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setBuildingsEnabled(true);

                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_json)
                );
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        });
        mMapView.onResume(); // needed to get the map to display immediately

        return rootView;
    }

    // On return from QR Scanner
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    String codeFromScanner;
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (barcode != null) {
                        Point[] p = barcode.cornerPoints;
                        codeFromScanner = barcode.displayValue;
                    } else {
                        codeFromScanner = data.getStringExtra(BarcodeCaptureActivity.ManualInput);
                    }

                    /*
                    Snackbar.make(rootView, "Scan: " + barcode.displayValue + ", Expected: " + currentHunt.getWaypointList().get(waypointIndex -1).getScanCode(), Snackbar.LENGTH_INDEFINITE)
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onShown(Snackbar sb) {
                                    super.onShown(sb);
                                }
                            })
                            .setAction("Action", null).show();
                    */

                    if (codeFromScanner != null &&
                            codeFromScanner.equals(((MainActivity) activity).getActiveHunt().getWaypointList().get(((MainActivity) activity).waypointIndex - 1).getScanCode())) {
                        if (updateWaypoint()) {
                            locationListener = null;
                            MainActivity.setActiveHunt(null);
                            active = false;
                            ((MainActivity) activity).initialized = false;
                            ((MainActivity) activity).navigateToCorrectScreen();

                        } else {
                            huntProgress();
                        }
                    } else {
                        final View dialogView = rootView.inflate(getContext(), R.layout.dialog_hunt_bad_code, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(dialogView);

                        builder.show();
                    }


                } else {
                    Snackbar.make(rootView, "QR Scan Cancelled", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateSensor() {
        float dist = Math.round(getDistanceFromWaypoint(userLocation));
        int colour = 0x0;
        if (dist < HOT_COLD_SENSOR_HOT_LIMIT) {
            colour = ResourcesCompat.getColor(getResources(), R.color.colourHot, null);
            sensorIcon.setImageResource(R.drawable.ic_sensor_hot);
            sensorText.setText(getString(R.string.sensor_location_found));
        } else if (dist > (HOT_COLD_SENSOR_COLD_LIMIT + HOT_COLD_SENSOR_HOT_LIMIT)) {
            colour = ResourcesCompat.getColor(getResources(), R.color.colourCold, null);
            sensorIcon.setImageResource(R.drawable.ic_sensor_cold);
            sensorText.setText(getString(R.string.sensor_far_away));
        } else {
            colour = (Integer) new ArgbEvaluator().evaluate((dist - HOT_COLD_SENSOR_HOT_LIMIT)/HOT_COLD_SENSOR_COLD_LIMIT,
                    ResourcesCompat.getColor(getResources(), R.color.colourHot, null),
                    ResourcesCompat.getColor(getResources(), R.color.colourCold, null));
            if (hotter) {
                sensorIcon.setImageResource(R.drawable.ic_hotter);
                sensorText.setText(getString(R.string.sensor_hotter));
            } else {
                sensorIcon.setImageResource(R.drawable.ic_colder);
                sensorText.setText(getString(R.string.sensor_colder));
            }
        }

        sensorIcon.setColorFilter(colour);
        sensorText.setTextColor(colour);
    }

    private boolean updateWaypoint() {
        if (((MainActivity) activity).getActiveHunt().getWaypointList().size() >= (((MainActivity) activity).waypointIndex + 1)) {
            Waypoint point = ((MainActivity) activity).getActiveHunt().getWaypointList().get(((MainActivity) activity).waypointIndex++);
            currentWaypoint = point.getCoords();
            waypointClue = point.getDescription();

            /*
            mMap.clear();

            MarkerOptions marker = new MarkerOptions()
                    .position(currentWaypoint)
                    .title(waypointClue)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
            mMap.addMarker(marker);
            */
            waypointCountText.setText(((MainActivity) activity).waypointIndex + "/" + ((MainActivity) activity).getActiveHunt().getWaypointList().size());
            clueText.setText("Clue " + ((MainActivity) activity).waypointIndex + ": " + waypointClue);
            return false;
        } else {
            return true;
        }
    }

    private float getDistanceFromWaypoint(Location loc) {
        if (loc != null) {
            Location user = loc;
            Location waypoint = new Location("");
            waypoint.setLatitude(currentWaypoint.latitude);
            waypoint.setLongitude(currentWaypoint.longitude);
            return user.distanceTo(waypoint);
        } else {
            return -1f;
        }
    }

    private void huntProgress() {
        final View dialogView = rootView.inflate(getContext(), R.layout.dialog_hunt_progress, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        final TextView progressTitle = (TextView) dialogView.findViewById(R.id.progress_title);
        final TextView progressText = (TextView) dialogView.findViewById(R.id.clue_text);

        progressTitle.setText("Waypoint " + (((MainActivity) activity).waypointIndex - 1) + " completed!");
        progressText.setText(waypointClue);

        builder.show();
    }

    private String formatTime(long time) {
        int minutes = (int) Math.floor(time / (60*1000));
        int hours = minutes / 60;
        int days = hours / 24;

        hours = hours % 24;
        minutes = minutes % 60;

        return ((days>0) ? (days + "d ") : "") +
                ((hours>0) ? (hours + "h ") : "") +
                ((minutes>0) ? (minutes + "m") :
                 (hours>0) ? "0m" : "<1m");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        active = false;/*
        outState.putSerializable("hunt", (Serializable) currentHunt);
        outState.putSerializable("start_time", (Serializable) timeStarted);
        outState.putInt("waypoint_index", waypointIndex);*/
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
