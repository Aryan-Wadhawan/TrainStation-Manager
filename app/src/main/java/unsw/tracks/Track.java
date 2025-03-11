package unsw.tracks;

import unsw.utils.TrackType;

public class Track {
    private String trackId;
    private String fromStationId;
    private String toStationId;
    private TrackType type;
    private final int durability;

    public Track(String trackId, String fromStationId, String toStationId, TrackType type) {
        this.trackId = trackId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.type = type;

        // for now, better to use ENUM maybe
        this.durability = 10;
    }

    public boolean connects(String stationA, String stationB) {
        return (fromStationId.equals(stationA) && toStationId.equals(stationB))
                || (fromStationId.equals(stationB) && toStationId.equals(stationA));
    }

    public String getTrackId() {
        return trackId;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public TrackType getType() {
        return type;
    }

    public int getDurability() {
        return durability;
    }
}
