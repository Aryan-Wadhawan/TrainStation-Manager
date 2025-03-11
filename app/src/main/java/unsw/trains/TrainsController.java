package unsw.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.*;
import unsw.stations.CargoStation;
import unsw.stations.CentralStation;
import unsw.stations.DepotStation;
import unsw.stations.PassengerStation;
import unsw.stations.Station;
import unsw.utils.Position;
import unsw.utils.TrackType;
import unsw.tracks.Track;

/**
 * The controller for the Trains system.
 *
 * The method signatures here are provided for you. Do NOT change the method signatures.
 */
public class TrainsController {
    // Add any fields here if necessary
    private Map<String, Station> stations = new HashMap<>();
    private Map<String, Track> tracks = new HashMap<>();
    private Map<String, Train> trains = new HashMap<>();

    public void createStation(String stationId, String type, double x, double y) {
        // Todo: Task ai
        Station newStation;
        switch (type) {
        case "PassengerStation":
            newStation = new PassengerStation(stationId, new Position(x, y));
            break;
        case "CargoStation":
            newStation = new CargoStation(stationId, new Position(x, y));
            break;
        case "CentralStation":
            newStation = new CentralStation(stationId, new Position(x, y));
            break;
        case "DepotStation":
            newStation = new DepotStation(stationId, new Position(x, y));
            break;
        default:
            throw new IllegalArgumentException("Invalid station type: " + type);
        }

        stations.put(stationId, newStation);

    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        // Todo: Task aii
        Station fromStation = stations.get(fromStationId);
        Station toStation = stations.get(toStationId);

        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("One or both of the station IDs do not exist!");
        }

        for (Track track : tracks.values()) {
            if (track.connects(fromStationId, toStationId)) {
                throw new IllegalArgumentException("A track already exists between the two provided stations!");
            }
        }

        Track newTrack = new Track(trackId, fromStationId, toStationId, TrackType.NORMAL);
        tracks.put(trackId, newTrack);
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        // Todo: Task aiii
        Station firstStation = stations.get(stationId);

        if (firstStation == null) {
            throw new IllegalArgumentException("Station does not exist: " + stationId);
        }
        // check if no capacity lefrt at station  LATERRR
        if (!isValidRoute(route, type)) {
            throw new InvalidRouteException("Invalid route for train type: " + type);
        }

        Train newTrain;
        Position startPosition = firstStation.getPosition();

        switch (type) {
        case "PassengerTrain":
            newTrain = new PassengerTrain(trainId, startPosition, route);
            break;
        case "CargoTrain":
            newTrain = new CargoTrain(trainId, startPosition, route);
            break;
        case "BulletTrain":
            newTrain = new BulletTrain(trainId, startPosition, route);
            break;
        default:
            throw new IllegalArgumentException("Invalid train type: " + type);
        }

        trains.put(stationId, newTrain);
        firstStation.addTrain(newTrain);
    }

    public List<String> listStationIds() {
        // Todo: Task aiv
        return new ArrayList<>();
    }

    public List<String> listTrackIds() {
        // Todo: Task av
        return new ArrayList<>();
    }

    public List<String> listTrainIds() {
        // Todo: Task avi
        return new ArrayList<>();
    }

    public TrainInfoResponse getTrainInfo(String trainId) {
        // Todo: Task avii
        return null;
    }

    public StationInfoResponse getStationInfo(String stationId) {
        // Todo: Task aviii
        return null;
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        // Todo: Task aix
        return null;
    }

    public void simulate() {
        // Todo: Task bi
    }

    /**
     * Simulate for the specified number of minutes. You should NOT modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId) {
        // Todo: Task bii
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        // Todo: Task bii
    }

    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        // Todo: Task biii
    }

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        // Todo: Task ci
    }

    public void createPassenger(String startStationId, String destStationID, String passengerId, boolean isMechanic) {
        // Todo: Task cii
    }

    /////// SOME UTILITY FUNCTIONS ///////////////////////////////////////////////////////////

    private boolean isCyclicalRoute(List<String> route) {
        return route.size() >= 3 && route.get(0).equals(route.get(route.size() - 1));
    }

    private boolean isValidRoute(List<String> route, String type) {
        if (route.size() < 2) {
            return false;
        }

        boolean isCyclical = isCyclicalRoute(route);
        if (type.equals("PassengerTrain") || type.equals("CargoTrain")) {
            return !isCyclical;
        }

        if (type.equals("BulletTrain")) {
            return true;
        }

        return false;
    }
}
