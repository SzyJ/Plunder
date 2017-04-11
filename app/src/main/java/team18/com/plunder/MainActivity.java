package team18.com.plunder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import team18.com.plunder.team18.com.fragments.CurrHuntFragment;
import team18.com.plunder.team18.com.fragments.MainActivityFragment;
import team18.com.plunder.team18.com.fragments.ManEventsFragment;
import team18.com.plunder.team18.com.fragments.ManHuntFragment;
import team18.com.plunder.team18.com.fragments.MapFragment;
import team18.com.plunder.team18.com.fragments.SearchFragment;
import team18.com.plunder.team18.com.fragments.SettingsFrament;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NAV_DRAWER_SEARCH = 0;
    public static final int NAV_DRAWER_MAP = 1;
    public static final int NAV_DRAWER_CURRENT_HUNTS = 2;
    public static final int NAV_DRAWER_MAN_HUNTS = 3;
    public static final int NAV_DRAWER_MAN_EVENTS = 4;
    public static final int NAV_DRAWER_SETTINGS = 5;


    private MainActivityFragment searchFragment;
    private MainActivityFragment mapFragment;
    private MainActivityFragment currHuntFragment;
    private MainActivityFragment manHuntFragment;
    private MainActivityFragment manEventsFragment;
    private MainActivityFragment settingsFrament;

    private int currentScreenIndex;

    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currentScreenIndex = getIntent().getExtras().getInt("nav_index");
        navigateToCorrectScreen();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            currentScreenIndex = NAV_DRAWER_SEARCH;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_map) {
            currentScreenIndex = NAV_DRAWER_MAP;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_current) {
            currentScreenIndex = NAV_DRAWER_CURRENT_HUNTS;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_manage_hunt) {
            currentScreenIndex = NAV_DRAWER_MAN_HUNTS;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_manage_event) {
            currentScreenIndex = NAV_DRAWER_MAN_EVENTS;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_settings) {
            currentScreenIndex = NAV_DRAWER_SETTINGS;
            navigateToCorrectScreen();
        } else if (id == R.id.nav_satellite) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            return true;
        } else if (id == R.id.nav_night_mode) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            return true;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToCorrectScreen() {
        FragmentManager fragMan = getSupportFragmentManager();
        switch (currentScreenIndex) {
            case NAV_DRAWER_SEARCH:
                if (searchFragment == null) {
                    searchFragment = new SearchFragment();
                }
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) searchFragment).commit();
                break;
            case NAV_DRAWER_MAP:
                if (mapFragment == null) {
                    mapFragment = new MapFragment();
                }
                //SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) mapFragment).commit();
                break;
            case NAV_DRAWER_CURRENT_HUNTS:
                if (currHuntFragment == null) {
                    currHuntFragment = new CurrHuntFragment();
                }
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) currHuntFragment).commit();
                break;
            case NAV_DRAWER_MAN_HUNTS:
                if (manHuntFragment == null) {
                    manHuntFragment = new ManHuntFragment();
                }
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) manHuntFragment).commit();
                break;
            case NAV_DRAWER_MAN_EVENTS:
                if (manEventsFragment == null) {
                    manEventsFragment = new ManEventsFragment();
                }
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) manEventsFragment).commit();
                break;
            case NAV_DRAWER_SETTINGS:
                if (settingsFrament == null) {
                    settingsFrament = new SettingsFrament();
                }
                fragMan.beginTransaction().replace(R.id.content_container,
                        (android.support.v4.app.Fragment) settingsFrament).commit();
                break;
        }

        navigationView.getMenu().getItem(currentScreenIndex).setChecked(true);


    }

}
