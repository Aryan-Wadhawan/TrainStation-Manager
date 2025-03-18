package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.managers.CargoManager;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

public abstract class Train {
    private String trainId;
    private Position position;
    private List<String> route;
    private String type;
    private List<Passenger> passengers;
    private List<Cargo> regularCargo;
    private List<PerishableCargo> perishableCargo;
    private boolean isMovingForward;

    public Train(String trainId, Position position, List<String> route, String type) {
        this.trainId = trainId;
        this.position = position;
        this.route = route;
        this.type = type;
        this.isMovingForward = true;
        this.passengers = new ArrayList<>();
        this.regularCargo = new ArrayList<>();
        this.perishableCargo = new ArrayList<>();
    }

    public double getSpeed() {
        switch (getType()) {
        case "PassengerTrain":
            return 2.0;
        case "CargoTrain":
            return Math.max(1.0, 3.0 - getCargoWeight() / 1000.0);
        case "BulletTrain":
            return Math.max(2.0, 5.0 - getCargoWeight() / 1000.0);
        default:
            throw new IllegalArgumentException("Unknown train type: " + getType());
        }
    }

    public abstract int getCargoWeight();

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

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<Cargo> getCargo() {

        return regularCargo;
    }

    public List<PerishableCargo> getPerishableCargo() {
        return perishableCargo;
    }

    public void addPassenger(Passenger passenger) {
        if (canCarryPassengers()) {
            passengers.add(passenger);
        }
    }

    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }

    public void addCargo(Cargo cargo) {

        regularCargo.add(cargo);

    }

    public void addCargo(PerishableCargo cargo) {

        perishableCargo.add((PerishableCargo) cargo);

    }

    public void removeCargo(PerishableCargo cargo) {
        perishableCargo.remove(cargo);

    }

    public void removeCargo(Cargo cargo) {

        regularCargo.remove(cargo);

    }

    public boolean canCarryPassengers() {
        return this.type.equals("PassengerTrain") || this.type.equals("BulletTrain");
    }

    public boolean canCarryCargo() {
        return this.type.equals("CargoTrain") || this.type.equals("BulletTrain");
    }

    public abstract List<LoadInfoResponse> getLoadsInfo();

    public abstract boolean hasCapacity();

    public void updatePerishableCargo() {
        // Reduce time for perishable cargo on train
        for (PerishableCargo perishableCargo : perishableCargo) {
            perishableCargo.decreaseTime(1);
        }

        // Remove expired perishable cargo from the station
        CargoManager.removeExpiredPerishableCargo(perishableCargo);
    }
}
