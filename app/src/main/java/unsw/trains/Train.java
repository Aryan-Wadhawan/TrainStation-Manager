package unsw.trains;

import java.util.List;
import unsw.utils.Position;

public class Train {
    private String trainId;
    private Position position;
    private List<String> route;

    public Train(String trainId, Position position, List<String> route) {
        this.trainId = trainId;
        this.position = position;
        this.route = route;
    }

    public String getTrainId() {
        return trainId;
    }

    public Position getPosition() {
        return position;
    }

    public List<String> getRoute() {
        return route;
    }

}
