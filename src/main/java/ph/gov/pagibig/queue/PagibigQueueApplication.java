package ph.gov.pagibig.queue;

import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;

/** Console demonstration of the centralized Pag-IBIG queuing system. */
public final class PagibigQueueApplication {
    private PagibigQueueApplication() {
    }

    public static void main(String[] args) {
        CentralQueueSystem queueSystem = new CentralQueueSystem();
        OnlineQueueMonitor onlineMonitor = new OnlineQueueMonitor();
        queueSystem.addObserver(onlineMonitor);

        List<HelpDeskStation> stations = List.of(
                new HelpDeskStation("Help Desk 1", queueSystem),
                new HelpDeskStation("Help Desk 2", queueSystem),
                new HelpDeskStation("Help Desk 3", queueSystem));

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readPositiveInt(scanner, "Choose an option: ");

                switch (choice) {
                    case 1 -> {
                        int number = queueSystem.issueQueueNumber();
                        System.out.println("Your centralized queue number is: " + number);
                    }
                    case 2 -> serveAtStation(scanner, stations);
                    case 3 -> resetAtStation(scanner, stations);
                    case 4 -> onlineMonitor.displayCurrentStatus();
                    case 5 -> System.out.println("Waiting numbers: " + queueSystem.getWaitingNumbers());
                    case 6 -> {
                        running = false;
                        System.out.println("Queuing system closed.");
                    }
                    default -> System.out.println("Invalid option. Please choose 1 to 6.");
                }
            }
        }
    }

    private static void serveAtStation(Scanner scanner, List<HelpDeskStation> stations) {
        HelpDeskStation station = selectStation(scanner, stations);
        OptionalInt number = station.serveNext();
        if (number.isPresent()) {
            System.out.println(station.getStationName() + " called queue number " + number.getAsInt() + ".");
        } else {
            System.out.println("There are no waiting customers.");
        }
    }

    private static void resetAtStation(Scanner scanner, List<HelpDeskStation> stations) {
        HelpDeskStation station = selectStation(scanner, stations);
        int startingNumber = readPositiveInt(scanner, "Enter the new starting queue number: ");
        station.resetQueue(startingNumber);
        System.out.println("Queue reset successfully by " + station.getStationName() + ".");
    }

    private static HelpDeskStation selectStation(Scanner scanner, List<HelpDeskStation> stations) {
        while (true) {
            int stationNumber = readPositiveInt(scanner, "Select help desk station (1-3): ");
            if (stationNumber <= stations.size()) {
                return stations.get(stationNumber - 1);
            }
            System.out.println("Invalid station. Please choose 1, 2, or 3.");
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // A friendly validation message is printed below.
            }
            System.out.println("Please enter a positive whole number.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("PAG-IBIG CENTRALIZED QUEUING SYSTEM");
        System.out.println("1. Get a queue number");
        System.out.println("2. Serve next customer");
        System.out.println("3. Reset queue number");
        System.out.println("4. View online monitor");
        System.out.println("5. View waiting numbers");
        System.out.println("6. Exit");
    }
}

