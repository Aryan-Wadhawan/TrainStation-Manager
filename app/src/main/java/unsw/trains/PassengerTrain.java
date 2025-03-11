package unsw.trains;

import java.util.List;

import unsw.utils.Position;

public class PassengerTrain extends Train {
    private static final int SPEED = 2;
    private static final int MAX_WEIGHT = 3500;

    public PassengerTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route);
        //TODO Auto-generated constructor stub
    }

}
