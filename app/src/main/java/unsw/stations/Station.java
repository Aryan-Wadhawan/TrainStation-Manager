package unsw.stations;

import java.util.ArrayList;
import java.util.List;

import unsw.loads.Cargo;

import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.managers.CargoManager;
import unsw.response.models.LoadInfoResponse;
import unsw.trains.Train;
import unsw.utils.Position;

public class Station {
    private String stationId;
    private String type;
    private Position position;
    private List<Train> trains;
    private List<Passenger> passengersWaiting;
    private List<Cargo> regularCargoWaiting;
    private List<PerishableCargo> perishableCargoWaiting;

    public Station(String stationId, String type, Position position) {
        this.stationId = stationId;
        this.type = type;
        this.position = position;
        this.trains = new ArrayList<>();
        this.passengersWaiting = new ArrayList<>();
        this.regularCargoWaiting = new ArrayList<>();
        this.perishableCargoWaiting = new ArrayList<>();
    }

    public void addPassenger(Passenger passenger) {
        passengersWaiting.add(passenger);
    }

    public void removePassenger(Passenger passenger) {
        passengersWaiting.remove(passenger);
    }

    public List<Passenger> getPassengersWaiting() {
        return passengersWaiting;
    }

    public void addCargo(Cargo cargo) {
        regularCargoWaiting.add(cargo);

    }

    public void addCargo(PerishableCargo cargo) {
        perishableCargoWaiting.add(cargo);

    }

    // public void removeCargo(Cargo cargo) {
    //     if (cargo instanceof PerishableCargo) {
    //         perishableCargoWaiting.remove(cargo);
    //     } else {
    //         regularCargoWaiting.remove(cargo);
    //     }
    // }

    public void removeCargo(Cargo cargo) {

        regularCargoWaiting.remove(cargo);

    }

    public void removeCargo(PerishableCargo cargo) {

        perishableCargoWaiting.remove(cargo);

    }

    public List<Cargo> getCargoWaiting() {
        // Remove expired perishable cargo before returning list
        //perishableCargoWaiting.removeIf(PerishableCargo::isExpired);
        return regularCargoWaiting;
    }

    public List<PerishableCargo> getPerishableCargoWaiting() {
        //perishableCargoWaiting.removeIf(PerishableCargo::isExpired);
        return perishableCargoWaiting;
    }

    public String getStationId() {
        return stationId;
    }

    public String getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isFull() {
        int maxCapacity;

        switch (type) {
        case "PassengerStation":
            maxCapacity = 2;
            break;
        case "CargoStation":
            maxCapacity = 4;
            break;
        case "CentralStation":
            maxCapacity = 8;
            break;
        case "DepotStation":
            maxCapacity = 8;
            break;
        default:
            throw new IllegalArgumentException("Invalid station type: " + type);
        }

        return trains.size() >= maxCapacity;
    }

    public List<Train> getTrains() {
        return trains;
    }

    public boolean hasTrain(Train train) {
        return trains.contains(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
    }

    /**
     * Adds a train to this station if it's not full.
     */
    public void addTrain(Train newTrain) {
        if (!isFull()) {
            trains.add(newTrain);
        } else {
            throw new IllegalArgumentException("Station " + stationId + " is full and cannot accept more trains.");
        }
    }

    public List<LoadInfoResponse> getLoadsInfo() {
        List<LoadInfoResponse> loads = new ArrayList<>();

        // If station supports passengers, add passenger info
        if (this instanceof PassengerStation || this instanceof CentralStation) {
            for (Passenger passenger : passengersWaiting) {
                loads.add(new LoadInfoResponse(passenger.getPassengerId(), "Passenger"));
            }
        }

        // If station supports cargo, add cargo info
        if (this instanceof CargoStation || this instanceof CentralStation) {
            // Add regular cargo info
            for (Cargo cargo : regularCargoWaiting) {
                loads.add(new LoadInfoResponse(cargo.getCargoId(), "Cargo"));
            }

            // Add perishable cargo info
            for (PerishableCargo perishable : perishableCargoWaiting) {
                loads.add(new LoadInfoResponse(perishable.getCargoId(), "PerishableCargo"));
            }
        }

        return loads;
    }

    public void updatePerishableCargo() {
        // Reduce time for perishable cargo at the station
        for (PerishableCargo perishableCargo : perishableCargoWaiting) {
            perishableCargo.decreaseTime(1);
        }

        // Remove expired perishable cargo from the station
        CargoManager.removeExpiredPerishableCargo(perishableCargoWaiting);
    }

}
