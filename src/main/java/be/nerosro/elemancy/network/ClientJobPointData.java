package be.nerosro.elemancy.network;

/**
 * Client-side cache for Elemancy-owned Job Points.
 */
public final class ClientJobPointData {

    private static int availableBalance;

    private ClientJobPointData() {
    }

    public static void update(JobPointSyncPayload payload) {
        availableBalance = payload.availableBalance();
    }

    public static int getAvailableBalance() {
        return availableBalance;
    }
}