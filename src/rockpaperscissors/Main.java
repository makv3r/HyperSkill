package rockpaperscissors;

import java.io.*;
import java.util.*;
import java.nio.file.*;

class RockPaperScissors {
    private static final String FILENAME = "rating.txt";
    private static final Scanner SCANNER = new Scanner(System.in);

    private enum Message {
        ENTER_NAME("Enter your name: "),
        HELLO("Hello, %s\n"),
        LETS_START("Okay, let's start"),
        RATING("Your rating: %d\n"),
        COMMAND_EXIT("!exit"),
        COMMAND_RATING("!rating"),
        BYE_MSG("Bye!"),
        INVALID_INPUT("Invalid input");

        private final String text;

        Message(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum GameResult {
        Loss("Sorry, but the computer chose %s", 0),
        Draw("There is a draw (%s)", 50),
        Win("Well done. The computer chose %s and failed", 100);

        private final String text;
        private final int points;

        GameResult(String text, int points) {
            this.text = text;
            this.points = points;
        }

        public String format(Shape shape) {
            return String.format(text, shape);
        }

        public int getPoints() {
            return points;
        }
    }

    private static class Shape {
        private final String name;

        Shape(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Shape shape)) return false;

            return Objects.equals(name, shape.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    private static final List<Shape> defaultShapes = List.of(
            new Shape("rock"),
            new Shape("paper"),
            new Shape("scissors")
    );

    private final List<Shape> shapes;
    private final Map<Shape, List<Shape>> shapesMap;

    private int rating;

    RockPaperScissors() {
        String name = introduceYourself();
        rating = loadRating(name);
        shapes = getShapes();
        shapesMap = buildShapesMap();
    }

    private String introduceYourself() {
        System.out.print(Message.ENTER_NAME);
        String name = SCANNER.nextLine();
        System.out.printf(Message.HELLO.toString(), name);
        return name;
    }

    private int loadRating(String playerName) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(FILENAME))) {
            return reader.lines()
                    .map(line -> line.split(" "))
                    .filter(parts -> parts[0].equals(playerName))
                    .mapToInt(parts -> Integer.parseInt(parts[1]))
                    .findFirst()
                    .orElse(0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    private List<Shape> getShapes() {
        String input = SCANNER.nextLine();

        return input.isEmpty() ? defaultShapes :
                Arrays.stream(input.split(","))
                        .map(Shape::new)
                        .toList();
    }

    private Map<Shape, List<Shape>> buildShapesMap() {
        Map<Shape, List<Shape>> map = new HashMap<>();
        int size = shapes.size();
        int halfSize = size / 2;

        for (int i = 0; i < size; i++) {
            Shape currentShape = shapes.get(i);
            List<Shape> winningShapes = new ArrayList<>();
            for (int j = 1; j <= halfSize; j++) {
                winningShapes.add(shapes.get((i + j) % size));
            }
            map.put(currentShape, winningShapes);
        }

        return map;
    }

    private Shape getRandomShape() {
        Random random = new Random();
        return shapes.get(random.nextInt(shapes.size()));
    }

    private GameResult compareShapes(Shape userShape, Shape computerShape) {
        if (userShape.equals(computerShape)) {
            return GameResult.Draw;
        } else if (shapesMap.get(userShape).contains(computerShape)) {
            return GameResult.Loss;
        } else {
            return GameResult.Win;
        }
    }

    private void ratingUpdate(GameResult gameResult) {
        switch (gameResult) {
            case Win -> rating += GameResult.Win.getPoints();
            case Loss -> rating += GameResult.Loss.getPoints();
            case Draw -> rating += GameResult.Draw.getPoints();
        }
    }

    private Shape userInput() {
        while (true) {
            String input = SCANNER.nextLine();
            if (Message.COMMAND_EXIT.toString().equals(input)) {
                System.out.println(Message.BYE_MSG);
                return null;
            } else if (Message.COMMAND_RATING.toString().equals(input)) {
                System.out.printf(Message.RATING.toString(), rating);
            } else if (shapes.contains(new Shape(input))) {
                return new Shape(input);
            } else {
                System.out.println(Message.INVALID_INPUT);
            }
        }
    }

    public void run() {
        System.out.println(Message.LETS_START);
        while (true) {
            Shape userShape = userInput();
            if (userShape == null) { // User chose to exit
                break;
            }

            Shape computerShape = getRandomShape();
            GameResult gameResult = compareShapes(userShape, computerShape);
            ratingUpdate(gameResult);
            System.out.println(gameResult.format(computerShape));
        }
    }
}

public class Main {
    public static void main(String[] args) {
        RockPaperScissors rockPaperScissors = new RockPaperScissors() {{
            run();
        }};
    }
}