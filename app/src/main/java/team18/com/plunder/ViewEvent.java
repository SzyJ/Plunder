package team18.com.plunder;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.MapUtil;

/**
 * Created by guillermochibas on 30-Apr-17.
 */

public class ViewEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView startDateText;
    private TextView authorText;
    private TextView wpCountText;

    private Button backButton;
    private Button startButton;

    private int waypointCount = 0;

    private String eventName;

    private Hunt hunt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        initialize();


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateHunt.class);
                intent.putExtra("new_hunt_obj", hunt);
                startActivity(intent);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setActiveHunt(hunt);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAP);
                //intent.putExtra("plunder_hunt", hunt);
                startActivity(intent);
            }
        });
        setTitle(eventName);
        writeText();
    }

    private void initialize() {
        hunt = (Hunt) getIntent().getExtras().getSerializable("hunt_obj");
        eventName = hunt.getName();
        waypointCount = hunt.getWaypointList().size();
        startDateText = (TextView) findViewById(R.id.date_created_text);
        authorText  = (TextView) findViewById(R.id.created_by_text);
        wpCountText = (TextView) findViewById(R.id.waypoint_count_text);

        backButton = (Button) findViewById(R.id.hunt_view_back_btn);
        startButton = (Button) findViewById(R.id.remake_hunt_btn);
    }

    private void writeText() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        startDateText.append("" + df.format(hunt.getDateCreated()));
        authorText.append("" + hunt.getAuthor());
        wpCountText.setText("" + waypointCount + " " + "waypoint" + ((waypointCount > 1) ? "s" : ""));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Style map
        mMap = MapUtil.styleMap(googleMap, this);

        //Draw Circles and place markers with clues
        mMap = MapUtil.resetCirlces(mMap, hunt);

        // attempt to animate Map Camera to show all points
        MapUtil.resetMapCamera(hunt, mMap, false);
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAN_HUNTS);
        startActivity(intent);
        //super.onBackPressed();
    }
}
