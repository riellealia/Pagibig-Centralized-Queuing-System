package ph.gov.pagibig.queue;

/** Receives real-time updates whenever the centralized queue changes. */
public interface QueueObserver {
    void onQueueChanged(QueueSnapshot snapshot);
}

