package team18.com.plunder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.vision.text.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.MapUtil;

/**
 * Created by Szymon Jackiewicz on 06-Apr-17.
 */

public class ViewHunt extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView dateText;
    private TextView authorText;
    private TextView wpCountText;
    private TextView completionTimeText;

    private Button backButton;
    private Button remakeButton;

    private int waypointCount = 0;


    private String huntName;

    private Hunt hunt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hunt_view_map);
        mapFragment.getMapAsync(this);

        initialize();


        remakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hunt.resetHunt();
                Intent intent = new Intent(getApplicationContext(), CreateHunt.class);
                intent.putExtra("new_hunt_obj", hunt);
                startActivity(intent);
            }
        });

        setTitle(huntName);
        writeText();
    }

    private void initialize() {
        hunt = (Hunt) getIntent().getExtras().getSerializable("hunt_obj");
        huntName = hunt.getName();
        waypointCount = hunt.getWaypointList().size();
        dateText = (TextView) findViewById(R.id.date_created_text);
        authorText  = (TextView) findViewById(R.id.created_by_text);
        wpCountText = (TextView) findViewById(R.id.waypoint_count_text);
        completionTimeText = (TextView) findViewById(R.id.completion_time_text);

        backButton = (Button) findViewById(R.id.hunt_view_back_btn);
        remakeButton = (Button) findViewById(R.id.remake_hunt_btn);
    }

    private void writeText() {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateText.append("" + df.format(hunt.getDateCreated()));
        authorText.append("" + hunt.getAuthor());
        wpCountText.setText("" + waypointCount + " " + "waypoint" + ((waypointCount > 1) ? "s" : ""));
        if (hunt.getCompletionTime() > 0) {
            completionTimeText.append("" + hunt.getCompletionTime());
        } else {
            completionTimeText.append("Hunt not yet tested");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Style map
        mMap = MapUtil.styleMap(googleMap, this);

        //Draw Circles and place markers with clues
        mMap = MapUtil.resetCirlces(mMap, hunt);

        // attempt to animate Map Camera to show all points
        MapUtil.resetMapCamera(hunt, mMap);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nav_index", MainActivity.NAV_DRAWER_MAN_HUNTS);
        startActivity(intent);
        //super.onBackPressed();
    }
}
