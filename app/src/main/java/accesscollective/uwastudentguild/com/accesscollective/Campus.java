package accesscollective.uwastudentguild.com.accesscollective;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Campus {
    private String campusName;
    private double bottomLeftLat;
    private double bottomLeftLong;
    private double topRightLat;
    private double topRightLong;
    private double zoom;
    private Layer[] layers;

    public Campus(String campusName, double bottomLeftLat, double bottomLeftLong, double topRightLat, double topRightLong, double zoom, Layer[] layers) {
        this.campusName = campusName;
        this.bottomLeftLat = bottomLeftLat;
        this.bottomLeftLong = bottomLeftLong;
        this.topRightLat = topRightLat;
        this.topRightLong = topRightLong;
        this.zoom = zoom;
        this.layers = layers;
    }

    public String getCampusName() {
        return campusName;
    }

    public double getBottomLeftLat() {
        return bottomLeftLat;
    }

    public double getBottomLeftLong() {
        return bottomLeftLong;
    }

    public double getTopRightLat() {
        return topRightLat;
    }

    public double getTopRightLong() {
        return topRightLong;
    }

    public double getZoom() {
        return zoom;
    }

    LatLng getCentre() {
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(bottomLeftLat, bottomLeftLong),
                new LatLng(topRightLat, topRightLong)
        );
        return latLngBounds.getCenter();
    }

    Layer[] getLayers() {
        return layers;
    }
}
