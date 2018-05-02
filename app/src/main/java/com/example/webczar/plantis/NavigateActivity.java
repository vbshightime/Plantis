package com.example.webczar.plantis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.webczar.plantis.Fragments.PlantShopee;
import com.example.webczar.plantis.Fragments.SavedPlant;
import com.example.webczar.plantis.Fragments.Search;
import com.example.webczar.plantis.Fragments.SoilMonitor;

public class NavigateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private android.support.v4.app.FragmentManager fragmentManager;
    private Fragment fragment;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
    //method for setting up toolbar
        setUpToolbar();
        //method for etting up drawer
        setUpDrawer();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set up fragment manager which will take care of
        fragmentManager = getSupportFragmentManager();
        fragment = new SoilMonitor();
        fragmentManager.beginTransaction().replace(R.id.content_Frame, fragment).commit();

    }


    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void setUpDrawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

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
        getMenuInflater().inflate(R.menu.navigate, menu);
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

        switch (id){
            case R.id.nav_camera:
                fragment = new SoilMonitor();
                toolbar.setTitle(R.string.soil_Monitor);
                break;
            case R.id.nav_gallery:
                fragment = new SavedPlant();
                toolbar.setTitle(R.string.saved_plants);
                break;
            case R.id.nav_slideshow:
                fragment = new Search();
                toolbar.setTitle(R.string.search);
                break;
            case R.id.nav_manage:
                fragment = new PlantShopee();
                toolbar.setTitle(R.string.plant_shopee);
                break;
            default:
                fragment = new SoilMonitor();
        }
        //Start the fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //replace the content of frame
        fragmentTransaction.replace(R.id.content_Frame, fragment);
       //commit the transaction
        fragmentTransaction.commit();

        //after each commit we need to close the drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert  drawerLayout!= null;
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

}
