package team18.com.plunder.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Szymon Jackiewicz on 3/19/2017.
 */

public class Hunt implements Iterator<Waypoint> {
    private int iteration = 0; // Keeps track of the next index to be returned
    private String name;
    private List<Waypoint> wpList;

    public Hunt(String name) {
        this.name = name;
        wpList = new ArrayList<Waypoint>();
    }

    public void addWaypoint(Waypoint newPoint) { wpList.add(newPoint); }

    public String getName() { return name; }
    public List<Waypoint> getWaypointList() {
        List<Waypoint> unmodList = wpList;
        Collections.unmodifiableList(unmodList);
        return unmodList;
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
