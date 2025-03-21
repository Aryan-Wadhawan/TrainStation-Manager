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

/**
 * Represents a generic train station that may handle passengers, regular cargo,
 * and perishable cargo. Specialized station types include:
 * {@link PassengerStation}, {@link CargoStation}, {@link CentralStation}, and {@link DepotStation}.
 */
public class Station {
    private String stationId;
    private String type;
    private Position position;

    private List<Train> trains;
    private List<Passenger> passengersWaiting;
    private List<Cargo> regularCargoWaiting;
    private List<PerishableCargo> perishableCargoWaiting;

    /**
     * Constructs a station with the given ID, type, and position.
     *
     * @param stationId Unique identifier for the station.
     * @param type      The type of station (e.g., PassengerStation, CargoStation).
     * @param position  Position of the station on the map/grid.
     */
    public Station(String stationId, String type, Position position) {
        this.stationId = stationId;
        this.type = type;
        this.position = position;
        this.trains = new ArrayList<>();
        this.passengersWaiting = new ArrayList<>();
        this.regularCargoWaiting = new ArrayList<>();
        this.perishableCargoWaiting = new ArrayList<>();
    }

    /**
     * Adds a passenger to the station's waiting list.
     *
     * @param passenger Passenger to add.
     */
    public void addPassenger(Passenger passenger) {
        passengersWaiting.add(passenger);
    }

    /**
     * Removes a passenger from the station's waiting list.
     *
     * @param passenger Passenger to remove.
     */
    public void removePassenger(Passenger passenger) {
        passengersWaiting.remove(passenger);
    }

    /**
     * Returns the list of passengers currently waiting at the station.
     *
     * @return list of passengers.
     */
    public List<Passenger> getPassengersWaiting() {
        return passengersWaiting;
    }

    /**
     * Adds regular cargo to the station's waiting list.
     *
     * @param cargo Cargo to add.
     */
    public void addCargo(Cargo cargo) {
        regularCargoWaiting.add(cargo);
    }

    /**
     * Adds perishable cargo to the station's waiting list.
     *
     * @param cargo Perishable cargo to add.
     */
    public void addCargo(PerishableCargo cargo) {
        perishableCargoWaiting.add(cargo);
    }

    /**
     * Removes regular cargo from the station's waiting list.
     *
     * @param cargo Cargo to remove.
     */
    public void removeCargo(Cargo cargo) {
        regularCargoWaiting.remove(cargo);
    }

    /**
     * Removes perishable cargo from the station's waiting list.
     *
     * @param cargo Perishable cargo to remove.
     */
    public void removeCargo(PerishableCargo cargo) {
        perishableCargoWaiting.remove(cargo);
    }

    /**
     * Returns the list of regular cargo currently waiting at the station.
     *
     * @return list of Cargo.
     */
    public List<Cargo> getCargoWaiting() {
        return regularCargoWaiting;
    }

    /**
     * Returns the list of perishable cargo currently waiting at the station.
     *
     * @return list of PerishableCargo.
     */
    public List<PerishableCargo> getPerishableCargoWaiting() {
        return perishableCargoWaiting;
    }

    /**
     * Returns the station's unique identifier.
     *
     * @return station ID.
     */
    public String getStationId() {
        return stationId;
    }

    /**
     * Returns the station's type (e.g., PassengerStation, CargoStation).
     *
     * @return station type as a string.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the position of the station on the map.
     *
     * @return Position object.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Checks whether the station is at maximum train capacity based on its type.
     *
     * @return true if full, false otherwise.
     */
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
        case "DepotStation":
            maxCapacity = 8;
            break;
        default:
            throw new IllegalArgumentException("Invalid station type: " + type);
        }

        return trains.size() >= maxCapacity;
    }

    /**
     * Returns the list of trains currently at the station.
     *
     * @return list of Train objects.
     */
    public List<Train> getTrains() {
        return trains;
    }

    /**
     * Checks whether the given train is currently at this station.
     *
     * @param train The train to check.
     * @return true if present, false otherwise.
     */
    public boolean hasTrain(Train train) {
        return trains.contains(train);
    }

    /**
     * Removes a train from this station.
     *
     * @param train The train to remove.
     */
    public void removeTrain(Train train) {
        trains.remove(train);
    }

    /**
     * Adds a train to the station if it's not already at max capacity.
     *
     * @param newTrain The train to add.
     */
    public void addTrain(Train newTrain) {
        if (!isFull()) {
            trains.add(newTrain);
        } else {
            throw new IllegalArgumentException("Station " + stationId + " is full and cannot accept more trains.");
        }
    }

    /**
     * Returns a list of information about the current loads (passengers and cargo) waiting at the station.
     *
     * @return list of LoadInfoResponse objects.
     */
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
            for (Cargo cargo : regularCargoWaiting) {
                loads.add(new LoadInfoResponse(cargo.getCargoId(), "Cargo"));
            }

            for (PerishableCargo perishable : perishableCargoWaiting) {
                loads.add(new LoadInfoResponse(perishable.getCargoId(), "PerishableCargo"));
            }
        }

        return loads;
    }

    /**
     * Updates all perishable cargo at the station by decreasing their remaining time.
     * Removes expired cargo via the CargoManager.
     */
    public void updatePerishableCargo() {
        for (PerishableCargo perishableCargo : perishableCargoWaiting) {
            perishableCargo.decreaseTime(1);
        }

        CargoManager.removeExpiredPerishableCargo(perishableCargoWaiting);
    }
}
