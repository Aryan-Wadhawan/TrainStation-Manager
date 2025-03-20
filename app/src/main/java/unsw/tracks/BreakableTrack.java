package unsw.tracks;

import unsw.utils.TrackType;

public class BreakableTrack extends Track {
    private int durability;
    private boolean isBroken;
    private boolean shouldStartRepairing; // New flag to delay repair start

    public BreakableTrack(String trackId, String fromStationId, String toStationId) {
        super(trackId, fromStationId, toStationId, TrackType.UNBROKEN);
        this.durability = 10;
        this.isBroken = false;
        this.shouldStartRepairing = false;
    }

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

    public void repair() {
        // ✅ Ensure the track stays BROKEN for at least one tick before repairing
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

    public boolean isBroken() {
        return isBroken;
    }

    public int getDurability() {
        return durability;
    }
}
