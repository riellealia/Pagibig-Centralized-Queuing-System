package ph.gov.pagibig.queue;

import java.util.OptionalInt;

/** A help desk that operates on the shared centralized queue. */
public final class HelpDeskStation {
    private final String stationName;
    private final CentralQueueSystem queueSystem;

    public HelpDeskStation(String stationName, CentralQueueSystem queueSystem) {
        if (stationName == null || stationName.isBlank()) {
            throw new IllegalArgumentException("Station name must not be blank.");
        }
        if (queueSystem == null) {
            throw new IllegalArgumentException("Queue system must not be null.");
        }
        this.stationName = stationName;
        this.queueSystem = queueSystem;
    }

    public String getStationName() {
        return stationName;
    }

    public OptionalInt serveNext() {
        return queueSystem.serveNext(stationName);
    }

    public void resetQueue(int startingNumber) {
        queueSystem.resetQueue(startingNumber, stationName);
    }
}

