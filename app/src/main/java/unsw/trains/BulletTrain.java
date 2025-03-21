package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.managers.CargoManager;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

/**
 * Represents a Bullet Train with support for transporting passengers,
 * regular cargo, and perishable cargo.
 * The train has a base speed and a maximum weight capacity, and its speed
 * decreases as total weight increases.
 */
public class BulletTrain extends Train {
    private static final int BASE_SPEED = 5;
    private static final int MAX_CAPACITY_KG = 5000;
    private static final int PASSENGER_WEIGHT = 70;

    private List<Passenger> passengers;
    private List<Cargo> cargoList;
    private List<PerishableCargo> perishableCargo;

    /**
     * Constructs a BulletTrain with the specified ID, initial position, and route.
     *
     * @param trainId  Unique ID of the train.
     * @param position Starting position of the train.
     * @param route    List of station IDs defining the route.
     */
    public BulletTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "BulletTrain");
        this.passengers = new ArrayList<>();
        this.cargoList = new ArrayList<>();
        this.perishableCargo = new ArrayList<>();
    }

    /**
     * Gets the base speed of the bullet train (5 km/min).
     *
     * @return base speed in km/min.
     */
    public int getBaseSpeed() {
        return BASE_SPEED;
    }

    /**
     * Gets the maximum load capacity of the train in kilograms.
     *
     * @return max capacity (5000kg).
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Calculates the total current weight of passengers and cargo.
     *
     * @return total weight in kg.
     */
    @Override
    public int getTotalWeight() {
        int passengerWeight = passengers.size() * PASSENGER_WEIGHT; // Each passenger = 70kg
        int cargoWeight = cargoList.stream().mapToInt(Cargo::getWeight).sum();
        int perishableCargoWeight = perishableCargo.stream().mapToInt(Cargo::getWeight).sum();
        return passengerWeight + cargoWeight + perishableCargoWeight;
    }

    /**
     * Returns the list of perishable cargo currently on the train.
     *
     * @return list of perishable cargo.
     */
    @Override
    public List<PerishableCargo> getPerishableCargo() {
        return perishableCargo;
    }

    /**
     * Adds a perishable cargo item to the train.
     *
     * @param cargo the perishable cargo to add.
     */
    public void addCargo(PerishableCargo cargo) {
        perishableCargo.add(cargo);
    }

    /**
     * Adds regular cargo to the train.
     *
     * @param cargo the cargo to add.
     */
    public void addCargo(Cargo cargo) {
        cargoList.add(cargo);
    }

    /**
     * Removes a perishable cargo item from the train.
     *
     * @param cargo the cargo to remove.
     */
    @Override
    public void removeCargo(PerishableCargo cargo) {
        perishableCargo.remove(cargo);
    }

    /**
     * Removes regular cargo from the train.
     *
     * @param cargo the cargo to remove.
     */
    @Override
    public void removeCargo(Cargo cargo) {
        cargoList.remove(cargo);
    }

    /**
     * Calculates the adjusted speed based on total weight.
     * The speed cannot go below 2 km/min.
     *
     * @return adjusted speed.
     */
    @Override
    public double getSpeed() {
        return Math.max(2.0, BASE_SPEED - (getTotalWeight() / 1000.0));
    }

    /**
     * Attempts to load a passenger onto the train.
     *
     * @param passenger the passenger to load.
     * @return true if loaded successfully, false if capacity would be exceeded.
     */
    public boolean loadPassenger(Passenger passenger) {
        if (getTotalWeight() + PASSENGER_WEIGHT <= MAX_CAPACITY_KG) {
            passengers.add(passenger);
            return true;
        }
        return false;
    }

    /**
     * Attempts to load cargo onto the train.
     *
     * @param cargo the cargo to load.
     * @return true if loaded successfully, false if capacity would be exceeded.
     */
    public boolean loadCargo(Cargo cargo) {
        if (getTotalWeight() + cargo.getWeight() <= MAX_CAPACITY_KG) {
            cargoList.add(cargo);
            return true;
        }
        return false;
    }

    /**
     * Unloads all passengers and cargo (excluding perishable cargo).
     */
    public void unload() {
        passengers.clear();
        cargoList.clear();
    }

    /**
     * Checks if the train has capacity to load more weight.
     *
     * @return true if under max capacity.
     */
    @Override
    public boolean hasCapacity() {
        return getTotalWeight() < MAX_CAPACITY_KG;
    }

    /**
     * Adds a passenger to the train if there's capacity.
     *
     * @param p the passenger to add.
     */
    @Override
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
    @Override
    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    /**
     * Gets the list of all passengers currently on the train.
     *
     * @return list of passengers.
     */
    @Override
    public List<Passenger> getPassengers() {
        return passengers;
    }

    /**
     * Gets a list of information about all loads on the train.
     * Includes passengers, cargo, and perishable cargo.
     *
     * @return list of load info responses.
     */
    @Override
    public List<LoadInfoResponse> getLoadsInfo() {
        List<LoadInfoResponse> loads = new ArrayList<>();

        // Passengers
        loads.addAll(
                getPassengers().stream().map(passenger -> new LoadInfoResponse(passenger.getPassengerId(), "Passenger"))
                        .collect(Collectors.toList()));

        // Regular Cargo
        loads.addAll(getCargo().stream().map(cargo -> new LoadInfoResponse(cargo.getCargoId(), "Cargo"))
                .collect(Collectors.toList()));

        // Perishable Cargo
        loads.addAll(getPerishableCargo().stream()
                .map(perishable -> new LoadInfoResponse(perishable.getCargoId(), "PerishableCargo"))
                .collect(Collectors.toList()));

        return loads;
    }

    /**
     * Updates all perishable cargo by reducing their remaining time by 1 unit.
     * Removes expired items using the CargoManager.
     */
    public void updatePerishableCargo() {
        for (PerishableCargo perishableCargo : perishableCargo) {
            perishableCargo.decreaseTime(1);
        }

        CargoManager.removeExpiredPerishableCargo(perishableCargo);
    }

    /**
     * Gets the list of regular cargo currently on the train.
     *
     * @return list of cargo.
     */
    @Override
    public List<Cargo> getCargo() {
        return cargoList;
    }
}
