package unsw.loads;

public class Cargo {
    private String cargoId;
    private String destination;
    private int weight;

    public Cargo(String cargoId, String destination, int weight) {
        this.cargoId = cargoId;
        this.destination = destination;
        this.weight = weight;
    }

    public String getCargoId() {
        return cargoId;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}
