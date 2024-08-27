package tictactoe;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        new TicTacToe() {{
            startGame();
        }};
    }
}

class TicTacToe {
    private enum GameStates {
        NOT_FINISHED("Game not finished"),
        DRAW("Draw"),
        X_WINS("X wins"),
        O_WINS("O wins"),
        IMPOSSIBLE("Impossible");

        String msg;

        GameStates(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    private final char[][] grid = {
            {'_', '_', '_'},
            {'_', '_', '_'},
            {'_', '_', '_'}
    };

    public void startGame() {
        boolean isXPlayerMove = true;
        while (true) {
            printGrid();
            makeMove(isXPlayerMove ? 'X' : 'O');
            isXPlayerMove = !isXPlayerMove; // Switch Player
            GameStates gameStates = getGameState();
            if (gameStates.equals(GameStates.DRAW) || gameStates.equals(GameStates.X_WINS) || gameStates.equals(GameStates.O_WINS)) {
                printGrid();
                System.out.println(gameStates);
                break;
            }
        }
    }

    private void printGrid() {
        System.out.println("---------");
        for (int i = 0; i < grid.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < grid.length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private void makeMove(char signature) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            try {
                int x = Integer.parseInt(input[0]);
                int y = Integer.parseInt(input[1]);

                if (x < 1 || x > 3 || y < 1 || y > 3) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    continue;
                } else if (grid[x - 1][y - 1] != '_') {
                    System.out.println("This cell is occupied! Choose another one!");
                    continue;
                }
                grid[x - 1][y - 1] = signature;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("You should enter numbers!");
                continue;
            }
            break;
        }
    }

    private GameStates getGameState() {
        int X_count = 0;
        int O_count = 0;
        int __count = 0;
        int X_wins = 0;
        int O_wins = 0;

        for (char[] chars : grid) {
            for (char ch : chars) {
                if (ch == '_') __count++;
                else if (ch == 'X') X_count++;
                else if (ch == 'O') O_count++;
            }
        }
        if (Math.abs(X_count - O_count) >= 2) return GameStates.IMPOSSIBLE;

        for (char[] chars : grid) {
            if (chars[0] == chars[1] && chars[0] == chars[2] && chars[0] != '_') {
                if (chars[0] == 'X') X_wins++;
                else O_wins++;
            }
        }

        for (int i = 0; i < grid.length; i++) {
            if (grid[0][i] == grid[1][i] && grid[0][i] == grid[2][i] && grid[0][i] != '_') {
                if (grid[0][i] == 'X') X_wins++;
                else O_wins++;
            }
        }

        if ((grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2] && grid[0][0] != '_') ||
            (grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0] && grid[0][2] != '_')) {
            if (grid[1][1] == 'X') X_wins++;
            else O_wins++;
        }

        if (X_wins > 1 || O_wins > 1 || (X_wins >= 1 && O_wins >= 1)) {
            return GameStates.IMPOSSIBLE;
        } else if (X_wins == 1) {
            return GameStates.X_WINS;
        } else if (O_wins == 1) {
            return GameStates.O_WINS;
        } else if (__count == 0) {
            return GameStates.DRAW;
        } else {
            return GameStates.NOT_FINISHED;
        }
    }
}