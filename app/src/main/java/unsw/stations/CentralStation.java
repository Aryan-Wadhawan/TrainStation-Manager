package unsw.stations;

import unsw.utils.Position;

/**
 * Represents a central station that can handle both passengers and cargo.
 * Inherits from the generic {@link Station} class and sets the type to "CentralStation".
 */
public class CentralStation extends Station {
    /**
     * Constructs a CentralStation with the specified ID and position.
     *
     * @param stationId Unique identifier for the station.
     * @param position  The position of the station on the map or grid.
     */
    public CentralStation(String stationId, Position position) {
        super(stationId, "CentralStation", position);
    }
}
