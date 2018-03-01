package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseUpdateFragment.UpdateCallbacks, OnMapReadyCallback {

    private ProgressBar mProgressBar;
    private Campus campus;
    private boolean mHasDownloaded;
    private FloatingSearchView mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mSearchBar = findViewById(R.id.floating_search_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSearchBar.attachNavigationDrawerToMenuButton(drawer);

        if (savedInstanceState != null) {
            mHasDownloaded = savedInstanceState.getBoolean("hasDownloaded", false);
            if (!mHasDownloaded) {
                Log.d("MapActivity", "!mHasDownloaded, FirebaseUpdateFragment is ongoing");
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
        getPreferences(0).edit().putBoolean("hasDownloaded", mHasDownloaded).apply();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FirebaseUpdateFragment updateFragment = (FirebaseUpdateFragment) fragmentManager.findFragmentByTag("FirebaseUpdateFragment");

        if (updateFragment == null) {
            getSupportFragmentManager().beginTransaction().add(new FirebaseUpdateFragment(), "FirebaseUpdateFragment").commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("hasDownloaded", mHasDownloaded);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPreExecute() {
        Log.e("MapActivity", "onPreExecute called");
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(Campus campus) {
        Log.e("MapActivity", "onPostExecute(layers) called!");
        mHasDownloaded = true;
        getPreferences(0).edit().putBoolean("hasDownloaded", true).apply();
        this.campus = campus;

        if (campus != null) {
            for (Layer layer : campus.getLayers()) {
                Log.e("MapActivity", "Layer: " + layer.getName());
                for (Checkpoint checkpoint : layer.getCheckpoints()) {
                    Log.e("MapActivity","Checkpoint: " + checkpoint.getName());
                }
            }
        } else {
            // TODO Load a default map?
            Log.e("MapActivity", "Campus is null");
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(campus.getCentre())
                .zoom((float) campus.getZoom())
                .build();
        MapFragment mapFragment = MapFragment.newInstance(new GoogleMapOptions().camera(cameraPosition));
        getFragmentManager().beginTransaction().add(R.id.frame_map, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapActivity", "Map ready!");
        mProgressBar.setVisibility(View.INVISIBLE);
        mSearchBar.setVisibility(View.VISIBLE);

        for (Layer layer: campus.getLayers()) {
            Log.e("MapActivity", "Creating checkpoints for " + layer.getName());
            for (Checkpoint checkpoint : layer.getCheckpoints()) {
                Log.e("MapActivity", "Creating marker " + checkpoint.getName() + " in layer " + layer.getName());
                LatLng latLng = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(checkpoint.getName()).position(latLng).icon(layer.getImageBitmapDescriptor(this, 3));
                if (!checkpoint.getDescription().equals("")) {
                    markerOptions.snippet(checkpoint.getDescription());
                }
                googleMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // email address and subject for feedback button
        String subject= "Access Collective App feedback";
        String [] addresses= {("test.codersforcauses@gmail.com")};

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change) {
            Intent Intent = new Intent(this, SelectCampus.class);
            startActivity(Intent);
        } else if (id == R.id.nav_suggest) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_feedback) {

            Intent intent= new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        Log.e("MapActivity", "onResume called");
        super.onResume();
        if (!getPreferences(0).getBoolean("hasDownloaded", false)) {
            mSearchBar.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSearchBar.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
