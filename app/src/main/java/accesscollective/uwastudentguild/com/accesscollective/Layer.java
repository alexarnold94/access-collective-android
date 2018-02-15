package accesscollective.uwastudentguild.com.accesscollective;

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
}
