package unsw.trains;

import java.util.List;

import unsw.utils.Position;

public class CargoTrain extends Train {
    private static final int BASE_SPEED = 3;
    private static final int MAX_CAPACITY_KG = 5000;

    public CargoTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "CargoTrain");
    }

    public int getBaseSpeed() {
        return BASE_SPEED;
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

}
