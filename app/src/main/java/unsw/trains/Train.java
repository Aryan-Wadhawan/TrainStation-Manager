package unsw.trains;

import java.util.List;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

/**
 * Abstract base class representing a train that can carry passengers and/or cargo.
 * Subclasses include specific train types such as PassengerTrain, CargoTrain, and BulletTrain.
 */
public abstract class Train {
    private String trainId;
    private Position position;
    private List<String> route;
    private String type;
    private boolean isMovingForward;

    /**
     * Constructs a train with given ID, position, route, and type.
     *
     * @param trainId  Unique identifier for the train.
     * @param position Starting position of the train.
     * @param route    List of station IDs defining the route.
     * @param type     Type of the train (e.g., "PassengerTrain", "CargoTrain").
     */
    public Train(String trainId, Position position, List<String> route, String type) {
        this.trainId = trainId;
        this.position = position;
        this.route = route;
        this.type = type;
        this.isMovingForward = true;
    }

    /**
     * Gets the current speed of the train based on its type and load.
     * Subclasses may override this for custom behavior.
     *
     * @return speed in km/min.
     */
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

    /**
     * Gets the total weight of all passengers and cargo on the train.
     *
     * @return total weight in kg.
     */
    public abstract int getTotalWeight();

    /**
     * Gets the unique ID of the train.
     *
     * @return train ID.
     */
    public String getTrainId() {
        return trainId;
    }

    /**
     * Gets the current position of the train.
     *
     * @return current Position object.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the route of the train, represented by a list of station IDs.
     *
     * @return list of station IDs.
     */
    public List<String> getRoute() {
        return route;
    }

    /**
     * Gets the type of the train (as a string).
     *
     * @return train type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the position of the train.
     *
     * @param newPosition new Position to set.
     */
    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    /**
     * Reverses the direction of the train on its route.
     */
    public void reverseDirection() {
        this.isMovingForward = !this.isMovingForward;
    }

    /**
     * Returns whether the train is currently moving forward on its route.
     *
     * @return true if moving forward, false otherwise.
     */
    public boolean isMovingForward() {
        return isMovingForward;
    }

    /**
     * Returns the list of passengers currently on the train.
     *
     * @return list of passengers.
     */
    public abstract List<Passenger> getPassengers();

    /**
     * Returns the list of regular cargo items on the train.
     *
     * @return list of cargo.
     */
    public abstract List<Cargo> getCargo();

    /**
     * Returns the list of perishable cargo items on the train.
     *
     * @return list of perishable cargo.
     */
    public abstract List<PerishableCargo> getPerishableCargo();

    /**
     * Adds a passenger to the train.
     *
     * @param passenger the passenger to add.
     */
    public abstract void addPassenger(Passenger passenger);

    /**
     * Removes a passenger from the train.
     *
     * @param passenger the passenger to remove.
     */
    public abstract void removePassenger(Passenger passenger);

    /**
     * Adds regular cargo to the train.
     *
     * @param cargo the cargo to add.
     */
    public abstract void addCargo(Cargo cargo);

    /**
     * Adds perishable cargo to the train.
     *
     * @param cargo the perishable cargo to add.
     */
    public abstract void addCargo(PerishableCargo cargo);

    /**
     * Removes a perishable cargo item from the train.
     *
     * @param cargo the perishable cargo to remove.
     */
    public abstract void removeCargo(PerishableCargo cargo);

    /**
     * Removes a regular cargo item from the train.
     *
     * @param cargo the cargo to remove.
     */
    public abstract void removeCargo(Cargo cargo);

    /**
     * Returns true if the train is capable of carrying passengers.
     *
     * @return true if can carry passengers, false otherwise.
     */
    public boolean canCarryPassengers() {
        return this.type.equals("PassengerTrain") || this.type.equals("BulletTrain");
    }

    /**
     * Returns true if the train is capable of carrying cargo.
     *
     * @return true if can carry cargo, false otherwise.
     */
    public boolean canCarryCargo() {
        return this.type.equals("CargoTrain") || this.type.equals("BulletTrain");
    }

    /**
     * Returns information about all loads (passengers, cargo, etc.) on the train.
     *
     * @return list of LoadInfoResponse.
     */
    public abstract List<LoadInfoResponse> getLoadsInfo();

    /**
     * Checks whether the train has capacity to take more load.
     *
     * @return true if there's capacity, false otherwise.
     */
    public abstract boolean hasCapacity();

    /**
     * Updates perishable cargo on the train (e.g., reduce time or remove expired).
     */
    public abstract void updatePerishableCargo();
}
