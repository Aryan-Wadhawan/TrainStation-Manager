package unsw.managers;

import unsw.loads.Passenger;
import unsw.stations.Station;
import unsw.trains.Train;

import java.util.ArrayList;
import java.util.List;

public class PassengerManager {
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

    public static void boardPassengers(Train train, Station station) {
        if (train.getType().equals("CargoTrain")) {
            return; // Only Passenger & Bullet trains can take passengers
        }

        List<Passenger> toBoard = new ArrayList<>();
        for (Passenger passenger : station.getPassengersWaiting()) {
            // if (train.getPassengers().size() < 50 && train.getRoute().contains(passenger.getDestination())) {
            //     toBoard.add(passenger);
            // }
            if (train.getRoute().contains(passenger.getDestination())) {
                toBoard.add(passenger);
            }
        }
        for (Passenger passenger : toBoard) {
            train.addPassenger(passenger);
            station.removePassenger(passenger);
        }
    }
}
