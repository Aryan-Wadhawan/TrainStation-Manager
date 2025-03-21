package unsw.trains;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import unsw.exceptions.InvalidRouteException;
import unsw.loads.Cargo;
import unsw.loads.Passenger;
import unsw.loads.PerishableCargo;
import unsw.managers.CargoManager;
import unsw.managers.TrainMovementManager;
import unsw.response.models.*;
import unsw.stations.CargoStation;
import unsw.stations.CentralStation;
import unsw.stations.DepotStation;
import unsw.stations.PassengerStation;
import unsw.stations.Station;
import unsw.utils.Position;
import unsw.utils.TrackType;
import unsw.tracks.BreakableTrack;
import unsw.tracks.Track;

/**
 * The controller for the Trains system.
 *
 * The method signatures here are provided for you. Do NOT change the method
 * signatures.
 */
public class TrainsController {
    private Map<String, Station> stations = new HashMap<>();
    private Map<String, Track> tracks = new HashMap<>();
    private Map<String, Train> trains = new HashMap<>();

    private TrainTracker trainTracker;
    private TrainMovementManager trainMovementManager;

    /**
     * Constructs a new TrainsController and initializes supporting managers.
     */
    public TrainsController() {
        this.trainTracker = new TrainTracker(trains, stations, tracks);
        this.trainMovementManager = new TrainMovementManager(trains, stations, tracks, trainTracker);
        CargoManager.setTrainMovementManager(trainMovementManager);
    }

    /**
     * Creates a station of a given type at the specified coordinates.
     *
     * @param stationId ID of the station.
     * @param type      Station type (PassengerStation, CargoStation, etc).
     * @param x         X-coordinate.
     * @param y         Y-coordinate.
     */
    public void createStation(String stationId, String type, double x, double y) {
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

    /**
     * Creates a basic track between two stations.
     *
     * @param trackId        Track identifier.
     * @param fromStationId  Origin station ID.
     * @param toStationId    Destination station ID.
     */
    public void createTrack(String trackId, String fromStationId, String toStationId) {
        Station fromStation = stations.get(fromStationId);
        Station toStation = stations.get(toStationId);

        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("One or both station IDs do not exist!");
        }

        for (Track track : tracks.values()) {
            if (track.connects(fromStationId, toStationId)) {
                throw new IllegalArgumentException("A track already exists between these stations.");
            }
        }

        Track newTrack = new Track(trackId, fromStationId, toStationId, TrackType.NORMAL);
        tracks.put(trackId, newTrack);
    }

    /**
     * Creates a train of a given type at the specified station, with its route.
     *
     * @param trainId    ID of the train.
     * @param type       Type of the train.
     * @param stationId  Starting station ID.
     * @param route      Ordered list of station IDs forming the route.
     * @throws InvalidRouteException if the route is invalid for the train type.
     */
    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {

        Station firstStation = stations.get(stationId);

        if (firstStation == null) {
            throw new IllegalArgumentException("Station does not exist: " + stationId);
        }

        if (!isValidRoute(route, type)) {
            throw new InvalidRouteException("Invalid route for train type: " + type);
        }

        Position startPosition = firstStation.getPosition();
        Train newTrain;

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

        trains.put(trainId, newTrain);
        firstStation.addTrain(newTrain);
    }

    /**
     * Returns a list of all station IDs.
     *
     * @return list of station IDs.
     */
    public List<String> listStationIds() {
        return new ArrayList<>(stations.keySet());
    }

    /**
     * Returns a list of all track IDs.
     *
     * @return list of track IDs.
     */
    public List<String> listTrackIds() {
        return new ArrayList<>(tracks.keySet());
    }

    /**
     * Returns a list of all train IDs.
     *
     * @return list of train IDs.
     */
    public List<String> listTrainIds() {
        return new ArrayList<>(trains.keySet());
    }

    /**
     * Returns detailed information about a specific train.
     *
     * @param trainId ID of the train.
     * @return TrainInfoResponse containing details.
     */
    public TrainInfoResponse getTrainInfo(String trainId) {
        Train train = trains.get(trainId);
        if (train == null) {
            throw new IllegalArgumentException("Train not found");
        }

        String location = trainTracker.getTrainLocation(trainId);
        return new TrainInfoResponse(train.getTrainId(), location, train.getType(), train.getPosition(),
                train.getLoadsInfo());
    }

    /**
     * Returns detailed information about a specific station.
     *
     * @param stationId ID of the station.
     * @return StationInfoResponse containing details.
     */
    public StationInfoResponse getStationInfo(String stationId) {
        Station station = stations.get(stationId);
        if (station == null) {
            throw new IllegalArgumentException("Station does not exist.");
        }

        return new StationInfoResponse(station.getStationId(), station.getType(), station.getPosition(),
                station.getLoadsInfo(),
                station.getTrains().stream()
                        .map(train -> new TrainInfoResponse(train.getTrainId(),
                                trainTracker.getTrainLocation(train.getTrainId()), train.getType(), train.getPosition(),
                                train.getLoadsInfo()))
                        .collect(Collectors.toList()));
    }

    /**
     * Returns track information given a track ID.
     *
     * @param trackId ID of the track.
     * @return TrackInfoResponse or null if not found.
     */
    public TrackInfoResponse getTrackInfo(String trackId) {
        Track track = tracks.get(trackId);
        if (track == null)
            return null;

        return new TrackInfoResponse(track.getTrackId(), track.getFromStationId(), track.getToStationId(),
                track.getType(), track.getDurability());
    }

    /**
     * Simulates 1 tick of the system: moving trains, updating stations, repairing tracks.
     */
    public void simulate() {
        List<Train> sortedTrains = new ArrayList<>(trains.values());
        sortedTrains.sort(Comparator.comparing(Train::getTrainId));

        for (Station station : stations.values()) {
            station.updatePerishableCargo();
        }

        for (Train train : sortedTrains) {
            trainMovementManager.moveTrain(train);
        }

        for (Track track : tracks.values()) {
            if (track instanceof BreakableTrack) {
                ((BreakableTrack) track).repair();
            }
        }
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

    /**
     * Creates and adds a new passenger to the specified start station.
     *
     * @param startStationId Start station ID.
     * @param destStationId  Destination station ID.
     * @param passengerId    Unique passenger ID.
     */
    public void createPassenger(String startStationId, String destStationId, String passengerId) {
        Station startStation = stations.get(startStationId);
        if (startStation == null || (!startStation.getType().equals("PassengerStation")
                && !startStation.getType().equals("CentralStation"))) {
            throw new IllegalArgumentException("Invalid start station for a passenger.");
        }

        Passenger passenger = new Passenger(passengerId, destStationId);
        startStation.addPassenger(passenger);
    }

    /**
     * Creates and adds a new cargo item to the specified start station.
     *
     * @param startStationId Start station ID.
     * @param destStationId  Destination station ID.
     * @param cargoId        Unique cargo ID.
     * @param weight         Weight of the cargo.
     */
    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        Station startStation = stations.get(startStationId);
        if (startStation == null || (!startStation.getType().equals("CargoStation")
                && !startStation.getType().equals("CentralStation"))) {
            throw new IllegalArgumentException("Invalid start station for cargo.");
        }

        Cargo cargo = new Cargo(cargoId, destStationId, weight);
        startStation.addCargo(cargo);
    }

    /**
     * Creates and adds perishable cargo to the given start station.
     *
     * @param startStationId  Station ID where cargo starts.
     * @param destStationId   Destination station ID.
     * @param cargoId         Unique cargo ID.
     * @param weight          Weight of the cargo.
     * @param minsTillPerish  Minutes until the cargo expires.
     */
    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        Station station = stations.get(startStationId);
        if (station == null) {
            throw new IllegalArgumentException("Invalid station ID");
        }

        PerishableCargo perishableCargo = new PerishableCargo(cargoId, destStationId, weight, minsTillPerish);
        station.addCargo(perishableCargo);
    }

    /**
     * Creates a track between two stations with optional breakability.
     *
     * @param trackId        ID of the track.
     * @param fromStationId  Origin station ID.
     * @param toStationId    Destination station ID.
     * @param isBreakable    True if the track is breakable.
     */
    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        Station fromStation = stations.get(fromStationId);
        Station toStation = stations.get(toStationId);

        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("One or both station IDs do not exist!");
        }

        for (Track track : tracks.values()) {
            if (track.connects(fromStationId, toStationId)) {
                throw new IllegalArgumentException("A track already exists between these stations.");
            }
        }
        if (isBreakable) {
            tracks.put(trackId, new BreakableTrack(trackId, fromStationId, toStationId));
        } else {
            tracks.put(trackId, new Track(trackId, fromStationId, toStationId, TrackType.NORMAL));
        }
    }

    public void createPassenger(String startStationId, String destStationID, String passengerId, boolean isMechanic) {
        // Todo: Task cii
    }

    //////////////// Utility Methods ////////////////////////////////////

    private boolean isCyclicalRoute(List<String> route) {
        return route.size() >= 3 && route.get(0).equals(route.get(route.size() - 1));
    }

    private boolean isValidRoute(List<String> route, String type) {
        if (route.size() < 2)
            return false;

        if (isCyclicalRoute(route)) {
            if (type.equals("PassengerTrain") || type.equals("CargoTrain")) {
                return false;
            }
        }

        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);

            boolean trackExists = tracks.values().stream().anyMatch(track -> track.connects(from, to));
            if (!trackExists)
                return false;
        }

        return true;
    }
}
