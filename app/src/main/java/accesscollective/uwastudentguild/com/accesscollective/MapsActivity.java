package accesscollective.uwastudentguild.com.accesscollective;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static class campusBounds {

        public float bottomLeftLat;
        public float bottomLeftLong;
        public float topRightLat;
        public float topRightLong;
        public int zoom;
        //float bottomLeftLat, float bottomLeftLong, float bottomRightLat, float bottomRightLong, int zoom
        public campusBounds() {
            // ...
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Change this so gets campusBoundsToGet from list activity or get location functionality
        String campusBoundsToGet = "UWA";
        DatabaseReference ref = mDatabase.child("campusBounds").child(campusBoundsToGet);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                campusBounds boundsToUse = dataSnapshot.getValue(campusBounds.class);
                Log.i("INFO", "Getting bounds info:  " + Float.toString(boundsToUse.bottomLeftLat));

                // Define bounds for campus, centre camera within bounds, and prevent scrolling outside of them
                LatLngBounds uwaCampusBounds = new LatLngBounds(
                        new LatLng(boundsToUse.bottomLeftLat, boundsToUse.bottomLeftLong), new LatLng(boundsToUse.topRightLat, boundsToUse.topRightLong));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uwaCampusBounds.getCenter(), boundsToUse.zoom));
                mMap.setLatLngBoundsForCameraTarget(uwaCampusBounds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("INFO","The read failed: " + databaseError.getCode());
            }
        });

        // Create marker
        LatLng uwaLandMark = new LatLng(-31.979908, 115.818039);
        mMap.addMarker(new MarkerOptions().position(uwaLandMark).title("Marker for UWA"));
    }
}
