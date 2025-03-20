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

public class BulletTrain extends Train {
    private static final int BASE_SPEED = 5;
    private static final int MAX_CAPACITY_KG = 5000;
    private List<Passenger> passengers;
    private List<Cargo> cargoList;
    private List<PerishableCargo> perishableCargo;

    public BulletTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "BulletTrain");
        this.passengers = new ArrayList<>();
        this.cargoList = new ArrayList<>();
        this.perishableCargo = new ArrayList<>();
    }

    /**
     * Gets the base speed of the bullet train.
     */
    public int getBaseSpeed() {
        return BASE_SPEED;
    }

    /**
     * Gets the maximum combined capacity for passengers & cargo.
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Gets the current total weight of passengers and cargo.
     */
    @Override
    public int getTotalWeight() {
        int passengerWeight = passengers.size() * 70; // Each passenger weighs 70kg
        int cargoWeight = cargoList.stream().mapToInt(Cargo::getWeight).sum();
        int perishableCargoWeight = perishableCargo.stream().mapToInt(Cargo::getWeight).sum();
        return passengerWeight + cargoWeight + perishableCargoWeight;
    }

    @Override
    public List<PerishableCargo> getPerishableCargo() {
        return perishableCargo;
    }

    public void addCargo(PerishableCargo cargo) {
        perishableCargo.add(cargo);
    }

    public void addCargo(Cargo cargo) {
        cargoList.add(cargo);
    }

    @Override
    public void removeCargo(PerishableCargo cargo) {

        perishableCargo.remove(cargo);

    }

    @Override
    public void removeCargo(Cargo cargo) {

        cargoList.remove(cargo);

    }

    /**
     * Adjusts speed based on weight, minimum speed = 2km/min.
     */
    @Override
    public double getSpeed() {
        return Math.max(2.0, BASE_SPEED - (getTotalWeight() / 1000.0));
    }

    /**
     * Loads passengers onto the train.
     */
    public boolean loadPassenger(Passenger passenger) {
        if (getTotalWeight() + 70 <= MAX_CAPACITY_KG) { // Each passenger is 70kg
            passengers.add(passenger);
            return true;
        }
        return false; // Cannot load beyond max capacity
    }

    /**
     * Loads cargo onto the train.
     */
    public boolean loadCargo(Cargo cargo) {
        if (getTotalWeight() + cargo.getWeight() <= MAX_CAPACITY_KG) {
            cargoList.add(cargo);
            return true;
        }
        return false; // Cannot load beyond max capacity
    }

    /**
     * Unloads passengers and cargo from the train.
     */
    public void unload() {
        passengers.clear(); // Remove all passengers at destination
        cargoList.clear(); // Remove all cargo at destination
    }

    @Override
    public boolean hasCapacity() {
        return getTotalWeight() < MAX_CAPACITY_KG;
    }

    @Override
    public void addPassenger(Passenger p) {
        if (hasCapacity()) {
            passengers.add(p);
        }
    }

    @Override
    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    @Override
    public List<Passenger> getPassengers() {
        return passengers;
    }

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

    public void updatePerishableCargo() {
        // Reduce time for perishable cargo on train
        for (PerishableCargo perishableCargo : perishableCargo) {
            perishableCargo.decreaseTime(1);
        }

        // Remove expired perishable cargo from the station
        CargoManager.removeExpiredPerishableCargo(perishableCargo);
    }

    @Override
    public List<Cargo> getCargo() {
        return cargoList;
    }

}
