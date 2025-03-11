package unsw.trains;

import java.util.List;

import unsw.utils.Position;

public class CargoTrain extends Train {
    private static final int BASE_SPEED = 3;
    private static final int MAX_WEIGHT = 5000;

    public CargoTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route);
        //TODO Auto-generated constructor stub
    }

}
