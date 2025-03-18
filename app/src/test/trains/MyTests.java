package trains;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.LoadInfoResponse;
import unsw.trains.TrainsController;
import unsw.utils.Position;

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

    @Test
    public void testTrainMovementOverMultipleTicks() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createTrack("t1-2", "s1", "s2");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2"));
            controller.createTrain("train2", "CargoTrain", "s1", List.of("s1", "s2"));
            controller.createTrain("train3", "BulletTrain", "s1", List.of("s1", "s2"));
        });

        // Simulate for 3 ticks
        controller.simulate(3);

        // Check each train's expected position after 3 minutes
        assertEquals(controller.getTrainInfo("train1").getPosition(), new Position(4.24, 4.24)); // 2 km/min * 3 min
        assertEquals(controller.getTrainInfo("train2").getPosition(), new Position(6.36, 6.36)); // 3 km/min * 3 min
        assertEquals(controller.getTrainInfo("train3").getPosition(), new Position(10.0, 10.0)); // BulletTrain arrives
    }

    @Test
    public void testTrainReversesAtEndStation() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createTrack("t1-2", "s1", "s2");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2"));
        });

        // Simulate until the train reaches s2
        controller.simulate(8); // 7 ticks = 14.14 km, in 8 ticks, train should reach (10,10)

        // Confirm train is at s2
        assertEquals(controller.getTrainInfo("train1").getPosition(), new Position(10.0, 10.0));
        assertEquals(controller.getTrainInfo("train1").getLocation(), "s2");

        // Simulate one more tick, train should reverse
        controller.simulate(1);
        assertEquals(controller.getTrainInfo("train1").getPosition(), new Position(8.59, 8.59)); // Moves backward
    }

    @Test
    public void testBulletTrainLoopsCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 0.0, 10.0);
        controller.createStation("s3", "CargoStation", 0.0, 20.0);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-1", "s3", "s1"); // Creates a cycle

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "BulletTrain", "s1", List.of("s1", "s2", "s3", "s1"));
        });

        // Simulate enough ticks for the train to complete a full loop
        controller.simulate(8);

        // Ensure it's still moving in the cycle (not reversing)
        assertEquals(controller.getTrainInfo("train1").getLocation(), "s1");
    }

    @Test
    public void testPassengerAndCargoCreation() {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("s1", "PassengerStation", 0.0, 0.0);
        controller.createStation("s2", "CargoStation", 10.0, 10.0);

        // Create passengers and cargo
        assertDoesNotThrow(() -> controller.createPassenger("s1", "s2", "passenger1"));
        assertDoesNotThrow(() -> controller.createCargo("s2", "s1", "cargo1", 500));
    }

    @Test
    public void testTrainLoadsAndUnloadsPassengers() {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("s1", "PassengerStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createTrack("t1-2", "s1", "s2");

        // Create a passenger train
        assertDoesNotThrow(() -> controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2")));

        // Create a passenger going to s2
        controller.createPassenger("s1", "s2", "passenger1");

        // Simulate one tick, passenger should board
        controller.simulate(1);

        // Ensure train has the passenger (checking via getLoads())
        assertEquals(1, controller.getTrainInfo("train1").getLoads().size());
        assertEquals("Passenger", controller.getTrainInfo("train1").getLoads().get(0).getType());

        // Simulate until the train reaches s2
        controller.simulate(7);

        // Ensure passenger gets off (no more loads)
        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    }

    @Test
    public void testPassengerBoardsAndUnloads() {
        TrainsController controller = new TrainsController();

        // Step 1: Create stations
        controller.createStation("s1", "PassengerStation", 0, 0);
        controller.createStation("s2", "PassengerStation", 0, 10);
        controller.createTrack("t1-2", "s1", "s2");

        // Step 2: Create a passenger at s1 going to s2
        controller.createPassenger("s1", "s2", "p1");

        // Step 3: Create a PassengerTrain at s1
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2"));
        });

        // Step 4: Verify passenger is at station s1 before train moves
        assertEquals(List.of(new LoadInfoResponse("p1", "Passenger")), controller.getStationInfo("s1").getLoads());

        // Step 5: Simulate train moving one tick → it should pick up the passenger
        controller.simulate(2);

        // Step 6: Passenger should no longer be at the station
        assertEquals(0, controller.getStationInfo("s1").getLoads().size());

        // Step 7: Train should now have the passenger
        assertEquals(List.of(new LoadInfoResponse("p1", "Passenger")), controller.getTrainInfo("train1").getLoads());

        // Step 8: Simulate train reaching s2
        controller.simulate(7); // Enough ticks to reach s2

        // Step 9: Passenger should be removed from the train
        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());

        // Step 10: Passenger should not appear in s2, as they "disembarked" (deleted)
        assertEquals(0, controller.getStationInfo("s2").getLoads().size());
    }

    // PERISHABLE CAGO TESTS START FROM HERE

    @Test
    public void testPerishableCargoCreation() {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 20);

        // Create perishable cargo at s1 with expiry time 10 minutes
        assertDoesNotThrow(() -> {
            controller.createPerishableCargo("s1", "s2", "perishable1", 500, 10);
        });

        // Ensure perishable cargo is correctly added to station
        assertEquals(List.of(new LoadInfoResponse("perishable1", "PerishableCargo")),
                controller.getStationInfo("s1").getLoads());
    }

    @Test
    public void testTrainLoadsPerishableCargo() {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 15);
        controller.createTrack("t1-2", "s1", "s2");

        // Create perishable cargo with 15 minutes until expiry
        controller.createPerishableCargo("s1", "s2", "perishable1", 500, 100);

        // Create a cargo train
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Ensure perishable cargo is at station before train moves
        assertEquals(List.of(new LoadInfoResponse("perishable1", "PerishableCargo")),
                controller.getStationInfo("s1").getLoads());

        // Simulate one tick - train should pick up perishable cargo
        controller.simulate(1);

        // // Cargo should be on the train now
        assertEquals(0, controller.getStationInfo("s1").getLoads().size());
        assertEquals(List.of(new LoadInfoResponse("perishable1", "PerishableCargo")),
                controller.getTrainInfo("train1").getLoads());

    }

    @Test
    public void testPerishableCargoExpiresBeforeArrival() {
        TrainsController controller = new TrainsController();

        // Step 1: Create stations
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 50);
        controller.createTrack("t1-2", "s1", "s2");

        // Step 2: Create perishable cargo with only 5 minutes before it expires
        controller.createPerishableCargo("s1", "s2", "perishable1", 500, 5);

        // Step 3: Create a cargo train
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Step 4: Verify perishable cargo is at station s1 before train moves
        List<LoadInfoResponse> initialStationCargo = controller.getStationInfo("s1").getLoads();
        assertEquals(1, initialStationCargo.size(), "Expected 1 perishable cargo at station before simulation");
        assertEquals("perishable1", initialStationCargo.get(0).getLoadId());
        assertEquals("PerishableCargo", initialStationCargo.get(0).getType());

        // Step 5: Simulate one tick - train should NOT pick up perishable cargo
        controller.simulate(1);

        // Verify perishable cargo is still at the station (not picked up)
        List<LoadInfoResponse> stationCargoAfterOneTick = controller.getStationInfo("s1").getLoads();
        List<LoadInfoResponse> trainCargoAfterOneTick = controller.getTrainInfo("train1").getLoads();

        assertEquals(1, stationCargoAfterOneTick.size(), "Perishable cargo should still be at station after 1 tick");
        assertEquals(0, trainCargoAfterOneTick.size(), "Train should not have picked up perishable cargo");

        // Step 6: Simulate 10 minutes, perishable cargo should expire and be removed
        controller.simulate(10);

        // Verify perishable cargo is removed from the station after expiry
        List<LoadInfoResponse> finalStationCargo = controller.getStationInfo("s1").getLoads();
        List<LoadInfoResponse> finalTrainCargo = controller.getTrainInfo("train1").getLoads();

        assertEquals(0, finalStationCargo.size(), "Expired perishable cargo should be removed from station");
        assertEquals(0, finalTrainCargo.size(), "Train should still not have the expired cargo");

        // Final check: No perishable cargo should exist in the system
        assertEquals(0, controller.getStationInfo("s2").getLoads().size(),
                "Cargo should never reach s2 since it expired");
    }

    @Test
    public void testPerishableCargoNotLoadedIfTrainCannotReachInTime() {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 100);
        controller.createTrack("t1-2", "s1", "s2");

        // Create perishable cargo with 8 minutes till perish
        controller.createPerishableCargo("s1", "s2", "perishable1", 500, 30);

        // Create a slow cargo train (3km/min)
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Train should **not** pick up perishable cargo as it takes ~34 minutes to reach s2
        controller.simulate(1);

        // Cargo should still be at the station
        assertEquals(1, controller.getStationInfo("s1").getLoads().size());
        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    }

    // @Test
    // public void testBulletTrainCanTransportPerishableCargoFaster() {
    //     TrainsController controller = new TrainsController();

    //     // Create stations
    //     controller.createStation("s1", "CentralStation", 0, 0);
    //     controller.createStation("s2", "CentralStation", 0, 10);
    //     controller.createTrack("t1-2", "s1", "s2");

    //     // Create perishable cargo with 10 minutes before it expires
    //     controller.createCargo("s1", "s2", "c1", 50);

    //     // Create a fast bullet train (5km/min)
    //     assertDoesNotThrow(() -> {
    //         controller.createTrain("train1", "PassengerTrain", "s1", List.of("s1", "s2"));
    //     });

    //     // Train should pick up perishable cargo since it will reach in 10 minutes
    //     controller.simulate(2);
    //     assertEquals(0, controller.getStationInfo("s1").getLoads().size());
    //     assertEquals(1, controller.getTrainInfo("train1").getLoads().size());

    //     // Simulate time for train to reach destination
    //     //controller.simulate(1);

    //     // Ensure cargo is delivered successfully
    //     assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    //     assertEquals(1, controller.getStationInfo("s2").getLoads().size());

    // }
}
