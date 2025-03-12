package unsw.trains;

import java.util.List;
import unsw.utils.Position;

public abstract class Train {
    private String trainId;
    private Position position;
    private List<String> route;
    private String type;

    public Train(String trainId, Position position, List<String> route, String type) {
        this.trainId = trainId;
        this.position = position;
        this.route = route;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
