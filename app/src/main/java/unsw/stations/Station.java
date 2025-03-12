package unsw.stations;

import java.util.ArrayList;
import java.util.List;

import unsw.trains.Train;
import unsw.utils.Position;

public class Station {
    private String stationId;
    private String type;
    private Position position;
    private List<Train> trains;

    public Station(String stationId, String type, Position position) {
        this.stationId = stationId;
        this.type = type;
        this.position = position;
        this.trains = new ArrayList<>();
        // this.loads = new ArrayList<>();
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

}
