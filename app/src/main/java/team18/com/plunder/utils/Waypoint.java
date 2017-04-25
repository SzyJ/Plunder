package team18.com.plunder.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Szymon Jackiewicz on 3/19/2017.
 */

public class Waypoint implements Serializable {

    private transient LatLng coords;
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


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(coords.latitude);
        out.writeDouble(coords.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        coords = new LatLng(in.readDouble(), in.readDouble());
    }
}
