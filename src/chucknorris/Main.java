package chucknorris;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private enum Messages {
        SELECT_OPERATION("Please input operation (encode/decode/exit):\n"),
        INPUT_STRING("Input string:\n"),
        INPUT_ENCODED_STRING("Input encoded string:\n"),
        ENCODED_STRING("Encoded string:\n"),
        DECODED_STRING("Decoded string:\n"),
        INVALID_OPERATION("There is no '%s' operation\n"),
        NOT_VALID("Encoded string is not valid.\n"),
        ON_EXIT("Bye!\n");

        private final String text;

        Messages(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static String getInput(Messages message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static void printOutput(String... messages) {
        for (String message : messages) {
            System.out.print(message);
        }
    }

    private static void runMenu() {
        loop:
        while (true) {
            String operation = getInput(Messages.SELECT_OPERATION);
            switch (operation) {
                case "exit" -> {
                    printOutput(Messages.ON_EXIT.toString());
                    break loop;
                }
                case "encode" -> {
                    String message = getInput(Messages.INPUT_STRING);
                    printOutput(Messages.ENCODED_STRING.toString(), ChuckNorrisCipher.encode(message), "\n\n");
                }
                case "decode" -> {
                    String message = getInput(Messages.INPUT_ENCODED_STRING);
                    try {
                        printOutput(Messages.DECODED_STRING.toString(), ChuckNorrisCipher.decode(message), "\n\n");
                    } catch (IllegalArgumentException e) {
                        printOutput(Messages.NOT_VALID.toString(), "\n");
                    }
                }
                default -> printOutput(String.format(Messages.INVALID_OPERATION.toString(), operation), "\n");
            }
        }
    }

    public static void main(String[] args) {
        runMenu();
    }
}

class ChuckNorrisCipher {
    /**
     * CNC - ChuckNorrisCipher
     */

    private final static int BINARY_SIZE = 7;

    public static String encode(String message) {
        return encodeBinary(getBinaryFromString(message));
    }

    public static String decode(String message) throws IllegalArgumentException {
        if (isEncoded(message)) {
            List<String> cncBlocks = getCNCBlocksToDecode(message);
            if (isValidBlocks(cncBlocks)) {
                String binary = decodeCNCBlocksToBinary(cncBlocks);
                if (isValidBinary(binary)) {
                    return getStringFromBinary(binary);
                }
            }
        }

        throw new IllegalArgumentException("Invalid code to decode.");
    }

    /**
     * Validate message private functions
     */

    private static boolean isEncoded(String s) {
        return s.matches("[0, ]+") && s.startsWith("0") && s.endsWith("0") && countSymbol(s, ' ') % 2 != 0;
    }

    private static boolean isValidBlocks(List<String> blocks) {
        for (String block : blocks) {
            if (!isValidBlock(block)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidBlock(String block) {
        return (block.startsWith("0 ") || block.startsWith("00 ")) && block.endsWith("0") && countSymbol(block, ' ') == 1;
    }

    private static boolean isValidBinary(String binary) {
        return countSymbol(binary, '1') + countSymbol(binary, '0') == binary.length()
               && binary.length() % BINARY_SIZE == 0;
    }

    /**
     * Decode private functions
     */

    private static List<String> getCNCBlocksToDecode(String encodedMessage) {
        // Split encoded message into CNC blocks
        List<String> cncBlocks = new ArrayList<>();
        int beginIndex = 0;
        for (int i = 0, counter = 0; i < encodedMessage.length(); i++) {
            if (encodedMessage.charAt(i) == ' ') {
                counter++;
            }
            if (counter == 2) {
                cncBlocks.add(encodedMessage.substring(beginIndex, i));
                counter = 0;
                beginIndex = i + 1;
            }
        }
        cncBlocks.add(encodedMessage.substring(beginIndex));
        return cncBlocks;
    }

    private static String decodeCNCBlocksToBinary(List<String> cncBlocks) {
        // Decode each group with Chuck Norris Unary Code method
        StringBuilder binary = new StringBuilder();
        for (String block : cncBlocks) {
            binary.append(decodeCNC(block));
        }
        return binary.toString();
    }

    private static String decodeCNC(String cncBlock) {
        return switch (cncBlock.indexOf(' ')) {
            case 1 -> fillString('1', cncBlock.length() - 2);
            case 2 -> fillString('0', cncBlock.length() - 3);
            //default -> throw new RuntimeException("Invalid index: " + block.indexOf(' '));
            default -> ""; // ?WTF?
        };
    }

    private static String getStringFromBinary(String binary) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binary.toCharArray().length; i += BINARY_SIZE) {
            char ch = getCharFromBinary(binary.substring(i, Math.min(i + BINARY_SIZE, binary.toCharArray().length)));
            result.append(ch);
        }
        return result.toString();
    }

    private static char getCharFromBinary(String binary) {
        return (char) Integer.parseInt(binary, 2);
    }

    private static String fillString(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }

    private static long countSymbol(String s, char symbol) {
        return s.chars().filter(ch -> ch == symbol).count();
    }

    /**
     * Encode private functions
     */

    private static List<String> getBinaryBlocksToEncode(String binary) {
        // Split binary into binary blocks, ex: 111 or 00
        List<String> binaryBlocks = new ArrayList<>();
        int beginIndex = 0;
        for (int i = 0; i < binary.length() - 1; i++) {
            if (binary.charAt(i) != binary.charAt(i + 1)) {
                binaryBlocks.add(binary.substring(beginIndex, i + 1));
                beginIndex = i + 1;
            }
        }
        binaryBlocks.add(binary.substring(beginIndex));
        return binaryBlocks;
    }

    private static String getBinaryFromString(String line) {
        StringBuilder binary = new StringBuilder();
        for (char ch : line.toCharArray()) {
            binary.append(String.format("%0" + BINARY_SIZE + "d", Integer.valueOf(Integer.toBinaryString(ch))));
        }
        return binary.toString();
    }

    private static String encodeBinary(String binary) {
        // Encode each binary block with Chuck Norris Unary Code Algorithm
        // 0 0 is 1, 0 00 is 11, 00 0 is 0, 00 000 is 000;
        StringBuilder encoded = new StringBuilder();
        List<String> binaryBlocks = getBinaryBlocksToEncode(binary);
        for (String binaryBlock : binaryBlocks) {
            encoded.append(String.format((binaryBlock.startsWith("1") ? "0 " : "00 ") + fillString('0', binaryBlock.length())));
            encoded.append(" ");
        }
        if (!encoded.isEmpty()) encoded.setLength(encoded.length() - 1); // Trim StringBuilder
        return encoded.toString();
    }
}