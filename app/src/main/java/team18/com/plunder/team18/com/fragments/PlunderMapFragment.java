package team18.com.plunder.team18.com.fragments;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;

import java.sql.Time;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import team18.com.plunder.MainActivity;
import team18.com.plunder.Manifest;
import team18.com.plunder.R;
import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.MapUtil;
import team18.com.plunder.utils.Waypoint;

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
    private Hunt currentHunt;
    private int waypointIndex;
    private LatLng currentWaypoint;
    private Location userLocation;
    private String waypointClue;
    private Date timeStarted;
    private boolean active = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_plunder_map, container, false);

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

        // Initialize hunt vars
        if (savedInstanceState != null) {
            currentHunt = (Hunt) savedInstanceState.getSerializable("hunt");
            timeStarted = (Date) savedInstanceState.getSerializable("start_time");
            waypointIndex = savedInstanceState.getInt("waypoint_index") - 1;
        } else {
            currentHunt = ((MainActivity) activity).getActiveHunt();
            timeStarted = new Date();
            waypointIndex = 0;
        }
        activity.setTitle(currentHunt.getName());
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
                if (updateWaypoint()) {
                    locationListener = null;
                    MainActivity.setActiveHunt(null);
                    active = false;
                    ((MainActivity) activity).navigateToCorrectScreen();
                }
            }
        });
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (active) {
                    hotter = (getDistanceFromWaypoint(location)) < (getDistanceFromWaypoint(userLocation));
                    userLocation = location;
                    //sensorText.setText("Dist to WP: " + getDistanceFromWaypoint(userLocation));
                    timerText.setText(formatTime(new Date().getTime() - timeStarted.getTime()));
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
        if (currentHunt.getWaypointList().size() >= (waypointIndex + 1)) {
            Waypoint point = currentHunt.getWaypointList().get(waypointIndex++);
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
            waypointCountText.setText(waypointIndex + "/" + currentHunt.getWaypointList().size());
            clueText.setText("Clue " + waypointIndex + ": " + waypointClue);
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

    private String formatTime(long time) {
        int minutes = (int) Math.floor(time / (60*1000));
        int hours = minutes / 60;
        int days = hours / 24;

        hours = hours % 24;
        minutes = minutes % 60;

        return ((days>0) ? (days + "d ") : "") +
                ((hours>0) ? (hours + "h ") : "") +
                ((minutes>0) ? (minutes + "m") : "<1m");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        active = false;
        outState.putSerializable("hunt", currentHunt);
        outState.putSerializable("start_time", timeStarted);
        outState.putInt("waypoint_index", waypointIndex);
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