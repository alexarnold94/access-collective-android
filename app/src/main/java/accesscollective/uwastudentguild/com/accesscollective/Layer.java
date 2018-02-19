package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;

public class Layer {
    private String name;
    private String imageRef;
    private Checkpoint[] checkpoints;

    public Layer(String name, String imageRef, Checkpoint[] checkpoints) {
        this.name = name;
        this.imageRef = imageRef;
        this.checkpoints = checkpoints;
    }

    public String getName() {
        return name;
    }

    public String getImageRef() {
        return imageRef;
    }

    public Checkpoint[] getCheckpoints() {
        return checkpoints;
    }

    public String getImageName() {
        String[] strings = imageRef.split("/");
        return strings[strings.length - 1];
    }

    public BitmapDescriptor getImageBitmapDescriptor(Context context, double scale) {
        File file = new File(context.getFilesDir(), getImageName());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) scale * bitmap.getHeight(),
                (int) scale * bitmap.getWidth(),
                true);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
