package team18.com.plunder.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Szymon Jackiewicz on 3/19/2017.
 */

public class Hunt implements Serializable, Iterator<Waypoint> {
    private int iteration = 0; // Keeps track of the next index to be returned
    private String name;
    private List<Waypoint> wpList;
    private Date dateCreated;
    private String author;
    private long completionTime;

    public Hunt(String name) {

        this.name = name;
        wpList = new ArrayList<Waypoint>();
        dateCreated = new Date();
        author = "Szymon"; // replace with GetCurrentUser().getName(); or something

        completionTime = -1; //(DataExistsInServer) : completionTime = -1 ? completionTime = GetDataFromServer();
    }

    public void addWaypoint(Waypoint newPoint) { wpList.add(newPoint); }

    public String getName() { return name; }
    public List<Waypoint> getWaypointList() {
        List<Waypoint> unmodList = wpList;
        Collections.unmodifiableList(unmodList);
        return unmodList;
    }

    public String getAuthor() { return author; }
    public Date getDateCreated() { return dateCreated; }
    public long getCompletionTime() { return completionTime; }

    public void resetHunt() {
        dateCreated = new Date();
        completionTime = -1;
        wpList.clear();
    }

    @Override
    public boolean hasNext() { return (iteration < wpList.size()); }

    @Override
    public Waypoint next() {
        if (hasNext()) {
            return wpList.get(iteration++);
        }
        throw new NoSuchElementException("Failed at index " + iteration + ". Waypoint List size: " + wpList.size());
    }
}
