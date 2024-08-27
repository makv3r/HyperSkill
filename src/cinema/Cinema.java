package cinema;

import java.util.*;

public class Cinema {
    private enum Message {
        ENTER_NUMBER_OF_ROWS("Enter the number of rows:"),
        ENTER_NUMBER_OF_SEATS("Enter the number of seats in each row:"),
        ENTER_ROW_NUMBER("Enter a row number:"),
        ENTER_SEAT_NUMBER("Enter a seat number in that row:"),
        TICKET_PRICE("Ticket price: $"),
        WRONG_OPTION("Wrong option!"),
        TICKET_ALREADY_PURCHASED("That ticket has already been purchased!"),
        MENU("""
                1. Show the seats
                2. Buy a ticket
                3. Statistics
                0. Exit
                """);

        private final String msg;

        Message(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    static final Scanner scanner = new Scanner(System.in);
    static final int FIRST_HALF_TICKET_PRICE = 10;
    static final int SECOND_HALF_TICKET_PRICE = 8;
    static final int PRICE_SWITCHER_LIMIT = 60;

    private final int rows;
    private final int seats;
    private final char[][] room;

    Cinema() {
        System.out.println("Enter the number of rows:");
        rows = scanner.nextInt();
        System.out.println("Enter the number of seats in each row:");
        seats = scanner.nextInt();
        System.out.println();
        room = new char[rows][seats];
        for (char[] chars : room) {
            Arrays.fill(chars, 'S');
        }
    }

    private int getInput(Message message, int limit) {
        // Fix this
        System.out.println(message);
        String line = scanner.nextLine();
        try {
            int number = Integer.parseInt(line);
        } catch (NumberFormatException ignored) {

        }
        return 0;
    }

    public void openMenu() {
        loop:
        while (true) {
            System.out.println(Message.MENU);
            switch (scanner.nextInt()) {
                case 0 -> {
                    break loop;
                }
                case 1 -> printCinemaRoom();
                case 2 -> buyTicket();
                case 3 -> printStatistics();
                default -> System.out.println("Wrong option!\n");
            }
        }
    }

    private void printCinemaRoom() {
        System.out.println();
        System.out.println("Cinema:");
        System.out.print("  ");
        for (int i = 1; i <= seats; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < seats; j++) {
                System.out.print(room[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printStatistics() {
        int purchasedTickets = 0;
        int currentIncome = 0;
        int totalIncome = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < seats; j++) {
                if (room[i][j] == 'B') {
                    purchasedTickets++;
                    currentIncome += getTicketPrice(i, j);
                }
                totalIncome += getTicketPrice(i, j);
            }
        }

        double percentage = (double) (100 * purchasedTickets) / (rows * seats);

        System.out.println();
        System.out.printf("Number of purchased tickets: %d\n", purchasedTickets);
        System.out.printf("Percentage: %.2f\n", percentage);
        System.out.printf("Current income: $%d\n", currentIncome);
        System.out.printf("Total income: $%d\n", totalIncome);
        System.out.println();
    }

    private void buyTicket() {
        System.out.println();
        System.out.println("Enter a row number:");
        int rowNumber = scanner.nextInt();

        System.out.println("Enter a seat number in that row:");
        int seatNumber = scanner.nextInt();

        System.out.println("Ticket price: $" + getTicketPrice(rowNumber, seatNumber));
        System.out.println();

        if (rowNumber >= 1 && rowNumber <= rows && seatNumber >= 1 && seatNumber <= seats) {

        }

        if (room[rowNumber - 1][seatNumber - 1] == 'S') {
            room[rowNumber - 1][seatNumber - 1] = 'B';
        } else {
            System.out.println("That ticket has already been purchased!");
        }
    }

    private int getTicketPrice(int rawNumber, int seatNumber) {
        int totalSeats = rows * seats;
        if (totalSeats <= PRICE_SWITCHER_LIMIT) {
            return FIRST_HALF_TICKET_PRICE;
        } else {
            int seatCount = (rawNumber - 1) * seats + seatNumber;
            int firstHalfSeatsCount = (rows / 2) * seats;
            return seatCount < firstHalfSeatsCount ? FIRST_HALF_TICKET_PRICE : SECOND_HALF_TICKET_PRICE;
        }
    }

    private boolean isValidInput() {
        return false;
    }

    public static void main(String[] args) {
        new Cinema() {{
            openMenu();
        }};
    }
}
