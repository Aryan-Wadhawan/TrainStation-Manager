package unsw.managers;

import unsw.loads.Cargo;
import unsw.loads.PerishableCargo;
import unsw.stations.Station;
import unsw.trains.Train;

import java.util.Iterator;
import java.util.List;

/**
 * Utility class responsible for managing cargo operations such as loading,
 * unloading, and removing expired perishable cargo.
 */
public class CargoManager {
    /**
     * Reference to the TrainMovementManager, used to estimate delivery times.
     */
    private static TrainMovementManager trainMovementManager;

    /**
     * Sets the TrainMovementManager to be used for estimating delivery times.
     *
     * @param manager the TrainMovementManager instance.
     */
    public static void setTrainMovementManager(TrainMovementManager manager) {
        trainMovementManager = manager;
    }

    /**
     * Unloads cargo from the given train if its destination matches the given station.
     * Both regular and perishable cargo are handled.
     *
     * @param train   The train to unload from.
     * @param station The destination station.
     */
    public static void unloadCargo(Train train, Station station) {
        Iterator<Cargo> iterator = train.getCargo().iterator();
        while (iterator.hasNext()) {
            Cargo cargo = iterator.next();
            if (cargo.getDestination().equals(station.getStationId())) {
                iterator.remove();
                train.removeCargo(cargo);
                station.addCargo(cargo);
            }
        }

        Iterator<PerishableCargo> perishableIterator = train.getPerishableCargo().iterator();
        while (perishableIterator.hasNext()) {
            PerishableCargo perishableCargo = perishableIterator.next();
            if (perishableCargo.getDestination().equals(station.getStationId())) {
                perishableIterator.remove();
                train.removeCargo(perishableCargo);
                station.addCargo(perishableCargo);
            }
        }
    }

    /**
     * Loads cargo from a station onto the train if there's available capacity.
     * Skips perishable cargo that will expire before reaching its destination.
     *
     * @param train   The train to load onto.
     * @param station The station to load from.
     */
    public static void boardCargo(Train train, Station station) {
        Iterator<Cargo> regularIterator = station.getCargoWaiting().iterator();
        while (regularIterator.hasNext()) {
            Cargo cargo = regularIterator.next();
            if (train.hasCapacity()) {
                train.addCargo(cargo);
                regularIterator.remove();
                station.removeCargo(cargo);
            }
        }

        Iterator<PerishableCargo> perishableIterator = station.getPerishableCargoWaiting().iterator();
        while (perishableIterator.hasNext()) {
            PerishableCargo perishableCargo = perishableIterator.next();
            int estimatedTime = estimateTimeToDestination(train, perishableCargo.getDestination());

            if (estimatedTime > perishableCargo.getMinutesTillPerish()) {
                continue; // Will perish in transit, skip it
            }

            if (train.hasCapacity()) {
                train.addCargo(perishableCargo);
                perishableIterator.remove();
                station.removeCargo(perishableCargo);
            }
        }
    }

    /**
     * Removes expired perishable cargo from the provided list.
     *
     * @param perishableCargoList List of perishable cargo to filter.
     */
    public static void removeExpiredPerishableCargo(List<PerishableCargo> perishableCargoList) {
        perishableCargoList.removeIf(PerishableCargo::isExpired);
    }

    /**
     * Estimates the travel time in minutes for the train to reach a cargo's destination.
     *
     * @param train           The train carrying the cargo.
     * @param cargoDestination The destination station ID.
     * @return Estimated time in minutes.
     */
    private static int estimateTimeToDestination(Train train, String cargoDestination) {
        if (trainMovementManager == null) {
            throw new IllegalStateException("TrainMovementManager has not been set in CargoManager!");
        }

        String currentLocation = trainMovementManager.getTrainTracker().getTrainLocation(train.getTrainId());
        List<String> route = train.getRoute();

        if (!route.contains(cargoDestination)) {
            throw new IllegalArgumentException("Cargo destination is not in the train's route!");
        }

        double totalDistance = trainMovementManager.getDistanceBetweenStations(currentLocation, cargoDestination);
        return (int) Math.ceil(totalDistance / train.getSpeed());
    }
}
