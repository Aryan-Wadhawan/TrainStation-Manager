package unsw.managers;

import unsw.loads.PerishableCargo;
import unsw.stations.Station;
import unsw.tracks.BreakableTrack;
import unsw.tracks.Track;
import unsw.trains.Train;
import unsw.trains.TrainTracker;
import unsw.utils.Position;

import java.util.List;
import java.util.Map;

/**
 * Manages the movement of trains between stations and tracks, handling logic for:
 * - Passenger/cargo boarding and unloading
 * - Movement based on speed and distance
 * - Track durability and breakdown handling
 */
public class TrainMovementManager {
    private Map<String, Station> stations;
    private Map<String, Track> tracks;
    private TrainTracker trainTracker;

    /**
     * Constructs a TrainMovementManager with references to the full system.
     *
     * @param trains        Map of train ID to Train objects.
     * @param stations      Map of station ID to Station objects.
     * @param tracks        Map of track ID to Track objects.
     * @param trainTracker  Tracker used to find train locations.
     */
    public TrainMovementManager(Map<String, Train> trains, Map<String, Station> stations, Map<String, Track> tracks,
            TrainTracker trainTracker) {
        this.stations = stations;
        this.trainTracker = trainTracker;
        this.tracks = tracks;
    }

    /**
     * Returns the train tracker associated with this manager.
     *
     * @return TrainTracker instance.
     */
    public TrainTracker getTrainTracker() {
        return trainTracker;
    }

    /**
     * Moves a train from its current position toward the next station.
     * Handles boarding/unloading, track durability, and direction.
     *
     * @param train The train to move.
     */
    public void moveTrain(Train train) {
        String currentLocation = trainTracker.getTrainLocation(train.getTrainId());
        Station station = stations.get(currentLocation);

        // Handle boarding logic and perishable cargo updates
        if (station != null) {
            if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
                for (PerishableCargo cargo : train.getPerishableCargo()) {
                    cargo.decreaseTime(1);
                }
                CargoManager.removeExpiredPerishableCargo(train.getPerishableCargo());
            }

            if (train.getType().equals("PassengerTrain") || train.getType().equals("BulletTrain")) {
                PassengerManager.boardPassengers(train, station);
            }

            if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
                CargoManager.boardCargo(train, station);
            }
        }

        List<String> route = train.getRoute();
        int currentIndex = route.indexOf(currentLocation);
        if (currentIndex == -1)
            return;

        int nextIndex = getNextStationIndex(train, currentIndex);
        String nextStationId = route.get(nextIndex);
        Station nextStation = stations.get(nextStationId);
        Position nextPos = nextStation.getPosition();
        double speed = train.getSpeed();

        Track trackToNextStation = null;
        for (Track track : tracks.values()) {
            if (track.connects(currentLocation, nextStationId)) {
                trackToNextStation = track;
                break;
            }
        }

        if (trackToNextStation instanceof BreakableTrack && ((BreakableTrack) trackToNextStation).isBroken()) {
            return; // Wait if track is broken
        }

        double dx = nextPos.getX() - train.getPosition().getX();
        double dy = nextPos.getY() - train.getPosition().getY();
        double distanceToNext = Math.sqrt(dx * dx + dy * dy);

        if (speed >= distanceToNext) {
            trainArrivesAtStation(train, nextStation);
        } else {
            moveTowards(train, nextPos, speed, distanceToNext);
        }
    }

    /**
     * Handles logic when a train arrives at a station.
     * Unloads cargo/passengers, updates track durability, and reverses direction if needed.
     *
     * @param train      The arriving train.
     * @param newStation The destination station.
     */
    private void trainArrivesAtStation(Train train, Station newStation) {
        String prevStationId = trainTracker.getTrainLocation(train.getTrainId());

        // Find previous track
        Track previousTrack = null;
        for (Track track : tracks.values()) {
            if (track.connects(prevStationId, newStation.getStationId())) {
                previousTrack = track;
                break;
            }
        }

        if (previousTrack instanceof BreakableTrack) {
            BreakableTrack breakableTrack = (BreakableTrack) previousTrack;
            int totalWeight = train.getTotalWeight();
            breakableTrack.decreaseDurability(totalWeight);
        }

        Station currentStation = stations.get(prevStationId);

        // Unload based on train type
        if (train.getType().equals("PassengerTrain") || train.getType().equals("BulletTrain")) {
            PassengerManager.unloadPassengers(train, newStation);
        }
        if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
            CargoManager.unloadCargo(train, newStation);
        }

        if (currentStation != null) {
            currentStation.removeTrain(train);
        }

        train.setPosition(newStation.getPosition());
        newStation.addTrain(train);

        if (isLinearTrain(train) && isEndOfRoute(train, newStation)) {
            train.reverseDirection();
        }
    }

    /**
     * Checks if a train uses a linear route (not cyclical).
     *
     * @param train The train to check.
     * @return true if linear, false otherwise.
     */
    private boolean isLinearTrain(Train train) {
        return train.getType().equals("PassengerTrain") || train.getType().equals("CargoTrain");
    }

    /**
     * Checks if a train is at the end of its route (for reversing direction).
     *
     * @param train   The train to check.
     * @param station The current station.
     * @return true if train is at start/end of route.
     */
    private boolean isEndOfRoute(Train train, Station station) {
        List<String> route = train.getRoute();
        return station.getStationId().equals(route.get(0))
                || station.getStationId().equals(route.get(route.size() - 1));
    }

    /**
     * Moves a train toward a destination station, reducing distance based on speed.
     * Also applies durability reduction if on a breakable track.
     *
     * @param train           The train to move.
     * @param nextPos         Position of next station.
     * @param speed           Speed of the train.
     * @param distanceToNext  Distance to the next station.
     */
    private void moveTowards(Train train, Position nextPos, double speed, double distanceToNext) {
        if (distanceToNext == 0)
            return;

        double ratio = Math.min(1, speed / distanceToNext);

        double newX = train.getPosition().getX() + ratio * (nextPos.getX() - train.getPosition().getX());
        double newY = train.getPosition().getY() + ratio * (nextPos.getY() - train.getPosition().getY());

        train.setPosition(new Position(newX, newY));

        String currentLocation = trainTracker.getTrainLocation(train.getTrainId());
        String nextStationId = train.getRoute()
                .get(getNextStationIndex(train, train.getRoute().indexOf(currentLocation)));

        Track track = null;
        for (Track t : tracks.values()) {
            if (t.connects(currentLocation, nextStationId)) {
                track = t;
                break;
            }
        }

        if (track instanceof BreakableTrack) {
            ((BreakableTrack) track).decreaseDurability(0); // Reduce by 1 tick
        }
    }

    /**
     * Determines the next station index for the train based on direction and type.
     *
     * @param train        The train.
     * @param currentIndex Current index in the route.
     * @return Index of the next station.
     */
    private int getNextStationIndex(Train train, int currentIndex) {
        boolean isLinear = !train.getType().equals("BulletTrain");

        if (isLinear) {
            return train.isMovingForward()
                    ? (currentIndex == train.getRoute().size() - 1 ? currentIndex - 1 : currentIndex + 1)
                    : (currentIndex == 0 ? currentIndex + 1 : currentIndex - 1);
        } else {
            return (currentIndex + 1) % train.getRoute().size();
        }
    }

    /**
     * Calculates the Euclidean distance from the train's current position to the final station in its route.
     *
     * @param train The train.
     * @return Distance in units.
     */
    public double getDistanceToDestination(Train train) {
        List<String> route = train.getRoute();
        if (route.isEmpty())
            return 0.0;

        String finalDestinationId = route.get(route.size() - 1);
        Station destination = stations.get(finalDestinationId);
        if (destination == null)
            return 0.0;

        Position current = train.getPosition();
        Position dest = destination.getPosition();

        double dx = dest.getX() - current.getX();
        double dy = dest.getY() - current.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the Euclidean distance between two stations by ID.
     *
     * @param startStationId Start station ID.
     * @param endStationId   End station ID.
     * @return Distance in units.
     */
    public double getDistanceBetweenStations(String startStationId, String endStationId) {
        Station start = stations.get(startStationId);
        Station end = stations.get(endStationId);

        if (start == null || end == null) {
            throw new IllegalArgumentException("One of the stations does not exist!");
        }

        double dx = end.getPosition().getX() - start.getPosition().getX();
        double dy = end.getPosition().getY() - start.getPosition().getY();

        return Math.sqrt(dx * dx + dy * dy);
    }
}
