package unsw.tracks;

import unsw.utils.TrackType;

/**
 * Represents a breakable train track with durability and repair functionality.
 * A breakable track can degrade over time due to train load and can be repaired
 * once broken.
 */
public class BreakableTrack extends Track {
    /**
     * Durability of the track, ranges from 0 (broken) to 10 (fully intact).
     */
    private int durability;

    /**
     * Indicates whether the track is currently broken.
     */
    private boolean isBroken;

    /**
     * Flag to ensure the track remains broken for at least one tick
     * before starting the repair process.
     */
    private boolean shouldStartRepairing;

    /**
     * Constructs a BreakableTrack with initial durability of 10.
     *
     * @param trackId       The unique identifier for the track.
     * @param fromStationId ID of the station where the track starts.
     * @param toStationId   ID of the station where the track ends.
     */
    public BreakableTrack(String trackId, String fromStationId, String toStationId) {
        super(trackId, fromStationId, toStationId, TrackType.UNBROKEN);
        this.durability = 10;
        this.isBroken = false;
        this.shouldStartRepairing = false;
    }

    /**
     * Decreases the track's durability based on the load of a train.
     * If the durability drops to 0 or below, the track is marked as broken.
     *
     * @param trainLoad The weight/load of the train passing over the track.
     */
    public void decreaseDurability(int trainLoad) {
        if (isBroken)
            return; // Already broken, don't decrease further

        int reduction = 1 + (int) Math.ceil(trainLoad / 1000.0);
        durability -= reduction;

        System.out.println("Reducing track durability by: " + reduction + ", new durability: " + durability);

        if (durability <= 0) {
            durability = 0;
            isBroken = true;
            shouldStartRepairing = false; // Ensure repair doesn't start instantly
            setType(TrackType.BROKEN);
            System.out.println("Track " + getTrackId() + " is now BROKEN.");
        }
    }

    /**
     * Repairs the track gradually by increasing its durability by 1 each call.
     * The repair will only start one tick after the track is broken.
     */
    public void repair() {
        if (isBroken && !shouldStartRepairing) {
            shouldStartRepairing = true;
            return; // Track stays broken for this tick
        }

        if (isBroken && shouldStartRepairing) {
            durability += 1;
            System.out.println("Repairing track " + getTrackId() + ", durability now: " + durability);

            if (durability >= 10) {
                durability = 10;
                isBroken = false;
                shouldStartRepairing = false;
                setType(TrackType.UNBROKEN);
                System.out.println("Track " + getTrackId() + " is fully repaired.");
            }
        }
    }

    /**
     * Returns whether the track is currently broken.
     *
     * @return true if the track is broken, false otherwise.
     */
    public boolean isBroken() {
        return isBroken;
    }

    /**
     * Returns the current durability of the track.
     *
     * @return durability value between 0 and 10.
     */
    public int getDurability() {
        return durability;
    }
}
