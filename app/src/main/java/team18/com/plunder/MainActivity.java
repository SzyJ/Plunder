package team18.com.plunder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import team18.com.plunder.team18.com.fragments.PlunderMapFragment;
import team18.com.plunder.team18.com.fragments.SearchFragment;
import team18.com.plunder.team18.com.fragments.SettingsFrament;
import team18.com.plunder.utils.Hunt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Navigation Drawer codes
    public static final int NAV_DRAWER_SEARCH = 0;
    public static final int NAV_DRAWER_MAP = 1;
    public static final int NAV_DRAWER_CURRENT_HUNTS = 2;
    public static final int NAV_DRAWER_MAN_HUNTS = 3;
    public static final int NAV_DRAWER_MAN_EVENTS = 4;
    public static final int NAV_DRAWER_SETTINGS = 5;

    private static Hunt activeHunt = null;

    private MainActivityFragment searchFragment;
    private MainActivityFragment mapFragment;
    private Fragment activePlunderMapFragment;
    private MainActivityFragment currHuntFragment;
    private MainActivityFragment manHuntFragment;
    private MainActivityFragment manEventsFragment;
    private MainActivityFragment settingsFrament;

    private int currentScreenIndex;

    private NavigationView navigationView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activePlunderMapFragment != null) {
            //getSupportFragmentManager().putFragment(outState, "plunder_map_fragment", activePlunderMapFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            activePlunderMapFragment = getSupportFragmentManager().getFragment(savedInstanceState, "plunder_map_fragment");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            activePlunderMapFragment = getSupportFragmentManager().getFragment(savedInstanceState, "plunder_map_fragment");
        }

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
            return true;
        } else if (id == R.id.nav_night_mode) {
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigateToCorrectScreen() {
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

                if (activeHunt != null && activePlunderMapFragment == null) {
                    activePlunderMapFragment = new PlunderMapFragment();
                }

                if (activeHunt != null) {
                    fragMan.beginTransaction().replace(R.id.content_container,
                            (android.support.v4.app.Fragment) activePlunderMapFragment).commit();
                } else {
                    fragMan.beginTransaction().replace(R.id.content_container,
                            (android.support.v4.app.Fragment) mapFragment).commit();
                }

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


    public static void setActiveHunt(Hunt hunt) { activeHunt = hunt; }
    public static Hunt getActiveHunt() { return activeHunt; }
}
