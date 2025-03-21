package unsw.loads;

/**
 * Represents a passenger who will board a train and travel to a specific destination station.
 */
public class Passenger {
    private String passengerId;
    private String destination;

    /**
     * Constructs a new Passenger.
     *
     * @param passengerId  Unique identifier for the passenger.
     * @param destination  Station ID where the passenger wants to go.
     */
    public Passenger(String passengerId, String destination) {
        this.passengerId = passengerId;
        this.destination = destination;
    }

    /**
     * Returns the unique ID of the passenger.
     *
     * @return passenger ID.
     */
    public String getPassengerId() {
        return passengerId;
    }

    /**
     * Returns the destination station ID for this passenger.
     *
     * @return destination station ID.
     */
    public String getDestination() {
        return destination;
    }
}
