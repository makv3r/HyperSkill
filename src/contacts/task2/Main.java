package contacts.task2;

import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        new Phonebook() {{
            menu();
        }};
    }
}

class Contact {
    private String name;
    private String surname;
    private String number;

    public Contact(String name, String surname, String number) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(surname);
        Objects.requireNonNull(number);
        this.name = name;
        this.surname = surname;
        setNumber(number);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (isValidNumber(number)) {
            this.number = number;
        } else {
            this.number = "";
        }
    }

    public boolean hasNumber() {
        return !"".equals(number);
    }

    private boolean isValidNumber(String number) {
        String regex = "^\\+?(\\(\\w+\\)|\\w+[ -]\\(\\w{2,}\\)|\\w+)([ -]\\w{2,})*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
}

class Phonebook {
    private final static Scanner scanner = new Scanner(System.in);
    private final List<Contact> list = new ArrayList<>();

    public void menu() {
        String action;
        while (true) {
            System.out.println("Enter action (add, remove, edit, count, list, exit):");

            switch (action = scanner.nextLine()) {
                case "add":
                    addContact();
                    break;
                case "count":
                    countContact();
                    break;
                case "remove":
                    if (hasRecords(action)) removeContact();
                    break;
                case "edit":
                    if (hasRecords(action)) editContact();
                    break;
                case "list":
                    if (hasRecords(action)) printList();
                    break;
                case "exit":
                    return;
                default:
                    System.out.printf("Action '%s' doesn't exist!\n", action);
                    break;
            }
        }
    }

    private boolean hasRecords(String action) {
        if (list.isEmpty()) {
            System.out.printf("No records to %s!\n", action);
            return false;
        }
        return true;
    }

    private void addContact() {
        Contact contact = new Contact(readField("name"), readField("surname"), readField("number"));
        if (!contact.hasNumber()) System.out.println("Wrong number format!");

        list.add(contact);
        System.out.println("The record added.");
    }

    private void countContact() {
        System.out.printf("The Phone Book has %s records.\n", list.size());
    }

    private void removeContact() {
        printList();
        int index = readRecordIndex();
        list.remove(index);
        System.out.println("The record removed!");
    }

    private void editContact() {
        printList();
        int index = readRecordIndex();
        Contact contact = list.get(index);

        String fieldname;
        label:
        while (true) {
            System.out.println("Select a field (name, surname, number):");
            fieldname = scanner.nextLine();
            switch (fieldname) {
                case "name":
                    contact.setName(readField(fieldname));
                    break label;
                case "surname":
                    contact.setSurname(readField(fieldname));
                    break label;
                case "number":
                    contact.setNumber(readField(fieldname));
                    if (!contact.hasNumber()) System.out.println("Wrong number format!");
                    break label;
                default:
                    System.out.println("Wrong field!");
                    break;
            }
        }
        System.out.println("The record updated!");
    }

    private void printList() {
        int count = 0;
        for (Contact contact : list) {
            count++;
            System.out.printf("%d. %s %s, %s\n", count, contact.getName(), contact.getSurname(), printNumber(contact));
        }
    }

    private String printNumber(Contact contact) {
        return contact.hasNumber() ? contact.getNumber() : "[no number]";
    }

    private int readRecordIndex() {
        int index;
        while (true) {
            System.out.println("Select a record:");
            index = scanner.nextInt();
            if (index <= 0 || index > list.size()) {
                System.out.println("Invalid record index!");
            } else {
                return index - 1;
            }
        }
    }

    private String readField(String fieldname) {
        System.out.printf("Enter the %s:\n", fieldname);
        return scanner.nextLine();
    }

}