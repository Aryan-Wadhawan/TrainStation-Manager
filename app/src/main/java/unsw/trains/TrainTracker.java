package unsw.trains;

import java.util.Map;

import unsw.stations.Station;
import unsw.tracks.Track;

public class TrainTracker {
    private Map<String, Train> trains;
    private Map<String, Station> stations;
    private Map<String, Track> tracks;

    public TrainTracker(Map<String, Train> trains, Map<String, Station> stations, Map<String, Track> tracks) {
        this.trains = trains;
        this.stations = stations;
        this.tracks = tracks;
    }

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

}
