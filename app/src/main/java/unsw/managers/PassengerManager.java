package unsw.managers;

import unsw.loads.Passenger;
import unsw.stations.Station;
import unsw.trains.Train;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for managing passenger operations including boarding and unloading passengers.
 */
public class PassengerManager {
    /**
     * Unloads passengers from the train who have reached their destination station.
     *
     * @param train   The train from which passengers are to be unloaded.
     * @param station The station where the train has arrived.
     */
    public static void unloadPassengers(Train train, Station station) {
        List<Passenger> toUnload = new ArrayList<>();
        for (Passenger passenger : train.getPassengers()) {
            if (passenger.getDestination().equals(station.getStationId())) {
                toUnload.add(passenger);
            }
        }
        for (Passenger passenger : toUnload) {
            train.removePassenger(passenger);
        }
    }

    /**
     * Boards passengers from a station onto a train, if the train supports passenger transport
     * and has capacity. Passengers are only boarded if their destination is on the train's route.
     *
     * @param train   The train to board passengers onto.
     * @param station The station from which passengers are boarding.
     */
    public static void boardPassengers(Train train, Station station) {
        if (train.getType().equals("CargoTrain")) {
            return; // Only PassengerTrain & BulletTrain can carry passengers
        }

        List<Passenger> toBoard = new ArrayList<>();
        for (Passenger passenger : station.getPassengersWaiting()) {
            if (train.getRoute().contains(passenger.getDestination()) && train.hasCapacity()) {
                toBoard.add(passenger);
            }
        }
        for (Passenger passenger : toBoard) {
            train.addPassenger(passenger);
            station.removePassenger(passenger);
        }
    }
}
