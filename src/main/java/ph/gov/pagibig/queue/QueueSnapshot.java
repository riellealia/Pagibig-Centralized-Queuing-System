package ph.gov.pagibig.queue;

/** Immutable view of the current queue state for monitors and displays. */
public record QueueSnapshot(
        Integer currentServing,
        int lastIssued,
        int waitingCount,
        String servingStation,
        String statusMessage) {
}

