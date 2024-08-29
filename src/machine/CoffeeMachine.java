package machine;

import java.util.Scanner;

interface Operation {
    void display();

    void buy();

    void fill();

    void take();

    void action();
}

public class CoffeeMachine implements Operation {

    private enum Messages {
        WATER_TO_ADD("Write how many ml of water you want to add:\n"),
        MILK_TO_ADD("Write how many ml of milk you want to add:\n"),
        BEANS_TO_ADD("Write how many grams of coffee beans you want to add:\n"),
        CUPS_TO_ADD("Write how many disposable cups you want to add:\n"),
        SELECT_ACTION("Write action (buy, fill, take):\n"),
        INVALID_ACTION("Invalid action: %s\n"),
        TAKE_ACTION("I gave you $%d\n"),
        SELECT_COFFEE_TYPE("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:\n"),
        INVALID_COFFEE_TYPE("Invalid coffee type\n"),
        SUPPLIES_AMOUNT_ERROR("Not enough supplies for this type of coffee\n");

        private final String text;

        Messages(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum CoffeeTypes {
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6);

        public final int WATER;
        public final int MILK;
        public final int BEANS;
        public final int CUPS;
        public final int PRICE;

        CoffeeTypes(int water, int milk, int beans, int price) {
            this.WATER = water;
            this.MILK = milk;
            this.BEANS = beans;
            this.CUPS = 1;
            this.PRICE = price;
        }
    }

    private final static Scanner SCANNER = new Scanner(System.in);

    private int water;
    private int milk;
    private int beans;
    private int cups;
    private int money;

    CoffeeMachine(int water, int milk, int beans, int cups, int money) {
        this.water = water;
        this.milk = milk;
        this.beans = beans;
        this.cups = cups;
        this.money = money;
    }

    @Override
    public void display() {
        String toDisplay = """
                The coffee machine has:
                %d ml of water
                %d ml of milk
                %d g of coffee beans
                %d disposable cups
                $%d of money
                """;

        System.out.printf(toDisplay, water, milk, beans, cups, money);
        System.out.print("\n");
    }

    @Override
    public void buy() {
        System.out.printf(Messages.SELECT_COFFEE_TYPE.toString());
        int coffeeType = SCANNER.nextInt();
        switch (coffeeType) {
            case 1 -> makeCoffee(CoffeeTypes.ESPRESSO);
            case 2 -> makeCoffee(CoffeeTypes.LATTE);
            case 3 -> makeCoffee(CoffeeTypes.CAPPUCCINO);
            default -> System.out.printf(Messages.INVALID_COFFEE_TYPE.toString());
        }
    }

    @Override
    public void fill() {
        System.out.printf(Messages.WATER_TO_ADD.toString());
        this.water += SCANNER.nextInt();
        System.out.printf(Messages.MILK_TO_ADD.toString());
        this.milk += SCANNER.nextInt();
        System.out.printf(Messages.BEANS_TO_ADD.toString());
        this.beans += SCANNER.nextInt();
        System.out.printf(Messages.CUPS_TO_ADD.toString());
        this.cups += SCANNER.nextInt();
        System.out.print("\n");
    }

    @Override
    public void take() {
        System.out.printf(Messages.TAKE_ACTION.toString(), money, "\n");
        money = 0;
    }

    @Override
    public void action() {
        System.out.printf(Messages.SELECT_ACTION.toString());
        String action = SCANNER.nextLine();

        switch (action) {
            case "buy" -> buy();
            case "fill" -> fill();
            case "take" -> take();
            default -> System.out.printf(Messages.INVALID_ACTION.toString(), action);
        }
    }

    private void makeCoffee(CoffeeTypes coffeeTypes) {
        if (isEnoughSupplies(coffeeTypes)) {
            water -= coffeeTypes.WATER;
            milk -= coffeeTypes.MILK;
            beans -= coffeeTypes.BEANS;
            cups -= coffeeTypes.CUPS;
            money += coffeeTypes.PRICE;
        } else {
            throw new RuntimeException(Messages.SUPPLIES_AMOUNT_ERROR.toString());
        }
        System.out.print("\n");
    }

    private boolean isEnoughSupplies(CoffeeTypes coffeeTypes) {
        return water >= coffeeTypes.WATER && milk >= coffeeTypes.MILK && beans >= coffeeTypes.BEANS && cups >= coffeeTypes.CUPS;
    }

    public static void main(String[] args) {
        CoffeeMachine coffeeMachine = new CoffeeMachine(400, 540, 120, 9, 550);
        coffeeMachine.display();
        coffeeMachine.action();
        coffeeMachine.display();
    }
}

