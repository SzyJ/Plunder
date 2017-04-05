package team18.com.plunder;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import team18.com.plunder.team18.com.fragments.ManEventsFragment;
import team18.com.plunder.team18.com.fragments.ManHuntFragment;
import team18.com.plunder.team18.com.fragments.MapFragment;
import team18.com.plunder.team18.com.fragments.SearchFragment;
import team18.com.plunder.team18.com.fragments.SettingsFrament;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragMan = getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.content_container, new MapFragment()).commit();
        navigationView.getMenu().getItem(1).setChecked(true);

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
        FragmentManager fragMan = getSupportFragmentManager();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            fragMan.beginTransaction().replace(R.id.content_container, new SearchFragment()).commit();
        } else if (id == R.id.nav_map) {
            SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
            fragMan.beginTransaction().replace(R.id.content_container, new MapFragment()).commit();
        } else if (id == R.id.nav_current) {
            fragMan.beginTransaction().replace(R.id.content_container, new CurrHuntFragment()).commit();
        } else if (id == R.id.nav_manage_hunt) {
            fragMan.beginTransaction().replace(R.id.content_container, new ManHuntFragment()).commit();
        } else if (id == R.id.nav_manage_event) {
            fragMan.beginTransaction().replace(R.id.content_container, new ManEventsFragment()).commit();
        } else if (id == R.id.nav_settings) {
            fragMan.beginTransaction().replace(R.id.content_container, new SettingsFrament()).commit();
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
}
