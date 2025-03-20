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

public class TrainMovementManager {
    private Map<String, Station> stations;
    private Map<String, Track> tracks;
    private TrainTracker trainTracker;

    public TrainMovementManager(Map<String, Train> trains, Map<String, Station> stations, Map<String, Track> tracks,
            TrainTracker trainTracker) {
        this.stations = stations;
        this.trainTracker = trainTracker;
        this.tracks = tracks;
    }

    public TrainTracker getTrainTracker() {
        return trainTracker;
    }

    public void moveTrain(Train train) {
        String currentLocation = trainTracker.getTrainLocation(train.getTrainId());

        // Get the station where the train is currently located
        Station station = stations.get(currentLocation);

        // Ensure station is valid before boarding passengers and cargo
        if (station != null) {
            // Reduce time for perishable cargo and remove expired items (Only for Cargo & Bullet trains)
            if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
                for (PerishableCargo perishableCargo : train.getPerishableCargo()) {
                    perishableCargo.decreaseTime(1); // Reduce time by 1 minute
                }
                CargoManager.removeExpiredPerishableCargo(train.getPerishableCargo()); // Remove expired cargo
            }

            // Handle Passenger boarding ONLY for PassengerTrain and BulletTrain
            if (train.getType().equals("PassengerTrain") || train.getType().equals("BulletTrain")) {
                PassengerManager.boardPassengers(train, station);
            }

            // Handle Cargo boarding ONLY for CargoTrain and BulletTrain
            if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
                CargoManager.boardCargo(train, station);
            }
        }

        // Get train route and determine next station
        List<String> route = train.getRoute();
        int currentIndex = route.indexOf(currentLocation);
        if (currentIndex == -1)
            return; // Train is not on a valid route

        int nextIndex = getNextStationIndex(train, currentIndex);
        String nextStationId = route.get(nextIndex);
        Station nextStation = stations.get(nextStationId);

        Position nextPos = nextStation.getPosition();
        double speed = train.getSpeed();

        // Check if the track is broken before proceeding
        Track trackToNextStation = null;
        for (Track track : tracks.values()) {
            if (track.connects(currentLocation, nextStationId)) {
                trackToNextStation = track;
                break;
            }
        }
        if (trackToNextStation instanceof BreakableTrack && ((BreakableTrack) trackToNextStation).isBroken()) {
            return; // Train waits at station if the track is broken
        }

        // Compute Euclidean distance
        double dx = nextPos.getX() - train.getPosition().getX();
        double dy = nextPos.getY() - train.getPosition().getY();
        double distanceToNext = Math.sqrt(dx * dx + dy * dy);

        // If train reaches the station, handle arrival
        if (speed >= distanceToNext) {
            trainArrivesAtStation(train, nextStation);

        } else {
            moveTowards(train, nextPos, speed, distanceToNext);
        }
    }

    private void trainArrivesAtStation(Train train, Station newStation) {
        String prevStationId = trainTracker.getTrainLocation(train.getTrainId());

        // Find the previous track (train is leaving this track)
        Track previousTrack = null;
        for (Track track : tracks.values()) {
            if (track.connects(prevStationId, newStation.getStationId())) {
                previousTrack = track;
                break;
            }
        }

        // Reduce durability AFTER train moves off the track
        if (previousTrack instanceof BreakableTrack) {
            BreakableTrack breakableTrack = (BreakableTrack) previousTrack;
            int totalWeight = train.getTotalWeight(); // Ensure method includes passengers + cargo

            System.out.println("Train " + train.getTrainId() + " leaving " + breakableTrack.getTrackId()
                    + ", reducing durability by " + (1 + Math.ceil(totalWeight / 1000.0)));

            breakableTrack.decreaseDurability(totalWeight);
        }

        Station currentStation = stations.get(trainTracker.getTrainLocation(train.getTrainId()));

        // Unload Passengers & Cargo safely based on train type
        if (train.getType().equals("PassengerTrain") || train.getType().equals("BulletTrain")) {
            PassengerManager.unloadPassengers(train, newStation);
        }
        if (train.getType().equals("CargoTrain") || train.getType().equals("BulletTrain")) {
            CargoManager.unloadCargo(train, newStation);
        }

        if (currentStation != null) {
            currentStation.removeTrain(train); // Remove train from old station
        }

        train.setPosition(newStation.getPosition());
        newStation.addTrain(train); // Add to new station

        if (isLinearTrain(train) && isEndOfRoute(train, newStation)) {
            train.reverseDirection();
        }
    }

    private boolean isLinearTrain(Train train) {
        return train.getType().equals("PassengerTrain") || train.getType().equals("CargoTrain");
    }

    private boolean isEndOfRoute(Train train, Station station) {
        List<String> route = train.getRoute();
        return station.getStationId().equals(route.get(0))
                || station.getStationId().equals(route.get(route.size() - 1));
    }

    private void moveTowards(Train train, Position nextPos, double speed, double distanceToNext) {
        if (distanceToNext == 0) {
            return; // Prevent division by zero
        }

        double ratio = Math.min(1, speed / distanceToNext); // Ensure we don't overshoot

        double newX = train.getPosition().getX() + ratio * (nextPos.getX() - train.getPosition().getX());
        double newY = train.getPosition().getY() + ratio * (nextPos.getY() - train.getPosition().getY());

        train.setPosition(new Position(newX, newY));

        String currentLocation = trainTracker.getTrainLocation(train.getTrainId());
        String nextStationId = train.getRoute()
                .get(getNextStationIndex(train, train.getRoute().indexOf(currentLocation)));

        // Find the track the train is currently moving on
        Track trackMovingOn = null;
        for (Track track : tracks.values()) {
            if (track.connects(currentLocation, nextStationId)) {
                trackMovingOn = track;
                break;
            }
        }

        if (trackMovingOn instanceof BreakableTrack) {
            BreakableTrack breakableTrack = (BreakableTrack) trackMovingOn;

            // Reduce durability by 1 every tick
            System.out.println("Train " + train.getTrainId() + " is moving on " + breakableTrack.getTrackId()
                    + ", reducing durability by 1");

            breakableTrack.decreaseDurability(0); // Just reduce by 1, trainLoad is not needed here
        }
    }

    private int getNextStationIndex(Train train, int currentIndex) {
        boolean isLinear = !train.getType().equals("BulletTrain");

        if (isLinear) {
            return train.isMovingForward()
                    ? (currentIndex == train.getRoute().size() - 1 ? currentIndex - 1 : currentIndex + 1)
                    : (currentIndex == 0 ? currentIndex + 1 : currentIndex - 1);
        } else {
            return (currentIndex + 1) % train.getRoute().size(); // Cyclical route
        }
    }

    // Calculates the Euclidean distance from the train’s current position to its final destination.
    public double getDistanceToDestination(Train train) {
        List<String> route = train.getRoute();
        if (route.isEmpty())
            return 0.0; // No route assigned

        String finalDestinationId = route.get(route.size() - 1);
        Station finalDestination = stations.get(finalDestinationId);

        if (finalDestination == null)
            return 0.0; // Destination not found

        Position currentPos = train.getPosition();
        Position destinationPos = finalDestination.getPosition();

        // Compute Euclidean distance
        double dx = destinationPos.getX() - currentPos.getX();
        double dy = destinationPos.getY() - currentPos.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getDistanceBetweenStations(String startStationId, String endStationId) {
        Station startStation = stations.get(startStationId);
        Station endStation = stations.get(endStationId);

        if (startStation == null || endStation == null) {
            throw new IllegalArgumentException("One of the stations does not exist!");
        }

        Position startPos = startStation.getPosition();
        Position endPos = endStation.getPosition();

        // Compute Euclidean distance
        double dx = endPos.getX() - startPos.getX();
        double dy = endPos.getY() - startPos.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

}
