package unsw.stations;

import unsw.utils.Position;

/**
 * Represents a station dedicated to handling passenger transport.
 * Inherits from the generic {@link Station} class and sets the type to "PassengerStation".
 */
public class PassengerStation extends Station {
    /**
     * Constructs a PassengerStation with the specified ID and position.
     *
     * @param stationId Unique identifier for the station.
     * @param position  The position of the station on the map or grid.
     */
    public PassengerStation(String stationId, Position position) {
        super(stationId, "PassengerStation", position);
    }
}
