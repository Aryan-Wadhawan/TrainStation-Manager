package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

/**
 * Represents a Passenger Train that can carry passengers only.
 * Each passenger adds a fixed weight, and the train operates at a constant speed.
 */
public class PassengerTrain extends Train {
    private static final int SPEED = 2;
    private static final int MAX_CAPACITY_KG = 3500;
    private static final int PASSENGER_WEIGHT = 70; // Each passenger weighs 70kg

    private List<Passenger> passengers;

    /**
     * Constructs a new PassengerTrain with the specified ID, position, and route.
     *
     * @param trainId  Unique identifier for the train.
     * @param position Starting position of the train.
     * @param route    List of station IDs defining the train's path.
     */
    public PassengerTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "PassengerTrain");
        this.passengers = new ArrayList<>();
    }

    /**
     * Returns the constant speed of the passenger train (2 km/min).
     *
     * @return fixed speed value.
     */
    @Override
    public double getSpeed() {
        return SPEED;
    }

    /**
     * Returns the maximum allowed weight on the train.
     *
     * @return max capacity in kg (3500).
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Calculates the current total weight of passengers on the train.
     *
     * @return total passenger weight in kg.
     */
    @Override
    public int getTotalWeight() {
        return passengers.size() * PASSENGER_WEIGHT;
    }

    /**
     * Checks if the train has capacity for additional passengers.
     *
     * @return true if under max capacity, false otherwise.
     */
    public boolean hasCapacity() {
        return getCurrentPassengerWeight() < MAX_CAPACITY_KG;
    }

    /**
     * Helper method to calculate total passenger weight.
     *
     * @return total passenger weight in kg.
     */
    private int getCurrentPassengerWeight() {
        return passengers.size() * PASSENGER_WEIGHT;
    }

    /**
     * Adds a passenger to the train if there's enough capacity.
     *
     * @param p the passenger to add.
     */
    public void addPassenger(Passenger p) {
        if (hasCapacity()) {
            passengers.add(p);
        }
    }

    /**
     * Removes a passenger from the train.
     *
     * @param p the passenger to remove.
     */
    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    /**
     * Returns a list of all passengers currently on the train.
     *
     * @return list of passengers.
     */
    @Override
    public List<Passenger> getPassengers() {
        return passengers;
    }

    /**
     * Returns information about all loads (passengers) on the train.
     *
     * @return list of LoadInfoResponse.
     */
    @Override
    public List<LoadInfoResponse> getLoadsInfo() {
        return getPassengers().stream().map(passenger -> new LoadInfoResponse(passenger.getPassengerId(), "Passenger"))
                .collect(Collectors.toList());
    }

    /**
     * Returns null since PassengerTrain does not carry cargo.
     *
     * @return null.
     */
    @Override
    public List<Cargo> getCargo() {
        return null;
    }

    /**
     * Returns null since PassengerTrain does not carry perishable cargo.
     *
     * @return null.
     */
    @Override
    public List<PerishableCargo> getPerishableCargo() {
        return null;
    }

    /**
     * PassengerTrain does not support regular cargo. This method does nothing.
     *
     * @param cargo ignored.
     */
    @Override
    public void addCargo(Cargo cargo) {
        return;
    }

    /**
     * PassengerTrain does not support perishable cargo. This method does nothing.
     *
     * @param cargo ignored.
     */
    @Override
    public void addCargo(PerishableCargo cargo) {
        return;
    }

    /**
     * PassengerTrain does not support perishable cargo. This method does nothing.
     *
     * @param cargo ignored.
     */
    @Override
    public void removeCargo(PerishableCargo cargo) {
        return;
    }

    /**
     * PassengerTrain does not support regular cargo. This method does nothing.
     *
     * @param cargo ignored.
     */
    @Override
    public void removeCargo(Cargo cargo) {
        return;
    }

    /**
     * PassengerTrain does not carry perishable cargo, so this method does nothing.
     */
    @Override
    public void updatePerishableCargo() {
        return;
    }
}
