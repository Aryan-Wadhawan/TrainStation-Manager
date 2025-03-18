package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import unsw.loads.Cargo;
import unsw.loads.PerishableCargo;
import unsw.response.models.LoadInfoResponse;
import unsw.utils.Position;

public class CargoTrain extends Train {
    private static final int BASE_SPEED = 3;
    private static final int MAX_CAPACITY_KG = 5000;
    private List<Cargo> cargoList;
    private List<PerishableCargo> perishableCargo;

    public CargoTrain(String trainId, Position position, List<String> route) {
        super(trainId, position, route, "CargoTrain");
        this.cargoList = new ArrayList<>();
        this.perishableCargo = new ArrayList<>();
    }

    public boolean hasCapacity() {
        return getCargoWeight() < MAX_CAPACITY_KG;
    }

    public void removeCargo(Cargo c) {
        cargoList.remove(c);
    }

    public List<Cargo> getCargo() {
        return cargoList;
    }

    /**
     * Gets the base speed of the cargo train.
     */
    public int getBaseSpeed() {
        return BASE_SPEED;
    }

    /**
     * Gets the maximum cargo capacity in kg.
     */
    public int getMaxCapacity() {
        return MAX_CAPACITY_KG;
    }

    /**
     * Gets the current cargo weight on board.
     */
    @Override
    public int getCargoWeight() {
        int cargoWeight = cargoList.stream().mapToInt(Cargo::getWeight).sum();
        int perishableCargoWeight = perishableCargo.stream().mapToInt(Cargo::getWeight).sum();
        return cargoWeight + perishableCargoWeight;
    }

    public List<PerishableCargo> getPerishableCargo() {
        return perishableCargo;
    }

    public void addCargo(PerishableCargo cargo) {
        perishableCargo.add(cargo);
    }

    public void addCargo(Cargo cargo) {
        cargoList.add(cargo);
    }

    /**
     * Adjusts speed based on cargo weight.
     */
    @Override
    public double getSpeed() {
        return Math.max(1.0, BASE_SPEED - (getCargoWeight() / 1000.0));
    }

    /**
     * Loads cargo onto the train.
     */
    public boolean loadCargo(Cargo cargo) {
        if (getCargoWeight() + cargo.getWeight() <= MAX_CAPACITY_KG) {
            cargoList.add(cargo);
            return true;
        }
        return false; // Cannot load beyond max capacity
    }

    /**
     * Unloads cargo from the train.
     */
    public void unloadCargo() {
        cargoList.clear(); // Unload all cargo at the destination
    }

    @Override
    public List<LoadInfoResponse> getLoadsInfo() {
        List<LoadInfoResponse> loads = new ArrayList<>();

        // Regular Cargo
        loads.addAll(getCargo().stream().map(cargo -> new LoadInfoResponse(cargo.getCargoId(), "Cargo"))
                .collect(Collectors.toList()));

        // Perishable Cargo
        loads.addAll(getPerishableCargo().stream()
                .map(perishable -> new LoadInfoResponse(perishable.getCargoId(), "PerishableCargo"))
                .collect(Collectors.toList()));

        return loads;
    }

}
