package accesscollective.uwastudentguild.com.accesscollective;

import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements FirebaseUpdateFragment.UpdateCallbacks, OnMapReadyCallback {

    private ProgressBar mProgressBar;
    private ArrayList<Layer> layers;
    private boolean mHasDownloaded;
    private FirebaseUpdateFragment mUpdateFragment;
    private com.google.android.gms.maps.MapFragment mMapFragment;
    private final int flid = 12345678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {
            mHasDownloaded = savedInstanceState.getBoolean("hasDownloaded", false);
            if (!mHasDownloaded) {
                Log.d("MapActivity", "!mHasDownloaded, FirebaseUpdateFragment is ongoing");
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mUpdateFragment = (FirebaseUpdateFragment) fragmentManager.findFragmentByTag("FirebaseUpdateFragment");

        if (mUpdateFragment == null) {
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
    public void onPostExecute(ArrayList<Layer> layers) {
        Log.e("MapActivity", "onPostExecute(layers) called!");
        mHasDownloaded = true;
        this.layers = layers;

        if (layers != null) {
            for (Layer layer : layers) {
                Log.e("MapActivity", "Layer: " + layer.getName());
                for (Checkpoint checkpoint : layer.getCheckpoints()) {
                    Log.e("MapActivity","Checkpoint: " + checkpoint.getName());
                }
            }
        } else {
            Log.e("MapActivity", "Layers is null");
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(-31.98200, 115.8150000))
                .zoom(15)
                .build();
        MapFragment mapFragment = MapFragment.newInstance(new GoogleMapOptions().camera(cameraPosition));
        getFragmentManager().beginTransaction().add(R.id.frame_map, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapActivity", "Map ready!");
        mProgressBar.setVisibility(View.INVISIBLE);

        for (Layer layer: layers) {
            Log.e("MapActivity", "Creating checkpoints for " + layer.getName());
            for (Checkpoint checkpoint: layer.getCheckpoints()) {
                Log.e("MapActivity", "Creating marker " + checkpoint.getName() + " in layer " + layer.getName());
                LatLng latLng = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(checkpoint.getName()).position(latLng);
                if (!checkpoint.getDescription().equals("")) {
                    markerOptions.snippet(checkpoint.getDescription());
                }
                googleMap.addMarker(markerOptions);
            }
        }
    }
}
