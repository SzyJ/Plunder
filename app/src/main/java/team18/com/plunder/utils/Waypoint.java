package team18.com.plunder.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Szymon Jackiewicz on 3/19/2017.
 */

public class Waypoint {

    private LatLng coords;
    private String description;

    public Waypoint(LatLng coords, String description) {
        this.coords = coords;
        this.description = description;
    }

    public LatLng getCoords() {
        LatLng coordsCpy = coords;
        return coordsCpy;
    }
    public String getDescription() { return description; }
}
