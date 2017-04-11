package team18.com.plunder.utils;

import android.content.Context;

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

public class MapUtil {


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
    public static void resetMapCamera(Hunt hunt, GoogleMap mMap) {
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
            mMap.animateCamera(camera);
        } catch (IllegalStateException ise) {
            //mMap.moveCamera(camera);
        }
    }
}
