package unsw.loads;

public class Passenger {
    private String passengerId;
    private String destination;

    public Passenger(String passengerId, String destination) {
        this.passengerId = passengerId;
        this.destination = destination;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getDestination() {
        return destination;
    }
}
