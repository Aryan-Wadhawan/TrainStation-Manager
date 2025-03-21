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
 * Represents a Cargo Train capable of carrying regular and perishable cargo.
 * Cargo weight affects the speed of the train.
 */
public class CargoTrain extends Train {
    private static final int BASE_SPEED = 3;
    private static final int MAX_CAPACITY_KG = 5000;

    private List<Cargo> cargoList;
    private List<PerishableCargo> perishableCargoList;

    /**
     * Constructs a new CargoTrain with the specified ID, position, and route.
     *
     * @param trainId  Unique identifier for the train.
     * @param position Starting position of the train.
     * @param route    List of station IDs defining the train's path.
     */
    public CargoTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "CargoTrain");
        this.cargoList = new ArrayList<>();
        this.perishableCargoList = new ArrayList<>();
    }

    /**
     * Checks whether the train has remaining capacity for additional cargo.
     *
     * @return true if total weight is below the max capacity, false otherwise.
     */
    public boolean hasCapacity() {
        return getTotalWeight() < MAX_CAPACITY_KG;
    }

    /**
     * Removes a regular cargo item from the train.
     *
     * @param c the cargo item to remove.
     */
    public void removeCargo(Cargo c) {
        cargoList.remove(c);
    }

    /**
     * Gets the list of regular cargo currently on the train.
     *
     * @return list of Cargo.
     */
    @Override
    public List<Cargo> getCargo() {
        return cargoList;
    }

    /**
     * Gets the base speed of the cargo train (3 km/min).
     *
     * @return base speed.
     */
    public int getBaseSpeed() {
        return BASE_SPEED;
    }

    /**
     * Gets the maximum cargo capacity of the train in kilograms.
     *
     * @return max capacity (5000 kg).
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Calculates the total weight of regular and perishable cargo.
     *
     * @return total weight in kg.
     */
    @Override
    public int getTotalWeight() {
        int cargoWeight = cargoList.stream().mapToInt(Cargo::getWeight).sum();
        int perishableCargoWeight = perishableCargoList.stream().mapToInt(Cargo::getWeight).sum();
        return cargoWeight + perishableCargoWeight;
    }

    /**
     * Gets the list of perishable cargo on the train.
     *
     * @return list of PerishableCargo.
     */
    @Override
    public List<PerishableCargo> getPerishableCargo() {
        return perishableCargoList;
    }

    /**
     * Adds a perishable cargo item to the train.
     *
     * @param cargo the perishable cargo to add.
     */
    public void addCargo(PerishableCargo cargo) {
        perishableCargoList.add(cargo);
    }

    /**
     * Adds a regular cargo item to the train.
     *
     * @param cargo the regular cargo to add.
     */
    public void addCargo(Cargo cargo) {
        cargoList.add(cargo);
    }

    /**
     * Computes the current speed based on the train's cargo load.
     * The minimum speed is 1 km/min.
     *
     * @return adjusted speed based on weight.
     */
    @Override
    public double getSpeed() {
        return Math.max(1.0, BASE_SPEED - (getTotalWeight() / 1000.0));
    }

    /**
     * Attempts to load cargo onto the train.
     *
     * @param cargo the cargo to load.
     * @return true if cargo was loaded, false if over capacity.
     */
    public boolean loadCargo(Cargo cargo) {
        if (getTotalWeight() + cargo.getWeight() <= MAX_CAPACITY_KG) {
            cargoList.add(cargo);
            return true;
        }
        return false;
    }

    /**
     * Unloads all regular cargo from the train.
     */
    public void unloadCargo() {
        cargoList.clear();
    }

    /**
     * Gets detailed information about all loads on the train.
     *
     * @return list of LoadInfoResponse for each load.
     */
    @Override
    public List<LoadInfoResponse> getLoadsInfo() {
        List<LoadInfoResponse> loads = new ArrayList<>();

        // Regular Cargo
        loads.addAll(cargoList.stream().map(cargo -> new LoadInfoResponse(cargo.getCargoId(), "Cargo"))
                .collect(Collectors.toList()));

        // Perishable Cargo
        loads.addAll(perishableCargoList.stream()
                .map(perishable -> new LoadInfoResponse(perishable.getCargoId(), "PerishableCargo"))
                .collect(Collectors.toList()));

        return loads;
    }

    /**
     * Returns null since cargo trains do not carry passengers.
     *
     * @return null.
     */
    @Override
    public List<Passenger> getPassengers() {
        return null;
    }

    /**
     * Updates perishable cargo by reducing time left and removing expired items.
     */
    public void updatePerishableCargo() {
        for (PerishableCargo perishableCargo : perishableCargoList) {
            perishableCargo.decreaseTime(1);
        }

        CargoManager.removeExpiredPerishableCargo(perishableCargoList);
    }

    /**
     * CargoTrain does not support passengers. This method does nothing.
     *
     * @param passenger ignored.
     */
    @Override
    public void addPassenger(Passenger passenger) {
        return;
    }

    /**
     * CargoTrain does not support passengers. This method does nothing.
     *
     * @param passenger ignored.
     */
    @Override
    public void removePassenger(Passenger passenger) {
        return;
    }

    /**
     * Removes a perishable cargo item from the train.
     *
     * @param cargo the perishable cargo to remove.
     */
    @Override
    public void removeCargo(PerishableCargo cargo) {
        perishableCargoList.remove(cargo);
    }
}
