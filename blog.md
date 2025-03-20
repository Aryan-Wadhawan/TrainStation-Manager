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

TASK B UPDATE BLOG
This past week, we focused on refactoring and improving the design of our train system, specifically handling perishable cargo. Initially, perishable cargo was mixed with regular cargo, causing data inconsistency and making it hard to manage expiration logic effectively.

Key Design Improvements

Separation of Concerns
We split regular and perishable cargo into distinct lists in Train and Station classes.HAd a load classs first which would be super for all types of passengers/cargos
Each class now only manages its respective responsibilities, making the code more maintainable.
Efficient Expiry Handling
Perishable cargo decreases time every simulation tick.
Expired cargo is now removed from trains and stations in TrainMovementManager and CargoManager.
Better Encapsulation now.
Train subclasses now manage their own load info instead of relying on Train for all types of cargo. good change my favourite honestly.
By the end of the week, tests were more reliable, expired cargo was correctly removed, and the overall OOP structure was much cleaner.

UPDATES:

# Fixing Train Type-Specific Boarding in Our Train Simulation

In our train system, we initially had a major bug—we were calling both cargo boarding AND UNLOADING functions for every train, both in one function. This led to error in load info.

The issue took a while to track down, especially when I mistakenly onboarded cargo twice for a station. After debugging for a long time, I finally realized that each train type (Passenger, Cargo, Bullet) needed specific handling and shouldnt just code without thinking of every scenario, debug tool is LEGENDARY.

The fix? Conditionally calling methods based on the train type in both moveTrain() and trainArrivesAtStation(). Now, PassengerTrains handle only passengers, CargoTrains only cargo, and BulletTrains handle both. this was a very big over sight from Task B honestly idk how i got throught that. bu i am very ahppy now, most methohds of train are now abstract and handle each and every test case nicely.

This debugging experience taught me a crucial lesson: always check assumptions when calling methods, especially in polymorphic designs!
