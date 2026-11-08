package ph.gov.pagibig.queue;

/** Lightweight test runner that uses only the Java standard library. */
public final class CentralQueueSystemTest {
    private CentralQueueSystemTest() {
    }

    public static void main(String[] args) {
        CentralQueueSystem queue = new CentralQueueSystem();
        HelpDeskStation desk1 = new HelpDeskStation("Help Desk 1", queue);
        HelpDeskStation desk2 = new HelpDeskStation("Help Desk 2", queue);
        HelpDeskStation desk3 = new HelpDeskStation("Help Desk 3", queue);

        check(queue.issueQueueNumber() == 1, "First issued number should be 1");
        check(queue.issueQueueNumber() == 2, "Second issued number should be 2");
        check(queue.issueQueueNumber() == 3, "Third issued number should be 3");

        check(desk1.serveNext().orElseThrow() == 1, "Desk 1 should serve number 1");
        check(desk2.serveNext().orElseThrow() == 2, "Desk 2 should serve number 2");
        check(desk3.serveNext().orElseThrow() == 3, "Desk 3 should serve number 3");
        check(desk1.serveNext().isEmpty(), "Queue should be empty");

        queue.issueQueueNumber();
        desk2.resetQueue(100);
        check(queue.getWaitingNumbers().isEmpty(), "Reset should clear old waiting numbers");
        check(queue.issueQueueNumber() == 100, "First number after reset should be 100");

        boolean rejected = false;
        try {
            desk3.resetQueue(0);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "Reset should reject a number below 1");

        System.out.println("All CentralQueueSystem tests passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

