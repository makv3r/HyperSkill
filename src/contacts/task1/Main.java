package contacts.task1;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Phonebook phonebook = new Phonebook();
        phonebook.addContact();
    }
}

class Contacts {
    String name;
    String surname;
    String phonenumber;

    public Contacts(String name, String surname, String phonenumber) {
        this.name = name;
        this.surname = surname;
        this.phonenumber = phonenumber;
    }
}

class Phonebook {
    final static Scanner scanner = new Scanner(System.in);
    List<Contacts> contacts = new ArrayList<>();

    public void addContact() {
        System.out.println("Enter the name of the person:");
        String name = scanner.next();
        System.out.println("Enter the surname of the person:");
        String surname = scanner.next();
        System.out.println("Enter the number:");
        String phonenumber = scanner.next();

        contacts.add(new Contacts(name, surname, phonenumber));

        System.out.println("A record created!");
        System.out.println("A Phone Book with a single record created!");
    }
}
