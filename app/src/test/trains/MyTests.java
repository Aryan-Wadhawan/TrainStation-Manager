package trains;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import unsw.exceptions.InvalidRouteException;
import unsw.trains.TrainsController;

public class MyTests {
    // Write your tests here
    @Test
    public void testCreateTrainAtNonExistentStation() {
        TrainsController controller = new TrainsController();

        assertThrows(IllegalArgumentException.class, () -> {
            controller.createTrain("train1", "PassengerTrain", "invalidStation", List.of("s1", "s2"));
        });
    }

    @Test
    public void testCreateTrainWithInvalidRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 5.0, 5.0);
        controller.createStation("s2", "CargoStation", 10.0, 10.0);

        // No track between s1 and s2 (invalid route)
        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2"));
        });
    }

    @Test
    public void testPassengerTrainCannotHaveCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 1.0, 1.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createStation("s3", "PassengerStation", 20.0, 20.0);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-1", "s3", "s1"); // Creates a cycle

        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2", "s3", "s1"));
        });
    }

    @Test
    public void testBulletTrainCanHaveCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 1.0, 1.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createStation("s3", "PassengerStation", 20.0, 20.0);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-1", "s3", "s1"); // Creates a cycle

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "BulletTrain", "s1", List.of("s1", "s2", "s3", "s1"));
        });
    }

    @Test
    public void testCargoTrainCannotHaveCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 1.0, 1.0);
        controller.createStation("s2", "CargoStation", 10.0, 10.0);
        controller.createStation("s3", "CargoStation", 20.0, 20.0);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-1", "s3", "s1"); // Creates a cycle

        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2", "s3", "s1"));
        });
    }

    @Test
    public void testBulletTrainWithDisconnectedRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 1.0, 1.0);
        controller.createStation("s2", "CentralStation", 10.0, 10.0);
        controller.createStation("s3", "CentralStation", 20.0, 20.0);

        // Only one track, so the route is disconnected
        controller.createTrack("t1-2", "s1", "s2");

        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "BulletTrain", "s1", List.of("s1", "s2", "s3"));
        });
    }

    @Test
    public void testCreateTrainWithSingleStationRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 5.0, 5.0);

        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1"));
        });
    }

    @Test
    public void testTrainCannotBeCreatedAtStationWithoutTrack() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 5.0, 5.0);
        controller.createStation("s2", "CargoStation", 10.0, 10.0);

        // 🚨 No track between s1 and s2, train should NOT be created
        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1"));
        });
    }

}
