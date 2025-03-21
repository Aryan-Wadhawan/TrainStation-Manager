package unsw.trains;

import java.util.Map;

import unsw.stations.Station;
import unsw.tracks.Track;

/**
 * Utility class responsible for tracking the location of trains.
 * It checks whether a train is currently at a station or on a track.
 */
public class TrainTracker {
    /**
     * Map of train IDs to Train objects.
     */
    private Map<String, Train> trains;

    /**
     * Map of station IDs to Station objects.
     */
    private Map<String, Station> stations;

    /**
     * Map of track IDs to Track objects.
     */
    private Map<String, Track> tracks;

    /**
     * Constructs a TrainTracker instance using maps of trains, stations, and tracks.
     *
     * @param trains   Map of train ID to Train.
     * @param stations Map of station ID to Station.
     * @param tracks   Map of track ID to Track.
     */
    public TrainTracker(Map<String, Train> trains, Map<String, Station> stations, Map<String, Track> tracks) {
        this.trains = trains;
        this.stations = stations;
        this.tracks = tracks;
    }

    /**
     * Gets the current location of a train by its ID.
     * First checks if the train is at a station, then on a track.
     *
     * @param trainId The ID of the train.
     * @return Station ID, Track ID, or "Unknown" if not found.
     */
    public String getTrainLocation(String trainId) {
        Train train = trains.get(trainId);
        if (train == null)
            return "Unknown";

        // Check if train is at a station
        for (Station station : stations.values()) {
            if (station.hasTrain(train)) {
                return station.getStationId();
            }
        }

        // Check if train is on a track
        for (Track track : tracks.values()) {
            if (track.hasTrain(train)) {
                return track.getTrackId();
            }
        }

        return "Unknown";
    }

    /**
     * Static utility method to get a train's location using direct input maps.
     * Useful when an instance of TrainTracker is not available.
     *
     * @param trainId  The ID of the train.
     * @param trains   Map of train ID to Train.
     * @param stations Map of station ID to Station.
     * @param tracks   Map of track ID to Track.
     * @return Station ID, Track ID, or "Unknown" if not found.
     */
    public static String getTrainLocation(String trainId, Map<String, Train> trains, Map<String, Station> stations,
            Map<String, Track> tracks) {
        Train train = trains.get(trainId);
        if (train == null)
            return "Unknown";

        for (Station station : stations.values()) {
            if (station.hasTrain(train))
                return station.getStationId();
        }

        for (Track track : tracks.values()) {
            if (track.hasTrain(train))
                return track.getTrackId();
        }

        return "Unknown";
    }
}
