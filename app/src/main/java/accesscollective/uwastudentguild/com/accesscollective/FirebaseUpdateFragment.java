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
        void onPostExecute(ArrayList<Layer> layers);
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

    private class UpdateTask extends AsyncTask<String, Void, ArrayList<Layer>> {

        private final String ERROR_CLASS_NAME = UpdateTask.class.getSimpleName();
        private long total = 1;
        private long count = 0;
        private ArrayList<Layer> layers = new ArrayList<>();
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
        protected ArrayList<Layer> doInBackground(String... strings) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("campusMarkersTest/" + strings[0]);

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    total = dataSnapshot.getChildrenCount();
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
                        layers.add(new Layer(layer.getKey(), image, checkpoints));
                        count++;
                        Log.e("UpdateFragment", "count = " + count);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            int dataLoop = 0;
            while (count < total) {
                if (dataLoop == 0) {
                    Log.e("UpdateFragment", "Data loop has started!");
                }
                dataLoop++;
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

            return layers;
        }

        @Override
        protected void onPostExecute(ArrayList<Layer> layers) {
            super.onPostExecute(layers);
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(layers);
            } else {
                Log.e(ERROR_CLASS_NAME, "mCallbacks is null for onPostExecute()");
            }
        }
    }
}
