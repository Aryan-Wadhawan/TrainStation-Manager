WEEK 1 BLOG

🚆 Blog Entry – Week 1: Building the Foundations of the Train Simulation
Date: (Insert Date)

📌 Week 1 Overview

This week, I worked on designing and implementing the core functionality for the train simulation system using Object-Oriented Programming (OOP) principles. My primary focus was on creating stations, tracks, trains, and validating routes while adhering to the constraints outlined in the specification.

🛠 Key Design Decisions

1️⃣ Managing Entities in TrainsController
The TrainsController class was structured to maintain three core collections:

1. stations (stores all train stations by their unique IDs)
2. tracks (stores all train tracks between stations)
3. trains (stores all created trains)
4. This ensured efficient retrieval and management of each entity.

2️⃣ Using InfoResponse Classes Correctly

1. I had to ensure that InfoResponse objects are NOT stored within any class.
2. Instead, I generate them on demand in functions like getTrainInfo() and 3. getStationInfo().
3. This ensures fresh data retrieval and prevents stale responses.

3️⃣ Route Validation in createTrain()

Initially, I implemented basic route validation, but I soon realized it was incomplete.
I introduced isValidRoute() to check:

1. If all stations are connected by tracks.
2. If PassengerTrains and CargoTrains are strictly linear.
3. If BulletTrains allow both linear and cyclical routes.

4️⃣ Preventing Invalid Train Creations
Implemented checks to:

1. Ensure trains are only created at existing stations.
2. Prevent trains from using disconnected routes.
3. Throw an InvalidRouteException when a train’s route is invalid.

🔍 Challenges Faced

🚨 Issue: getTrainInfo() Returning null
In early testing, getTrainInfo() returned null, even though trains were created successfully.
Debugging revealed that train positions were not being set correctly in createTrain().
Solution: Fetch the station’s position when creating a train and pass it to the train’s constructor.
🚨 Route Validation Not Working Initially
Initially, isValidRoute() only checked for cyclical vs. linear routes but didn’t verify track connections.
This caused some tests to pass when they should have failed.
Solution: I updated isValidRoute() to iterate through the route and ensure tracks exist between consecutive stations.
🧪 Test Cases Implemented

I wrote several edge case tests to verify the correctness of our implementation:

Key Takeaways from Week 1
Understanding the importance of strict validation.
Using InfoResponse correctly to avoid stale data.
Debugging requires step-by-step verification of entity states.
Test-driven development (TDD) helps catch errors early.
