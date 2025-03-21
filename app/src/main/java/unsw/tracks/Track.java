package unsw.tracks;

import java.util.HashSet;
import java.util.Set;

import unsw.trains.Train;
import unsw.utils.TrackType;

/**
 * Represents a rail track connecting two stations. Tracks have types,
 * durability, and can have trains traveling on them.
 * This is the base class for more specific track types such as {@link BreakableTrack}.
 */
public class Track {
    /**
     * Unique identifier for the track.
     */
    private String trackId;

    /**
     * ID of the station where the track starts.
     */
    private String fromStationId;

    /**
     * ID of the station where the track ends.
     */
    private String toStationId;

    /**
     * The current type of the track (e.g., UNBROKEN, BROKEN).
     */
    private TrackType type;

    /**
     * The fixed durability of the track. Default value is 10.
     */
    private final int durability;

    /**
     * The set of trains currently on the track.
     */
    private Set<Train> trainsOnTrack;

    /**
     * Constructs a new Track connecting two stations with a specified type.
     *
     * @param trackId        The unique ID of the track.
     * @param fromStationId  ID of the origin station.
     * @param toStationId    ID of the destination station.
     * @param type           The initial track type.
     */
    public Track(String trackId, String fromStationId, String toStationId, TrackType type) {
        this.trackId = trackId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.type = type;
        this.durability = 10;
        this.trainsOnTrack = new HashSet<>();
    }

    /**
     * Checks if this track connects the two given stations.
     *
     * @param stationA ID of one station.
     * @param stationB ID of the other station.
     * @return true if the track connects the two stations, regardless of direction.
     */
    public boolean connects(String stationA, String stationB) {
        return (fromStationId.equals(stationA) && toStationId.equals(stationB))
                || (fromStationId.equals(stationB) && toStationId.equals(stationA));
    }

    /**
     * Returns the unique track ID.
     *
     * @return the track ID.
     */
    public String getTrackId() {
        return trackId;
    }

    /**
     * Returns the ID of the station where the track starts.
     *
     * @return the from-station ID.
     */
    public String getFromStationId() {
        return fromStationId;
    }

    /**
     * Returns the ID of the station where the track ends.
     *
     * @return the to-station ID.
     */
    public String getToStationId() {
        return toStationId;
    }

    /**
     * Returns the current type of the track.
     *
     * @return the track type.
     */
    public TrackType getType() {
        return type;
    }

    /**
     * Returns the fixed durability value of the track.
     *
     * @return the durability (default is 10).
     */
    public int getDurability() {
        return durability;
    }

    /**
     * Updates the type of the track.
     * Protected to allow modification by subclasses only.
     *
     * @param newType The new track type.
     */
    protected void setType(TrackType newType) {
        this.type = newType;
    }

    /**
     * Checks if the given train is currently on this track.
     *
     * @param train the train to check.
     * @return true if the train is on the track, false otherwise.
     */
    public boolean hasTrain(Train train) {
        return trainsOnTrack.contains(train);
    }
}
