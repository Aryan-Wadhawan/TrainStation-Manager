package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

public class PassengerTrain extends Train {
    private static final int SPEED = 2;
    private static final int MAX_CAPACITY_KG = 3500;
    private static final int PASSENGER_WEIGHT = 70; // Each passenger weighs 70kg
    private List<Passenger> passengers;

    public PassengerTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "PassengerTrain");
        this.passengers = new ArrayList<>();

    }

    /**
     * Gets the constant speed of the passenger train.
     */
    @Override
    public double getSpeed() {
        return SPEED;
    }

    /**
     * Gets the maximum capacity in terms of weight.
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Returns the current weight of all passengers on board.
     */
    @Override
    public int getTotalWeight() {
        return passengers.size() * PASSENGER_WEIGHT;
    }

    public boolean hasCapacity() {
        return getCurrentPassengerWeight() < MAX_CAPACITY_KG;
    }

    private int getCurrentPassengerWeight() {
        return passengers.size() * 70; // Each passenger weighs 70kg
    }

    public void addPassenger(Passenger p) {
        if (hasCapacity()) {
            passengers.add(p);
        }
    }

    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    @Override
    public List<LoadInfoResponse> getLoadsInfo() {
        return getPassengers().stream().map(passenger -> new LoadInfoResponse(passenger.getPassengerId(), "Passenger"))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cargo> getCargo() {
        return null;
    }

    @Override
    public List<PerishableCargo> getPerishableCargo() {
        return null;
    }

    @Override
    public void addCargo(Cargo cargo) {
        return;
    }

    @Override
    public void addCargo(PerishableCargo cargo) {
        return;
    }

    @Override
    public void removeCargo(PerishableCargo cargo) {
        return;
    }

    @Override
    public void removeCargo(Cargo cargo) {
        return;
    }

    @Override
    public void updatePerishableCargo() {
        return;
    }

}
