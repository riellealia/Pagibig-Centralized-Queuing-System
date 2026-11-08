package ph.gov.pagibig.queue;

/** Displays queue changes as soon as they occur. */
public final class OnlineQueueMonitor implements QueueObserver {
    private volatile QueueSnapshot latestSnapshot;

    @Override
    public void onQueueChanged(QueueSnapshot snapshot) {
        latestSnapshot = snapshot;
        displayCurrentStatus();
    }

    public void displayCurrentStatus() {
        QueueSnapshot snapshot = latestSnapshot;
        if (snapshot == null) {
            System.out.println("[ONLINE MONITOR] Waiting for queue information...");
            return;
        }

        String current = snapshot.currentServing() == null
                ? "None"
                : String.valueOf(snapshot.currentServing());

        System.out.println();
        System.out.println("========== ONLINE QUEUE MONITOR ==========");
        System.out.println("Current number : " + current);
        System.out.println("Serving station: " + snapshot.servingStation());
        System.out.println("Last issued    : " + snapshot.lastIssued());
        System.out.println("People waiting : " + snapshot.waitingCount());
        System.out.println("Status         : " + snapshot.statusMessage());
        System.out.println("==========================================");
    }
}

