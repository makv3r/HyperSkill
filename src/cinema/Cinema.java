package cinema;

import java.util.*;

public class Cinema {
    private enum Message {
        CINEMA_NAME("\nCinema:\n  "),
        ENTER_NUMBER_OF_ROWS("Enter the number of rows:\n"),
        ENTER_NUMBER_OF_SEATS("Enter the number of seats in each row:\n"),
        ENTER_ROW_NUMBER("Enter a row number:\n"),
        ENTER_SEAT_NUMBER("Enter a seat number in that row:\n"),
        TICKET_PRICE("\nTicket price: $%d\n"),
        WRONG_INPUT("\nWrong input!\n"),
        TICKET_ALREADY_PURCHASED("\nThat ticket has already been purchased!\n"),
        MENU("""
                
                1. Show the seats
                2. Buy a ticket
                3. Statistics
                0. Exit
                """),
        STATISTICS("""
                
                Number of purchased tickets: %d
                Percentage: %.2f%%
                Current income: $%d
                Total income: $%d
                """
        );

        private final String msg;

        Message(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int FIRST_HALF_TICKET_PRICE = 10;
    private static final int SECOND_HALF_TICKET_PRICE = 8;
    private static final int PRICE_SWITCHER_LIMIT = 60;

    private static final char EMPTY_SEAT = 'S';
    private static final char TAKEN_SEAT = 'B';

    private final int rows;
    private final int seats;
    private final char[][] room;
    private final int income;

    private int currentIncome;
    private int purchasedTickets;

    Cinema() {
        this.currentIncome = 0;
        this.purchasedTickets = 0;

        while (true) {
            int rows = getIntValue(Message.ENTER_NUMBER_OF_ROWS);
            int seats = getIntValue(Message.ENTER_NUMBER_OF_SEATS);

            if (rows > 0 && seats > 0) {
                this.rows = rows;
                this.seats = seats;

                this.room = new char[this.rows][this.seats];
                for (char[] c : this.room) {
                    Arrays.fill(c, EMPTY_SEAT);
                }

                int income = 0;
                for (int i = 1; i <= rows; i++) {
                    income += (getTicketPrice(i) * seats);
                }
                this.income = income;

                break;
            }
        }
    }

    public void run() {
        loop:
        while (true) {
            switch (getIntValue(Message.MENU)) {
                case 0 -> { // Exit
                    break loop;
                }
                case 1 -> printCinemaRoomSeats();
                case 2 -> buyTicket();
                case 3 -> printStatistics();
                default -> System.out.printf(Message.WRONG_INPUT.toString());
            }
        }
    }

    private int getIntValue(Message message) {
        while (true) {
            try {
                System.out.printf(message.toString());
                return SCANNER.nextInt();
            } catch (InputMismatchException | IllegalArgumentException e) {
                SCANNER.nextLine();
            }
        }
    }

    private void printCinemaRoomSeats() {
        System.out.printf(Message.CINEMA_NAME.toString());
        for (int i = 0; i < seats; i++) {
            System.out.print(i + 1 + " ");
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < seats; j++) {
                System.out.print(room[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void printStatistics() {
        double percentage = (double) (100 * purchasedTickets) / (rows * seats);
        System.out.printf(Message.STATISTICS.toString(), purchasedTickets, percentage, currentIncome, income);
    }

    private void buyTicket() {
        while (purchasedTickets < rows * seats) {
            System.out.println();
            int rowNumber = getIntValue(Message.ENTER_ROW_NUMBER);
            int seatNumber = getIntValue(Message.ENTER_SEAT_NUMBER);

            if (rowNumber <= 0 || rowNumber > rows || seatNumber <= 0 || seatNumber > seats) {
                System.out.printf(Message.WRONG_INPUT.toString());
            } else if (room[rowNumber - 1][seatNumber - 1] != EMPTY_SEAT) {
                System.out.printf(Message.TICKET_ALREADY_PURCHASED.toString());
            } else {
                System.out.printf(Message.TICKET_PRICE.toString(), getTicketPrice(rowNumber));
                room[rowNumber - 1][seatNumber - 1] = TAKEN_SEAT;
                currentIncome += getTicketPrice(rowNumber);
                purchasedTickets++;
                break;
            }
        }
    }

    private int getTicketPrice(int rawNumber) {
        return rows * seats <= PRICE_SWITCHER_LIMIT || (rawNumber <= (rows / 2)) ?
                FIRST_HALF_TICKET_PRICE : SECOND_HALF_TICKET_PRICE;
    }

    public static void main(String[] args) {
        Cinema cinema = new Cinema() {{
            run();
        }};
    }
}
