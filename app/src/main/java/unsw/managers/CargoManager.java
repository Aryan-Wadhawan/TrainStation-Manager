package unsw.managers;

import unsw.loads.Cargo;
import unsw.loads.PerishableCargo;
import unsw.stations.Station;
import unsw.trains.Train;

import java.util.Iterator;
import java.util.List;

public class CargoManager {
    private static TrainMovementManager trainMovementManager;

    // Set TrainMovementManager reference (called in TrainsController)
    public static void setTrainMovementManager(TrainMovementManager manager) {
        trainMovementManager = manager;
    }

    public static void unloadCargo(Train train, Station station) {
        Iterator<Cargo> iterator = train.getCargo().iterator();
        while (iterator.hasNext()) {
            Cargo cargo = iterator.next();
            if (cargo.getDestination().equals(station.getStationId())) {
                iterator.remove(); // Unload cargo from train
                train.removeCargo(cargo);
                station.addCargo(cargo); // Move it to the station
            }
        }
        // Handle Perishable Cargo
        Iterator<PerishableCargo> perishableIterator = train.getPerishableCargo().iterator();
        while (perishableIterator.hasNext()) {
            PerishableCargo perishableCargo = perishableIterator.next();
            if (perishableCargo.getDestination().equals(station.getStationId())) {
                perishableIterator.remove(); // Unload from train
                train.removeCargo(perishableCargo);
                station.addCargo(perishableCargo); // Add to station
            }
        }
    }

    // to do, try this unload methof wihout iterator to mak sure cargo taken off train to station

    // public static void unloadCargo(Train train, Station station) {
    //     List<Cargo> cargoList = train.getCargo();
    //     for (Cargo cargo : cargoList) {
    //         if (cargo.getDestination().equals(station.getStationId())) {
    //             //iterator.remove(); // Unload cargo from train
    //             train.removeCargo(cargo);
    //             station.addCargo(cargo); // Move it to the station
    //         }
    //     }
    //     //  Handle Perishable Cargo
    //     List<PerishableCargo> perishCargoList = train.getPerishableCargo();
    //     for (PerishableCargo cargo : perishCargoList) {
    //         if (cargo.getDestination().equals(station.getStationId())) {
    //             //iterator.remove(); // Unload cargo from train
    //             train.removeCargo(cargo);
    //             station.addCargo(cargo); // Move it to the station
    //         }
    //     }
    // }

    public static void boardCargo(Train train, Station station) {
        // Handle regular cargo first
        Iterator<Cargo> regularIterator = station.getCargoWaiting().iterator();
        while (regularIterator.hasNext()) {
            Cargo cargo = regularIterator.next();

            if (train.hasCapacity()) {
                train.addCargo(cargo);
                regularIterator.remove(); // Remove from station
                station.removeCargo(cargo);
            }
        }

        // Handle perishable cargo separately
        Iterator<PerishableCargo> perishableIterator = station.getPerishableCargoWaiting().iterator();
        while (perishableIterator.hasNext()) {
            PerishableCargo perishableCargo = perishableIterator.next();

            int estimatedTime = estimateTimeToDestination(train, perishableCargo.getDestination());

            //If it will expire before reaching its destination, skip it
            if (estimatedTime > perishableCargo.getMinutesTillPerish()) {
                continue;
            }

            if (train.hasCapacity()) {
                train.addCargo(perishableCargo);
                perishableIterator.remove(); // Remove from station
                station.removeCargo(perishableCargo);
            }
        }
    }

    // public static void boardCargo(Train train, Station station) {
    //     // Handle regular cargo first
    //     List<Cargo> regularCargoList = station.getCargoWaiting();
    //     for (Cargo cargo : regularCargoList) {

    //         if (train.hasCapacity()) {
    //             train.addCargo(cargo);
    //             station.removeCargo(cargo);
    //         }
    //     }

    //     // Handle perishable cargo separately
    //     List<PerishableCargo> perishableCargoList = station.getPerishableCargoWaiting();
    //     for (PerishableCargo perishableCargo : perishableCargoList) {
    //         int estimatedTime = estimateTimeToDestination(train, perishableCargo.getDestination());

    //         //If it will expire before reaching its destination, skip it
    //         if (estimatedTime > perishableCargo.getMinutesTillPerish()) {
    //             continue;
    //         }

    //         if (train.hasCapacity()) {
    //             train.addCargo(perishableCargo);
    //             station.removeCargo(perishableCargo);

    //         }
    //     }
    // }

    //  Remove expired perishable cargo ONLY
    public static void removeExpiredPerishableCargo(List<PerishableCargo> perishableCargoList) {
        perishableCargoList.removeIf(PerishableCargo::isExpired);
    }

    private static int estimateTimeToDestination(Train train, String cargoDestination) {
        if (trainMovementManager == null) {
            throw new IllegalStateException("TrainMovementManager has not been set in CargoManager!");
        }

        // Get current position of train
        String currentLocation = trainMovementManager.getTrainTracker().getTrainLocation(train.getTrainId());
        List<String> route = train.getRoute();

        if (!route.contains(cargoDestination)) {
            throw new IllegalArgumentException("Cargo destination is not in the train's route!");
        }

        // Compute distance to the cargo's specific destination
        double totalDistance = trainMovementManager.getDistanceBetweenStations(currentLocation, cargoDestination);

        return (int) Math.ceil(totalDistance / train.getSpeed());
    }

}
