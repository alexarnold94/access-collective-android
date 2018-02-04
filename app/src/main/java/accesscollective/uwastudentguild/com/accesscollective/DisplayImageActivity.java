package accesscollective.uwastudentguild.com.accesscollective;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

//import com.bumptech.glide.Glide;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String markerName = getIntent().getStringExtra("MARKER_ID");
        Log.i("INFO", "RECEIVED MARKER : " + markerName);

        String floorPlansStorageLocation = "floorplans/" + markerName  + "/";

        /* get floor plan data */

/*
        *//*display images*//*
        // not using for now as glide version is resulting in issues
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("image1.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("INFO", uri.toString());
                ImageView imageView = (ImageView) findViewById(R.id.mapImageView);
                Glide.with(getApplicationContext()).load(uri.toString()).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        *//* iterate through images *//*
        ImageView imageView = (ImageView) findViewById(R.id.mapImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("== My activity ===","OnClick is called");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("image2.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("INFO", uri.toString());
                        ImageView imageView = (ImageView) findViewById(R.id.mapImageView);
                        Glide.with(getApplicationContext()).load(uri.toString()).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });*/

    }

}
