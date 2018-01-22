package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        public campusBounds() {
            // ...
        }
    }

    public static class markerLocation {

        public float latitude;
        public float longitude;

        public markerLocation() {
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
        DatabaseReference campusBoundsRef = mDatabase.child("campusBounds").child(campusBoundsToGet);

        // Attach a listener to read the data at our posts reference
        // could change this to addListenerForSingleValueEvent
        campusBoundsRef.addValueEventListener(new ValueEventListener() {
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

        /*Get layers and associated markers, and store in hashmap (for potential future use) */
        DatabaseReference layersWithMarkersRef = mDatabase.child("campusMarkers").child(campusBoundsToGet);
        final Map<String, markerLocation> allMarkerLocations = new HashMap<>();

        layersWithMarkersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String layerKey = postSnapshot.getKey();
                    Log.i("INFO","Layer  is  " + layerKey);
                    for (DataSnapshot postSnapshot2: dataSnapshot.child(layerKey).getChildren()){
                        String markerValue = postSnapshot2.getKey();
                        markerLocation markerToStore = postSnapshot2.getValue(markerLocation.class);

                        allMarkerLocations.put(layerKey, markerToStore);

                        LatLng uwaLandMark = new LatLng(markerToStore.latitude, markerToStore.longitude);
                        mMap.addMarker(new MarkerOptions().position(uwaLandMark).title(markerValue));

                        Log.i("INFO","Lat and Long  is  " + Float.toString(markerToStore.latitude) + " " + Float.toString(markerToStore.longitude) );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("INFO","The read failed: " + databaseError.getCode());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            public boolean onMarkerClick(Marker marker) {
                
                String title = marker.getTitle();
                Log.i("INFO", "Getting marker info:  " + marker.getTitle());
                Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                intent.putExtra("MARKER_ID", marker.getTitle());
                startActivity(intent);
                return false;
                }
        });

    }
}
