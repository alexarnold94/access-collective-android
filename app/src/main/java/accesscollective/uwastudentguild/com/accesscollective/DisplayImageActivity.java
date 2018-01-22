package accesscollective.uwastudentguild.com.accesscollective;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        String markerName = getIntent().getStringExtra("MARKER_ID");
        Log.i("INFO", "RECEIVED MARKER : " + markerName);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageReference.child("image1.jpg");

        Log.i("INFO", storageReference.toString());
        Log.i("INFO", pathReference.toString());


        ImageView imageView = (ImageView) findViewById(R.id.mapImageView);
        //load image into imageview
        Glide.with(this ).load(pathReference).into(imageView);
    }

}
