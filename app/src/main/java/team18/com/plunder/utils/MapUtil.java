package team18.com.plunder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import team18.com.plunder.R;

/**
 * Created by Szymon on 07-Apr-17.
 */

public class MapUtil implements ActivityCompat.OnRequestPermissionsResultCallback{

    // Location Permissions
    private static LocationManager locationManager;
    private static android.location.LocationListener locationListener;
    public static final long REFRESH_INTERVAL = 1000; //How often the app will check for location updates (in MilliSeconds)
    public static final float MIN_DISTANCE = 0f; // Location change needed for location refresh to trigger (in Meters)
    public static final int PERMISSION_REQUEST_CODE = 0;

    // Map Drawing
    private final static int PRIMARY_CIRCLE_FILL_COLOUR = 0x30FFA000;
    private final static int PRIMARY_CIRCLE_OUTLINE_COLOUR = 0xFFFF8F00;
    private final static int SECONDARY_CIRCLE_FILL_COLOUR = 0x30BDBDBD;
    private final static int SECONDARY_CIRCLE_OUTLINE_COLOUR = 0xFF757575;
    private final static double CIRCLE_RADIUS = 100.0;

    private final static float DEFAULT_CAMERA_ZOOM = 15f;

    /**
     * Draws a circle and places a marker for each waypoint in the given hunt
     * Draws a differently coloured circle for one waypoint to bring attention to it
     *
     * @param mMap GoogleMap object that to be used
     * @param hunt hunt that is to be shown on the map
     * @param selectedWaypoint An extra waypoint to be hilighted to bring attention to it
     * @return GoogleMap object with previously added circles and markers removed and new ones applied
     */
    public static GoogleMap resetCirlces(GoogleMap mMap, Hunt hunt, LatLng selectedWaypoint) {
        mMap.clear();
        int stepper = 1;
        for (Waypoint wp : hunt.getWaypointList()) {
            //addCircle(wp.getCoords(), "Waypoint " + (stepper++) + ": \"" + wp.getDescription() + "\"", false);
            mMap = MapUtil.addCircle(wp.getCoords(), "Waypoint " + (stepper++) + ": \"" + wp.getDescription() + "\"", false, mMap);
        }
        if (selectedWaypoint != null) {
            //addCircle(selectedWaypoint, "New Waypoint", true);
            mMap = MapUtil.addCircle(selectedWaypoint,  "New Waypoint", true, mMap);
        }

        return mMap;
    }

    /**
     * Draws a circle and places a marker for each waypoint in the given hunt
     *
     * @param mMap GoogleMap object that to be used
     * @param hunt hunt that is to be shown on the map
     * @return GoogleMap object with previously added circles and markers removed and new ones applied
     */
    public static GoogleMap resetCirlces(GoogleMap mMap, Hunt hunt) {
        mMap.clear();
        int stepper = 1;
        for (Waypoint wp : hunt.getWaypointList()) {
            //addCircle(wp.getCoords(), "Waypoint " + (stepper++) + ": \"" + wp.getDescription() + "\"", false);
            mMap = MapUtil.addCircle(wp.getCoords(), "Waypoint " + (stepper++) + ": \"" + wp.getDescription() + "\"", false, mMap);
        }
        return mMap;
    }

    /**
     * Draws a circle on the map and places a marker in the middle of it.
     * Circle radius and colours are defined as final veriables at the top.
     *
     * @param coords the coordinates for the centre of the new circle
     * @param description Text that is displayed when a marker is touched
     * @param primary true if place is selected but not yet added to bring attention to it.
     * @return the GoogleMap object with the added circle and marker.
     */
    public static GoogleMap addCircle(LatLng coords, String description, boolean primary, GoogleMap mMap) {
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

        return mMap;
    }

    public static GoogleMap styleMap(GoogleMap mMap, Context context) {
        mMap.setBuildingsEnabled(true);
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json)
        );
        return mMap;
    }

    /**
     * Relocates the Camera to show all currently selected waypoints
     *
     * @param hunt hunt from which the waypoints would be selected
     * @param mMap the map affected
     */
    public static void resetMapCamera(Hunt hunt, GoogleMap mMap, boolean animate) {
        CameraUpdate camera;

        if (hunt.getWaypointList().size() > 1) {

            /*
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
            if (animate) {
                mMap.animateCamera(camera);
            } else {
                mMap.moveCamera(camera);
            }
        } catch (IllegalStateException ise) {
            //mMap.moveCamera(camera);
        }
    }

    public static void requestPermissions(final Activity activity) {
        // Get permissions from user for location
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                /*
                Snackbar.make(activity, "GPS Re-Enabled", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                */
            }

            @Override
            public void onProviderDisabled(String provider) {
                /*
                Snackbar.make(activity, "GPS Disabled", Snackbar.LENGTH_INDEFINITE)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                            }
                        })
                        .setAction("Action", null).show();
               */
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                activity.requestPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);
            }
        } else {
            try {
                locationManager.requestLocationUpdates("gps", REFRESH_INTERVAL, MIN_DISTANCE,locationListener);
            } catch (SecurityException e) {
                // APp will not function properly if location permission is not granted.
                // The app should be locked up here until the permission is granted
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestLocationUpdates("gps", REFRESH_INTERVAL, MIN_DISTANCE, locationListener);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
    }
}
