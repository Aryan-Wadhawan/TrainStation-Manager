package unsw.trains;

import java.util.List;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

public abstract class Train {
    private String trainId;
    private Position position;
    private List<String> route;
    private String type;
    private boolean isMovingForward;

    public Train(String trainId, Position position, List<String> route, String type) {
        this.trainId = trainId;
        this.position = position;
        this.route = route;
        this.type = type;
        this.isMovingForward = true;
    }

    public double getSpeed() {
        switch (getType()) {
        case "PassengerTrain":
            return 2.0;
        case "CargoTrain":
            return Math.max(1.0, 3.0 - getTotalWeight() / 1000.0);
        case "BulletTrain":
            return Math.max(2.0, 5.0 - getTotalWeight() / 1000.0);
        default:
            throw new IllegalArgumentException("Unknown train type: " + getType());
        }
    }

    public abstract int getTotalWeight();

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

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void reverseDirection() {
        this.isMovingForward = !this.isMovingForward;
    }

    public boolean isMovingForward() {
        return isMovingForward;
    }

    public abstract List<Passenger> getPassengers();

    public abstract List<Cargo> getCargo();

    public abstract List<PerishableCargo> getPerishableCargo();

    public abstract void addPassenger(Passenger passenger);

    public abstract void removePassenger(Passenger passenger);

    public abstract void addCargo(Cargo cargo);

    public abstract void addCargo(PerishableCargo cargo);

    public abstract void removeCargo(PerishableCargo cargo);

    public abstract void removeCargo(Cargo cargo);

    public boolean canCarryPassengers() {
        return this.type.equals("PassengerTrain") || this.type.equals("BulletTrain");
    }

    public boolean canCarryCargo() {
        return this.type.equals("CargoTrain") || this.type.equals("BulletTrain");
    }

    public abstract List<LoadInfoResponse> getLoadsInfo();

    public abstract boolean hasCapacity();

    public abstract void updatePerishableCargo();
}
