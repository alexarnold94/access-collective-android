package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class FirebaseUpdateFragment extends Fragment {

    public interface UpdateCallbacks {
        void onPreExecute();
        void onPostExecute(Campus campus);
    }

    private UpdateCallbacks mCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (UpdateCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // TODO: Get campusName from SharedPreferences
        new UpdateTask().execute("UWA Crawley");
    }

    private class UpdateTask extends AsyncTask<String, Void, Campus> {

        private final String ERROR_CLASS_NAME = UpdateTask.class.getSimpleName();
        private long total = 1;
        private long count = 0;
        private Layer[] layers;
        private Campus campus;
        private boolean mError;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            } else {
                Log.e(ERROR_CLASS_NAME, "mCallbacks is null for onPreExecute()");
            }
        }

        @Override
        protected Campus doInBackground(String... strings) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("campusMarkersTest/" + strings[0]);

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    total = dataSnapshot.getChildrenCount();
                    int layersIndex = 0;
                    layers = new Layer[(int) total];

                    Log.e("UpdateTask", "total = " + total);
                    for (DataSnapshot layer: dataSnapshot.getChildren()) {
                        System.out.println("Layer = " + layer.getKey());
                        int checkpointIndex = 0;
                        Checkpoint[] checkpoints = new Checkpoint[(int) layer.getChildrenCount() - 1];
                        String image = "";

                        for (DataSnapshot checkpoint: layer.getChildren()) {
                            Log.e("UpdateFragment", "Checkpoint = " + checkpoint.getKey());
                            if (checkpoint.getKey().equals("image")) {
                                image = checkpoint.getValue(String.class);
                            } else {
                                double lat = 0.0;
                                double lon = 0.0;
                                String desc = "";

                                for (DataSnapshot info: checkpoint.getChildren()) {
                                    if (info.getKey().equals("latitude")) {
                                        lat = info.getValue(Double.class);
                                    } else if (info.getKey().equals("longitude")) {
                                        lon = info.getValue(Double.class);
                                    } else if (info.getKey().equals("description")) {
                                        desc = info.getKey();
                                    }
                                }

                                Log.e("UpdateFragment", "Creating a new Checkpoint for checkpoint = " + checkpoint.getKey());
                                checkpoints[checkpointIndex++] = new Checkpoint(checkpoint.getKey(), desc, lat, lon);
                            }
                        }
                        Log.e("UpdateFragment", "Creating a new Layer for layer = " + layer.getKey());
                        layers[layersIndex++] = new Layer(layer.getKey(), image, checkpoints);
                        count++;
                        Log.e("UpdateFragment", "count = " + count);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("UpdateFragment", "Datbase error downloading Layer data: " + databaseError.toException());
                    mError = true;
                }
            });

            int layerLoop = 0;
            while (count < total) {
                if (layerLoop == 0) {
                    Log.e("UpdateFragment", "Data loop has started!");
                }
                layerLoop++;
                if (mError) {
                    return null;
                }
            }

            count = 0;
            // TODO: Download images
            for (final Layer layer: layers) {
                final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(layer.getImageRef());
                if (getActivity() != null) {
                    final File file = new File(getActivity().getFilesDir(), layer.getImageName());
                    Log.e("UpdateTask", "Getting metadata for image = " + layer.getImageRef());
                    storageRef.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                        @Override
                        public void onComplete(@NonNull final Task<StorageMetadata> task) {
                            Log.e("UpdateTask", "Got metadata for image = " + layer.getImageRef());
                            if (getActivity() != null) {
                                SharedPreferences sharedPreferences = getActivity().getPreferences(0);
                                long locallyUpdated = sharedPreferences.getLong(layer.getImageRef(), 0);

                                if (task.getResult().getUpdatedTimeMillis() > locallyUpdated) {
                                    // Save image to device and update SharedPreferences
                                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.e("UpdateFragment", "Successfully downloaded " + layer.getImageRef());
                                            if (getActivity() != null) {
                                                SharedPreferences.Editor editor = getActivity().getPreferences(0).edit();
                                                editor.putLong(layer.getImageRef(), task.getResult().getUpdatedTimeMillis());
                                                editor.apply();
                                                count++;
                                            } else {
                                                Log.e("UpdateTask", "getActivity() returned null - cannot get Firebase information");
                                                mError = true;
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("UpdateTask", "Failed to save file to device: " + e.getLocalizedMessage());
                                            count++;
                                        }
                                    });
                                } else {
                                    Log.e("UpdateTask", "Local image is up to date: " + layer.getImageRef());
                                    count++;
                                }
                            } else {
                                Log.e("UpdateTask", "getActivity() returned null - cannot get Firebase information");
                                mError = true;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("UpdateTask", "Failed to download image: " + e.getLocalizedMessage());
                            count++;
                        }
                    });
                } else {
                    Log.e("UpdateTask", "getActivity() returned null - cannot get Firebase information");
                    mError = true;
                }
            }

            int imageLoop = 0;
            while (count < total) {
                if (imageLoop == 0) {
                    Log.e("UpdateFragment", "Image loop has started!");
                }
                if (mError) {
                    return null;
                }
                imageLoop++;
            }

            count = 0;
            databaseRef = FirebaseDatabase.getInstance().getReference().child("campusBounds/" + strings[0]);

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double bottomLeftLat = 0.0, bottomLeftLong = 0.0, topRightLat = 0.0, topRightLong = 0.0, zoom = 0.0;
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        if (data.getKey().equals("bottomLeftLat")) {
                            bottomLeftLat = data.getValue(Double.class);
                        } else if (data.getKey().equals("bottomLeftLong")) {
                            bottomLeftLong = data.getValue(Double.class);
                        } else if (data.getKey().equals("topRightLat")) {
                            topRightLat = data.getValue(Double.class);
                        } else if (data.getKey().equals("topRightLong")) {
                            topRightLong = data.getValue(Double.class);
                        } else if (data.getKey().equals("zoom")) {
                            zoom = data.getValue(Double.class);
                        }
                    }

                    campus = new Campus(dataSnapshot.getKey(), bottomLeftLat, bottomLeftLong, topRightLat, topRightLong, zoom, layers);
                    count = total;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("UpdateFragment", "Database error trying to download Campus data: " + databaseError.toException());
                    mError = true;
                }
            });

            int campusLoop = 0;
            while (count < total) {
                if (campusLoop == 0) {
                    Log.e("UpdateFragment", "Campus loop has started!");
                }
                if (mError) {
                    return null;
                }
                campusLoop++;
            }

            return campus;
        }

        @Override
        protected void onPostExecute(Campus campus) {
            super.onPostExecute(campus);
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(campus);
            } else {
                Log.e(ERROR_CLASS_NAME, "mCallbacks is null for onPostExecute()");
            }
        }
    }
}
