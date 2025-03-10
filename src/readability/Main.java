package readability;

import java.io.*;
import java.util.*;
import java.math.*;
import java.nio.file.*;
import java.util.stream.*;

class ARI { // Automated Readability Index

    private enum Messages {
        SELECT_SCORE("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): "),
        INVALID_INPUT("Invalid input! Please enter a valid option."),
        READABILITY_INDEX("%s: %.2f (about %d-year-olds)."),
        AVERAGE_SCORE("This text should be understood in average by %s-year-olds.");

        private final String text;

        Messages(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum ReadabilityTests {
        ARI("Automated Readability Index"),
        FK("Flesch–Kincaid readability tests"),
        SMOG("Simple Measure of Gobbledygook"),
        CL("Coleman–Liau index");

        final String name;

        ReadabilityTests(String name) {
            this.name = name;
        }
    }

    private final static Scanner SCANNER = new Scanner(System.in);
    private final static int[] GRADE_LEVEL = new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 22};

    private final String text;
    public final List<String> wordsList;
    private final long characters;
    private final long words;
    private final long sentences;
    private final long syllables;
    private final long polysyllables;

    private final Map<ReadabilityTests, Double> scoreMap = new HashMap<>();

    public ARI(String filename) throws IOException {
        this.text = Files.readString(Path.of(filename));

        this.wordsList = getWordsList();

        this.characters = countCharacters();
        this.words = countWords();
        this.sentences = countSentences();
        this.syllables = countSyllables();
        this.polysyllables = countPolysyllables();

        scoreMap.put(ReadabilityTests.ARI, getAriScore(characters, words, sentences));
        scoreMap.put(ReadabilityTests.FK, getFkScore(syllables, words, sentences));
        scoreMap.put(ReadabilityTests.SMOG, getSmogScore(polysyllables, sentences));
        scoreMap.put(ReadabilityTests.CL, getClScore(characters, words, sentences));
    }

    public void printReadabilityTests() {
        System.out.println(this);

        while (true) {
            System.out.print(Messages.SELECT_SCORE);
            String input = SCANNER.nextLine();
            System.out.println();

            if ("all".equals(input)) {
                for (ReadabilityTests value : ReadabilityTests.values()) {
                    System.out.println(formatReadabilityTests(value));
                }
                System.out.println(System.lineSeparator() + formatAverageAge());
                break;
            }

            try {
                System.out.println(formatReadabilityTests(ReadabilityTests.valueOf(input)));
                break;
            } catch (IllegalArgumentException ignore) {
                System.out.println(Messages.INVALID_INPUT + System.lineSeparator());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("The text is:%n%s%n%n", text) +
               String.format("Words: %d%n", words) +
               String.format("Sentences: %d%n", sentences) +
               String.format("Characters: %d%n", characters) +
               String.format("Syllables: %d%n", syllables) +
               String.format("Polysyllables: %d%n", polysyllables);
    }

    private boolean isLowerVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    private long countCharacters() {
        return text.replaceAll("[ \n\t]", "").length();
    }

    private long countWords() {
        return wordsList.size();
    }

    private long countSentences() {
        String[] sentenceArray = text.split("[.!?]+");
        int sentences = 0;
        for (String sentence : sentenceArray) {
            if (!sentence.trim().isEmpty() && !sentence.matches("[\n\r]+")) {
                sentences++;
            }
        }
        return sentences;
    }

    private long countSyllablesInWord(String word) {
        long syllables = 0;
        boolean isPreviousCharVowel = false;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (isLowerVowel(c)) {
                if (c == 'e' && i == word.length() - 1 && syllables > 0) {
                    continue; // Ignore silent 'e' at the end
                }
                if (!isPreviousCharVowel) {
                    syllables++;
                }
                isPreviousCharVowel = true;
            } else {
                isPreviousCharVowel = false;
            }
        }

        return Math.max(syllables, 1);
    }

    private long countSyllables() {
        return wordsList.stream()
                .mapToLong(this::countSyllablesInWord)
                .sum();
    }

    private long countPolysyllables() {
        return wordsList.stream()
                .mapToLong(this::countSyllablesInWord)
                .filter(syllables -> syllables > 2)
                .count();
    }

    private List<String> getWordsList() {
        // Return list of words in lowercase format with no punctuation at the end of the word.
        return Arrays.stream(text.split("\\s+"))
                .map(word -> word.replaceAll("[^\\p{L}\\d]+$", ""))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private double getAriScore(long characters, long words, long sentences) {
        if (words == 0 || sentences == 0) return 0; // Prevent division by zero

        return 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
    }

    private double getFkScore(long syllables, long words, long sentences) {
        if (words == 0 || sentences == 0) return 0; // Prevent division by zero

        return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }

    private double getSmogScore(long polysyllables, long sentences) {
        if (sentences == 0) return 0; // Prevent division by zero

        return 1.043 * Math.sqrt(polysyllables * (30.0 / sentences)) + 3.1291;
    }

    private double getClScore(long characters, long words, long sentences) {
        if (words == 0) return 0; // Prevent division by zero

        double s = (double) sentences / words * 100;
        double l = (double) characters / words * 100;
        return 0.0588 * l - 0.296 * s - 15.8;
    }

    private double getAverageAge() {
        return scoreMap.values()
                .stream()
                .mapToDouble(this::getGradeLevel)
                .average()
                .orElse(0);
    }

    private int getGradeLevel(double score) {
        int roundedScore = (int) Math.ceil(score) - 1;
        return roundedScore >= 0 && roundedScore < GRADE_LEVEL.length ? GRADE_LEVEL[roundedScore] : 0;
    }

    private String formatReadabilityTests(ReadabilityTests rt) {
        double score = scoreMap.get(rt);
        return String.format(
                Messages.READABILITY_INDEX.toString(),
                rt.name,
                truncateDouble(score, 2),
                getGradeLevel(score)
        );
    }

    private String formatAverageAge() {
        return String.format(Messages.AVERAGE_SCORE.toString(), truncateDouble(getAverageAge(), 2));
    }

    private double truncateDouble(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(value).setScale(decimalPlaces, RoundingMode.DOWN);
        return bd.doubleValue();
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            ARI ari = new ARI(args[0]);
            ari.printReadabilityTests();
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}