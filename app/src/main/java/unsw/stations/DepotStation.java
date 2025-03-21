package unsw.stations;

import unsw.utils.Position;

/**
 * Represents a depot station used for storing or dispatching trains.
 * Inherits from the generic {@link Station} class and sets the type to "DepotStation".
 */
public class DepotStation extends Station {
    /**
     * Constructs a DepotStation with the specified ID and position.
     *
     * @param stationId Unique identifier for the station.
     * @param position  The position of the station on the map or grid.
     */
    public DepotStation(String stationId, Position position) {
        super(stationId, "DepotStation", position);
    }
}
