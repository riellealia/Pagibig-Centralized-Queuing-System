package ph.gov.pagibig.queue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Owns the single queue shared by all help desk stations.
 * All state-changing operations are thread-safe.
 */
public final class CentralQueueSystem {
    private final Queue<Integer> waitingNumbers = new ArrayDeque<>();
    private final List<QueueObserver> observers = new CopyOnWriteArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private int nextQueueNumber;
    private int lastIssuedNumber;
    private Integer currentServingNumber;
    private String servingStation = "None";
    private String statusMessage = "Queue is ready.";

    public CentralQueueSystem() {
        this(1);
    }

    public CentralQueueSystem(int startingNumber) {
        validateStartingNumber(startingNumber);
        nextQueueNumber = startingNumber;
        lastIssuedNumber = startingNumber - 1;
    }

    public int issueQueueNumber() {
        QueueSnapshot snapshot;
        int issuedNumber;

        lock.lock();
        try {
            issuedNumber = nextQueueNumber++;
            lastIssuedNumber = issuedNumber;
            waitingNumbers.offer(issuedNumber);
            statusMessage = "Queue number " + issuedNumber + " was issued.";
            snapshot = snapshotUnsafe();
        } finally {
            lock.unlock();
        }

        notifyObservers(snapshot);
        return issuedNumber;
    }

    public OptionalInt serveNext(String stationName) {
        requireStationName(stationName);
        QueueSnapshot snapshot;
        Integer next;

        lock.lock();
        try {
            next = waitingNumbers.poll();
            if (next == null) {
                statusMessage = stationName + " requested the next number, but nobody is waiting.";
            } else {
                currentServingNumber = next;
                servingStation = stationName;
                statusMessage = stationName + " is now serving queue number " + next + ".";
            }
            snapshot = snapshotUnsafe();
        } finally {
            lock.unlock();
        }

        notifyObservers(snapshot);
        return next == null ? OptionalInt.empty() : OptionalInt.of(next);
    }

    public void resetQueue(int startingNumber, String stationName) {
        validateStartingNumber(startingNumber);
        requireStationName(stationName);
        QueueSnapshot snapshot;

        lock.lock();
        try {
            waitingNumbers.clear();
            nextQueueNumber = startingNumber;
            lastIssuedNumber = startingNumber - 1;
            currentServingNumber = null;
            servingStation = "None";
            statusMessage = stationName + " reset the queue. The next number will be "
                    + startingNumber + ".";
            snapshot = snapshotUnsafe();
        } finally {
            lock.unlock();
        }

        notifyObservers(snapshot);
    }

    public QueueSnapshot getSnapshot() {
        lock.lock();
        try {
            return snapshotUnsafe();
        } finally {
            lock.unlock();
        }
    }

    public List<Integer> getWaitingNumbers() {
        lock.lock();
        try {
            return List.copyOf(new ArrayList<>(waitingNumbers));
        } finally {
            lock.unlock();
        }
    }

    public void addObserver(QueueObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer must not be null.");
        }
        observers.add(observer);
        observer.onQueueChanged(getSnapshot());
    }

    public void removeObserver(QueueObserver observer) {
        observers.remove(observer);
    }

    private QueueSnapshot snapshotUnsafe() {
        return new QueueSnapshot(
                currentServingNumber,
                lastIssuedNumber,
                waitingNumbers.size(),
                servingStation,
                statusMessage);
    }

    private void notifyObservers(QueueSnapshot snapshot) {
        for (QueueObserver observer : observers) {
            observer.onQueueChanged(snapshot);
        }
    }

    private static void validateStartingNumber(int startingNumber) {
        if (startingNumber < 1) {
            throw new IllegalArgumentException("Starting number must be at least 1.");
        }
    }

    private static void requireStationName(String stationName) {
        if (stationName == null || stationName.isBlank()) {
            throw new IllegalArgumentException("Station name must not be blank.");
        }
    }
}

