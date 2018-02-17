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

        MapFragment mFragment = MapFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.frame_map, mFragment).commit();

        FrameLayout fl = new FrameLayout(this);
        fl.setBackgroundColor(Color.WHITE); //change to whatever color your activity/fragment has set as its background color
        fl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); //cover the whole frame
        //View.generateViewId()generate a new View ID. This requires API 17, so if you're supporting lower than that use just a static integer instead
        fl.setId(flid);
        ((FrameLayout) findViewById(R.id.frame_map)).addView(fl);
        mFragment.getMapAsync(this);

        // Do map stuff here
        //mProgressBar.setVisibility(View.INVISIBLE);
        //mMapView.setVisibility(View.VISIBLE);

        // Necessary to avoid name clash with the Google Maps MapFragment
//        mMapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map_fragment);
//        mMapFragment.getView().setVisibility(View.INVISIBLE);
//        mMapFragment.getMapAsync(this);
        //Log.e("MapActivity", "mMapFragment.getMapAsync() called");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapActivity", "Map ready!");
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
