package team18.com.plunder.team18.com.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import team18.com.plunder.R;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class MapFragment extends Fragment {

    MapView mMapView;
   //private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {

                //googleMap.setMyLocationEnabled(true);

                // Map functionality
                //mMap.setMinZoomPreference(15f); //20 looks good
                mMap.setBuildingsEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng newcastleUniversityCoordinates = new LatLng(54.979177, -1.614806); /*
                mMap.addMarker(new MarkerOptions().position(newcastleUniversityCoordinates).title("Hello World"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newcastleUniversityCoordinates));
                */

                // For zooming automatically to the location of the marker
                final CameraPosition cameraPosition = new CameraPosition.Builder().target(newcastleUniversityCoordinates).zoom(15f).build();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Re-center on User's location", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });



                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json)
                );

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
