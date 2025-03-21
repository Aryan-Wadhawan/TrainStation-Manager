package unsw.loads;

/**
 * Represents a unit of regular cargo to be transported by a train.
 * Cargo includes a unique ID, a destination station ID, and a weight in kilograms.
 */
public class Cargo {
    private String cargoId;
    private String destination;
    private int weight;

    /**
     * Constructs a new Cargo object.
     *
     * @param cargoId     Unique identifier for the cargo.
     * @param destination Station ID where the cargo should be delivered.
     * @param weight      Weight of the cargo in kilograms.
     */
    public Cargo(String cargoId, String destination, int weight) {
        this.cargoId = cargoId;
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Returns the unique ID of the cargo.
     *
     * @return cargo ID.
     */
    public String getCargoId() {
        return cargoId;
    }

    /**
     * Returns the destination station ID for this cargo.
     *
     * @return destination station ID.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the weight of the cargo in kilograms.
     *
     * @return cargo weight.
     */
    public int getWeight() {
        return weight;
    }
}
