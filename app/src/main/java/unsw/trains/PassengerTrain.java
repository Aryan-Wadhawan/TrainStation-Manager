package unsw.trains;

import java.util.List;

import unsw.utils.Position;

public class PassengerTrain extends Train {
    private static final int SPEED = 2;
    private static final int MAX_CAPACITY_KG = 3500;

    public PassengerTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "PassengerTrain");
    }

    public int getSpeed() {
        return SPEED;
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }
}
