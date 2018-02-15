package accesscollective.uwastudentguild.com.accesscollective;

public class Campus {
    private String campusName;
    private double bottomLeftLat;
    private double bottomLeftLong;
    private double topRightLat;
    private double topRightLong;
    private double zoom;

    public Campus(String campusName, double bottomLeftLat, double bottomLeftLong, double topRightLat, double topRightLong, double zoom) {
        this.campusName = campusName;
        this.bottomLeftLat = bottomLeftLat;
        this.bottomLeftLong = bottomLeftLong;
        this.topRightLat = topRightLat;
        this.topRightLong = topRightLong;
        this.zoom = zoom;
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
}
