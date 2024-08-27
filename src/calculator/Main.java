package calculator;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Product> productList = new ArrayList<>() {{
            add(new Product("Bubblegum", 2, 202));
            add(new Product("Toffee", 0.2, 118));
            add(new Product("Ice cream", 5, 2250));
            add(new Product("Milk chocolate", 4, 1680));
            add(new Product("Doughnut", 2.5, 1075));
            add(new Product("Pancake", 3.2, 80));
        }};

        ProductStore productStore = new ProductStore(productList);

        productStore.printPrices();
        productStore.printEarnedAmount();
        productStore.printIncome();
        productStore.inputExpenses();
        productStore.printNetIncome();
    }
}

class Product {
    public final String name;
    public final double price;
    public final double earned;

    Product(String name, double price, double earned) {
        this.name = name;
        this.price = price;
        this.earned = earned;
    }
}

class ProductStore {
    public final List<Product> productList;
    private final double income;
    private double netIncome;
    private double expenses;

    ProductStore(List<Product> productList) {
        this.productList = productList;
        income = calculateIncome();
        netIncome = calculateNetIncome();
    }

    private double calculateIncome() {
        double income = 0;
        for (Product product : productList) {
            income += product.earned;
        }
        return income;
    }

    private double calculateNetIncome() {
        return income - expenses;
    }

    public void inputExpenses() {
        Scanner scanner = new Scanner(System.in);
        double staffExpenses = 0;
        double otherExpenses = 0;

        System.out.println("Staff expenses:");
        staffExpenses = scanner.nextDouble();
        System.out.println("Other expenses:");
        otherExpenses = scanner.nextDouble();

        expenses = staffExpenses + otherExpenses;
        netIncome = calculateNetIncome();
    }

    public void printPrices() {
        System.out.println("Prices:");
        for (Product product : productList) {
            System.out.printf("%s: $%.2f\n", product.name, product.price);
        }
        System.out.println();
    }

    public void printEarnedAmount() {
        System.out.println("Earned amount:");
        for (Product product : productList) {
            System.out.printf("%s: $%.0f\n", product.name, product.earned);
        }
        System.out.println();
    }

    public void printIncome() {
        System.out.printf("Income: $%.0f\n", income);
    }

    public void printNetIncome() {
        System.out.printf("Net income: $%.0f\n", netIncome);
    }
}