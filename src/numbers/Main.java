package numbers;

import java.util.*;

public class Main {
    public void start() {
        print(Messages.WELCOME.toString());
        print(Messages.INSTRUCTIONS.toString());

        loop:
        while (true) {
            Request request = new Request();
            switch (request.type) {
                case EXIT -> {
                    print(Messages.ON_EXIT.toString());
                    break loop;
                }
                case EMPTY -> print(Messages.INSTRUCTIONS.toString());
                case ERROR -> print(request.errorMessage);
                case ONE_NUMBER -> print(new PropertiesOfNumber(request.number).getAllProperties());
                case TWO_NUMBERS -> print(PropertiesOfNumber.getRangeOfNumbers(request.number, request.amount));
                case TWO_NUMBERS_WITH_PROPERTIES -> print(PropertiesOfNumber.getAmountOfNumbersWithProperties(
                        request.number,
                        request.amount,
                        request.approvedProperties)
                );
            }
        }
    }

    private void print(String message) {
        System.out.printf("%s%s%s", "\n", message, "\n");
    }

    public static void main(String[] args) {
        new Main() {{
            start();
        }};
    }
}

enum Messages {
    WELCOME("Welcome to Amazing Numbers!"),
    ON_EXIT("Goodbye!"),
    REQUEST("Enter a request: > "),
    FIRST_PARAMETER_ERROR("The first parameter should be a natural number or zero.\n"),
    SECOND_PARAMETER_ERROR("The second parameter should be a natural number.\n"),
    PROPERTY_PARAMETER_ERROR("The property [%s] is wrong.\n"),
    PROPERTIES_PARAMETER_ERROR("The properties [%s] are wrong.\n"),
    AVAILABLE_PROPERTIES("Available properties: [%s]\n"),
    INSTRUCTIONS("""
            Supported requests:
            - enter a natural number to know its properties;
            - enter two natural numbers to obtain the properties of the list:
              * the first parameter represents a starting number;
              * the second parameter shows how many consecutive numbers are to be printed;
            - two natural numbers and properties to search for;
            - a property preceded by minus must not be present in numbers;
            - separate the parameters with one space;
            - enter 0 to exit.
            """),
    EXCLUSIVE_PROPERTIES("""
            The request contains mutually exclusive properties: [%s]
            There are no numbers with these properties.
            """);

    private final String text;

    Messages(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

class NumbersAlgorithms {
    static boolean isEven(long number) {
        return number % 2 == 0;
    }

    static boolean isOdd(long number) {
        return number % 2 != 0;
    }

    static boolean isBuzz(long number) {
        return number % 10 == 7 || number % 7 == 0;
    }

    static boolean isDuck(long number) {
        for (; number != 0; number /= 10) {
            if (number % 10 == 0) {
                return true;
            }
        }
        return false;
    }

    static boolean isPalindromic(long number) {
        char[] chars = String.valueOf(number).toCharArray();
        for (int i = 0; i < chars.length / 2; i++) {
            if (chars[i] != chars[chars.length - 1 - i]) return false;
        }
        return true;
    }

    static boolean isGapful(long number) {
        long firstDigit = number;
        while (firstDigit >= 10) {
            firstDigit /= 10;
        }
        long lastDigit = number % 10;
        long divider = (firstDigit % 10) * 10 + lastDigit;
        return number > 99 && number % divider == 0;
    }

    static boolean isSpy(long number) {
        long sum = 0;
        long product = 1;
        for (; number != 0; number /= 10) {
            long digit = number % 10;
            sum += digit;
            product *= digit;
        }
        return sum == product;
    }

    static boolean isSquare(long number) {
        double sqrt = Math.sqrt(number);
        return sqrt - Math.floor(sqrt) == 0;
    }

    static boolean isSunny(long number) {
        return isSquare(number + 1);
    }

    static boolean isJumping(long number) {
        while (number != 0) {
            long digit1 = number % 10;
            number /= 10;
            if (number != 0) {
                long digit2 = number % 10;
                if (Math.abs(digit1 - digit2) != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isHappy(long number) {
        while (number != 1 && number != 4) {
            long sum = 0;
            while (number > 0) {
                sum += (long) Math.pow(number % 10, 2);
                number /= 10;
            }
            number = sum;
        }
        return number == 1;
    }

    static boolean isSad(long number) {
        return !isHappy(number);
    }
}

enum PropertyTypes {
    EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, JUMPING, HAPPY, SAD;

    static boolean hasProperty(String type) {
        for (PropertyTypes propertyType : PropertyTypes.values()) {
            if (propertyType.name().equals(type)) {
                return true;
            }
        }
        return false;
    }

    static String getAllNames() {
        StringBuilder sb = new StringBuilder();
        for (PropertyTypes type : PropertyTypes.values()) {
            sb.append(type.name()).append(", ");
        }
        sb.setLength(sb.length() - 2); // Delete last 2 characters
        return sb.toString();
    }
}

enum PropertyAction {
    ADD, REMOVE;

    @Override
    public String toString() {
        return this == REMOVE ? "-" : "";
    }
}

enum ExcludedTypes {
    PAIR_1(PropertyTypes.EVEN, PropertyTypes.ODD),
    PAIR_2(PropertyTypes.DUCK, PropertyTypes.SPY),
    PAIR_3(PropertyTypes.SUNNY, PropertyTypes.SQUARE),
    PAIR_4(PropertyTypes.HAPPY, PropertyTypes.SAD);

    PropertyTypes excludedTypeOne;
    PropertyTypes excludedTypeTwo;

    ExcludedTypes(PropertyTypes excludedTypeOne, PropertyTypes excludedTypeTwo) {
        this.excludedTypeOne = excludedTypeOne;
        this.excludedTypeTwo = excludedTypeTwo;
    }
}

class PropertyOfRequest {
    PropertyAction propertyAction;
    PropertyTypes propertyTypes;

    PropertyOfRequest(PropertyAction propertyAction, PropertyTypes propertyTypes) {
        this.propertyAction = propertyAction;
        this.propertyTypes = propertyTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyOfRequest propertyOfRequest)) return false;
        return propertyAction == propertyOfRequest.propertyAction && propertyTypes == propertyOfRequest.propertyTypes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyAction, propertyTypes);
    }

    @Override
    public String toString() {
        return propertyAction.toString() + propertyTypes.toString();
    }
}

class PropertiesOfNumber {
    private final long number;
    private final boolean even;
    private final boolean odd;
    private final boolean buzz;
    private final boolean duck;
    private final boolean palindromic;
    private final boolean gapful;
    private final boolean spy;
    private final boolean square;
    private final boolean sunny;
    private final boolean jumping;
    private final boolean happy;
    private final boolean sad;

    PropertiesOfNumber(long number) {
        this.number = number;
        even = NumbersAlgorithms.isEven(number);
        odd = NumbersAlgorithms.isOdd(number);
        buzz = NumbersAlgorithms.isBuzz(number);
        duck = NumbersAlgorithms.isDuck(number);
        palindromic = NumbersAlgorithms.isPalindromic(number);
        gapful = NumbersAlgorithms.isGapful(number);
        spy = NumbersAlgorithms.isSpy(number);
        square = NumbersAlgorithms.isSquare(number);
        sunny = NumbersAlgorithms.isSunny(number);
        jumping = NumbersAlgorithms.isJumping(number);
        happy = NumbersAlgorithms.isHappy(number);
        sad = !happy;
    }

    String getAllProperties() {
        return "Properties of " + number + "\n" +
               "        even: " + even + "\n" +
               "         odd: " + odd + "\n" +
               "        buzz: " + buzz + "\n" +
               "        duck: " + duck + "\n" +
               " palindromic: " + palindromic + "\n" +
               "      gapful: " + gapful + "\n" +
               "         spy: " + spy + "\n" +
               "      square: " + square + "\n" +
               "       sunny: " + sunny + "\n" +
               "     jumping: " + jumping + "\n" +
               "       happy: " + happy + "\n" +
               "         sad: " + sad + "\n";
    }

    String getAllPropertiesInLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("             ").append(number).append(" is");
        if (even) sb.append(" even,");
        if (odd) sb.append(" odd,");
        if (buzz) sb.append(" buzz,");
        if (duck) sb.append(" duck,");
        if (palindromic) sb.append(" palindromic,");
        if (gapful) sb.append(" gapful,");
        if (spy) sb.append(" spy,");
        if (square) sb.append(" square,");
        if (sunny) sb.append(" sunny,");
        if (jumping) sb.append(" jumping,");
        if (happy) sb.append(" happy,");
        if (sad) sb.append(" sad,");
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    static String getRangeOfNumbers(long number, long amount) {
        StringBuilder sb = new StringBuilder();
        for (long i = number; i < number + amount; i++) {
            sb.append(new PropertiesOfNumber(i).getAllPropertiesInLine()).append("\n");
        }
        return sb.toString();
    }

    static String getAmountOfNumbersWithProperties(long number, long amount, Set<PropertyOfRequest> propertyRequestSet) {
        StringBuilder sb = new StringBuilder();
        for (long i = number, j = amount; j != 0; i++) {
            boolean isMagicNumber = true;
            for (PropertyOfRequest propertyOfRequest : propertyRequestSet) {
                boolean isValidProperty = getPropertyOfNumberByType(i, propertyOfRequest.propertyTypes);
                if ((propertyOfRequest.propertyAction == PropertyAction.ADD && !isValidProperty) ||
                    (propertyOfRequest.propertyAction == PropertyAction.REMOVE && isValidProperty)) {
                    isMagicNumber = false;
                }
            }
            if (isMagicNumber) {
                sb.append(new PropertiesOfNumber(i).getAllPropertiesInLine()).append("\n");
                j--;
            }
        }
        return sb.toString();
    }

    static boolean getPropertyOfNumberByType(long number, PropertyTypes propertyType) {
        return switch (propertyType) {
            case EVEN -> NumbersAlgorithms.isEven(number);
            case ODD -> NumbersAlgorithms.isOdd(number);
            case BUZZ -> NumbersAlgorithms.isBuzz(number);
            case DUCK -> NumbersAlgorithms.isDuck(number);
            case PALINDROMIC -> NumbersAlgorithms.isPalindromic(number);
            case GAPFUL -> NumbersAlgorithms.isGapful(number);
            case SPY -> NumbersAlgorithms.isSpy(number);
            case SQUARE -> NumbersAlgorithms.isSquare(number);
            case SUNNY -> NumbersAlgorithms.isSunny(number);
            case JUMPING -> NumbersAlgorithms.isJumping(number);
            case HAPPY -> NumbersAlgorithms.isHappy(number);
            case SAD -> NumbersAlgorithms.isSad(number);
        };
    }
}

class Request {
    enum Types {
        EXIT, EMPTY, ERROR, ONE_NUMBER, TWO_NUMBERS, TWO_NUMBERS_WITH_PROPERTIES
    }

    public final long number;
    public final long amount;
    public final Set<PropertyOfRequest> approvedProperties = new HashSet<>();
    public final String errorMessage;
    public final Types type;

    private final Set<String> invalidProperties = new HashSet<>();
    private final Set<PropertyOfRequest> mutuallyExclusiveProperties = new HashSet<>();

    Request() {
        List<String> input = getInput();
        int requestLength = input.size();
        number = requestLength >= 1 ? parseInputToLong(input.get(0)) : -1;
        amount = requestLength >= 2 ? parseInputToLong(input.get(1)) : -1;
        if (requestLength >= 3) {
            validateRequest(new HashSet<>(input.subList(2, requestLength)));
            if (invalidProperties.isEmpty()) {
                getMutuallyExclusiveProperties();
            }
        }
        errorMessage = getErrorMessage(requestLength);
        type = getRequestType(errorMessage.isEmpty() ? requestLength : -1);
    }

    private void validateRequest(Set<String> uncheckedProperties) {
        for (String propertyRequest : uncheckedProperties) {
            boolean isPositive = !propertyRequest.startsWith("-");
            String propertyType = isPositive ? propertyRequest : propertyRequest.substring(1);
            if (PropertyTypes.hasProperty(propertyType)) {
                approvedProperties.add(new PropertyOfRequest(
                        isPositive ? PropertyAction.ADD : PropertyAction.REMOVE,
                        PropertyTypes.valueOf(propertyType))
                );
            } else {
                invalidProperties.add(propertyRequest);
            }
        }
    }

    private void getMutuallyExclusiveProperties() {
        Set<PropertyTypes> positive = new HashSet<>();
        Set<PropertyTypes> negative = new HashSet<>();
        for (PropertyOfRequest propertyOfRequest : approvedProperties) {
            if (propertyOfRequest.propertyAction == PropertyAction.ADD) {
                positive.add(propertyOfRequest.propertyTypes);
            } else {
                negative.add(propertyOfRequest.propertyTypes);
            }
        }

        for (PropertyTypes propertyTypes : positive) {
            if (negative.contains(propertyTypes)) {
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.REMOVE, propertyTypes));
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.ADD, propertyTypes));
            }
        }

        for (ExcludedTypes pair : ExcludedTypes.values()) {
            if (positive.contains(pair.excludedTypeOne) && positive.contains(pair.excludedTypeTwo)) {
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.ADD, pair.excludedTypeOne));
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.ADD, pair.excludedTypeTwo));
            }
            if (negative.contains(pair.excludedTypeOne) && negative.contains(pair.excludedTypeTwo)) {
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.REMOVE, pair.excludedTypeOne));
                mutuallyExclusiveProperties.add(new PropertyOfRequest(PropertyAction.REMOVE, pair.excludedTypeTwo));
            }
        }
    }

    private String getSetNames(Set<?> set) {
        StringBuilder sb = new StringBuilder();
        for (Object o : set) {
            sb.append(o).append(", ");
        }
        sb.setLength(sb.length() - 2); // Delete last 2 characters
        return sb.toString();
    }

    private String getErrorMessage(int length) {
        boolean isValidNumber = number >= 0; // Natural number or zero.
        boolean isValidCount = amount > 0;   // Natural number.
        boolean isValidProperties = invalidProperties.isEmpty();
        boolean isMutuallyExclusive = mutuallyExclusiveProperties.isEmpty();

        StringBuilder sb = new StringBuilder();

        if (!isValidNumber) {
            sb.append(Messages.FIRST_PARAMETER_ERROR);
        } else if (!isValidCount && length >= 2) {
            sb.append(Messages.SECOND_PARAMETER_ERROR);
        } else if (!isValidProperties && length >= 3) {
            sb.append(String.format(
                    invalidProperties.size() == 1 ? Messages.PROPERTY_PARAMETER_ERROR.toString() : Messages.PROPERTIES_PARAMETER_ERROR.toString(),
                    getSetNames(invalidProperties)
            ));
            sb.append(String.format(Messages.AVAILABLE_PROPERTIES.toString(), PropertyTypes.getAllNames()));
        } else if (!isMutuallyExclusive) {
            sb.append(String.format(Messages.EXCLUSIVE_PROPERTIES.toString(), getSetNames(mutuallyExclusiveProperties)));
        }

        return sb.toString();
    }

    private Types getRequestType(int length) {
        return switch (length) {
            case -1 -> Types.ERROR;
            case 0 -> Types.EMPTY;
            case 1 -> number == 0 ? Types.EXIT : Types.ONE_NUMBER;
            case 2 -> Types.TWO_NUMBERS;
            default -> Types.TWO_NUMBERS_WITH_PROPERTIES;
        };
    }

    private List<String> getInput() {
        Scanner scanner = new Scanner(System.in);
        List<String> list = new ArrayList<>();
        System.out.print(Messages.REQUEST);
        StringTokenizer st = new StringTokenizer(scanner.nextLine(), " ");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken().toUpperCase());
        }
        return list;
    }

    private long parseInputToLong(String input) {
        long number = -1;
        try {
            number = Long.parseLong(input);
        } catch (NumberFormatException ignored) {
        }
        return number;
    }
}
