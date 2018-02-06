package accesscollective.uwastudentguild.com.accesscollective;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.mapView, fragment);
        transaction.commit();

        fragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String campusNameFromMain = getArguments().getString("CAMPUS_NAME");

        //Change this so gets campusBoundsToGet from list activity or get location functionality
        //String campusBoundsToGet = "UWA";
        String campusBoundsToGet = campusNameFromMain;
        DatabaseReference campusBoundsRef = mDatabase.child("campusBounds").child(campusBoundsToGet);

        // Attach a listener to read the data at our posts reference
        // could change this to addListenerForSingleValueEvent
        campusBoundsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                campusBounds boundsToUse = dataSnapshot.getValue(campusBounds.class);

                // Define bounds for campus, centre camera within bounds, and prevent scrolling outside of them
                if(boundsToUse != null){
                    LatLngBounds uwaCampusBounds = new LatLngBounds(
                            new LatLng(boundsToUse.bottomLeftLat, boundsToUse.bottomLeftLong), new LatLng(boundsToUse.topRightLat, boundsToUse.topRightLong));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uwaCampusBounds.getCenter(), boundsToUse.zoom));
                    mMap.setLatLngBoundsForCameraTarget(uwaCampusBounds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("INFO","The read failed: " + databaseError.getCode());
            }
        });

        /* Get layer colours */
        final Map<String, Integer> allLayerColours = new HashMap<>();

        DatabaseReference layerColour = mDatabase.child("layerColours").child("standard");
        layerColour.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String layerType = postSnapshot.getKey();
                    Integer hue = postSnapshot.getValue(Integer.class);

                    Log.d("INFO", "hue value of " + hue + " for layer " + layerType);
                    allLayerColours.put(layerType, hue);
                }
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

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String layerKey = postSnapshot.getKey();

                    Log.d("INFO", "Getting checkpoints for layer " + layerKey);

                    for (DataSnapshot postSnapshot2: dataSnapshot.child(layerKey).getChildren()){
                        String markerValue = postSnapshot2.getKey();
                        markerLocation markerToStore = postSnapshot2.getValue(markerLocation.class);

                        allMarkerLocations.put(layerKey, markerToStore);

                        Log.d("INFO", markerToStore.latitude +  "  " + markerToStore.longitude);

                        float hueToUse = (float) allLayerColours.get(layerKey);

                        LatLng uwaLandMark = new LatLng(markerToStore.latitude, markerToStore.longitude);
                        mMap.addMarker(new MarkerOptions().position(uwaLandMark).title(markerValue).icon(BitmapDescriptorFactory
                                .defaultMarker(hueToUse)));
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

                //In case we decide to use activity to display floor plans on checkpoint click
                //Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                //intent.putExtra("MARKER_ID", marker.getTitle());
                //startActivity(intent);
                return false;
            }
        });

    }

}