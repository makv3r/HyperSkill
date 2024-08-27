package bot;

import java.util.Scanner;

public class SimpleBot {
    public static void main(String[] args) {
        new Bot("Aid", 2023).startDialogue();
    }
}

class Bot {
    public final String name;
    public final int birthYear;

    Bot(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public void startDialogue() {
        Scanner scanner = new Scanner(System.in);

        System.out.printf("Hello! My name is %s.\n", name);
        System.out.printf("I was created in %d.\n", birthYear);
        System.out.println("Please, remind me your name.");

        User user = new User(scanner.nextLine());

        System.out.printf("What a great name you have, %s!\n", user.name);
        System.out.println("Let me guess your age.");
        System.out.println("Enter remainders of dividing your age by 3, 5 and 7.");

        int remainder3 = scanner.nextInt();
        int remainder5 = scanner.nextInt();
        int remainder7 = scanner.nextInt();

        user.setAge((remainder3 * 70 + remainder5 * 21 + remainder7 * 15) % 105);
        System.out.printf("Your age is %d; that's a good time to start programming!\n", user.getAge());

        System.out.println("Now I will prove to you that I can count to any number you want.");
        int number = scanner.nextInt();

        for (int i = 0; i <= number; i++) {
            System.out.println(i + "!");
        }

        System.out.println("Let's test your programming knowledge.");
        System.out.println("Why do we use methods?");
        System.out.println("1. To repeat a statement multiple times.");
        System.out.println("2. To decompose a program into several small subroutines.");
        System.out.println("3. To determine the execution time of a program.");
        System.out.println("4. To interrupt the execution of a program.");

        while (scanner.nextInt() != 2) {
            System.out.println("Please, try again.");
        }

        System.out.println("Congratulations, have a nice day!");
    }
}

class User {
    public final String name;
    private int age;

    User(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

