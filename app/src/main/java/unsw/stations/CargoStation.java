package unsw.stations;

import unsw.utils.Position;

/**
 * A station specifically designed for handling cargo-related operations.
 * Inherits from the generic {@link Station} class and sets the type to "CargoStation".
 */
public class CargoStation extends Station {
    /**
     * Constructs a CargoStation with the given ID and position.
     *
     * @param stationId Unique identifier for the station.
     * @param position  Position of the station on the map/grid.
     */
    public CargoStation(String stationId, Position position) {
        super(stationId, "CargoStation", position);
    }
}
