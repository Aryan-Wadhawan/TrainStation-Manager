package unsw.loads;

public class PerishableCargo extends Cargo {
    private int minutesTillPerish;

    public PerishableCargo(String cargoId, String destination, int weight, int minutesTillPerish) {
        super(cargoId, destination, weight);
        this.minutesTillPerish = minutesTillPerish;
    }

    public int getMinutesTillPerish() {
        return minutesTillPerish;
    }

    public void decreaseTime(int minutes) {
        this.minutesTillPerish -= minutes;
    }

    public boolean isExpired() {
        return this.minutesTillPerish <= 0;
    }
}
