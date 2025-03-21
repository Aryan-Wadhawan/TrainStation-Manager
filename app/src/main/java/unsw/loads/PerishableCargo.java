package unsw.loads;

/**
 * Represents perishable cargo that can expire after a certain time.
 * Inherits from {@link Cargo} and adds a perishability timer.
 */
public class PerishableCargo extends Cargo {
    private int minutesTillPerish;

    /**
     * Constructs a PerishableCargo item.
     *
     * @param cargoId            Unique identifier for the cargo.
     * @param destination        Station ID where the cargo should be delivered.
     * @param weight             Weight of the cargo in kilograms.
     * @param minutesTillPerish  Time in minutes until the cargo perishes.
     */
    public PerishableCargo(String cargoId, String destination, int weight, int minutesTillPerish) {
        super(cargoId, destination, weight);
        this.minutesTillPerish = minutesTillPerish;
    }

    /**
     * Returns the number of minutes remaining before the cargo perishes.
     *
     * @return time until perish in minutes.
     */
    public int getMinutesTillPerish() {
        return minutesTillPerish;
    }

    /**
     * Decreases the perish timer by a given number of minutes.
     *
     * @param minutes The number of minutes to decrease.
     */
    public void decreaseTime(int minutes) {
        this.minutesTillPerish -= minutes;
    }

    /**
     * Checks whether the cargo has expired (i.e., time remaining is 0 or less).
     *
     * @return true if the cargo is expired, false otherwise.
     */
    public boolean isExpired() {
        return this.minutesTillPerish <= 0;
    }
}
