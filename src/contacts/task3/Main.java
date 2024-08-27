package contacts.task3;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        new Phonebook() {{
            menu();
        }};
    }
}

class Timestamp {
    private final static DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public final String created;
    public String edited;

    Timestamp() {
        created = dtf.format(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        edited = created;
    }

    public void invoke() {
        edited = dtf.format(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}

class Contact {
    final Timestamp timestamp;
    private String name;
    private String number;

    public Contact(String name, String number) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(number);
        timestamp = new Timestamp();
        this.name = name;
        this.number = isValidNumber(number) ? number : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        timestamp.invoke();
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        timestamp.invoke();
        this.number = isValidNumber(number) ? number : "";
    }

    public boolean hasNumber() {
        return !"".equals(number);
    }

    public static boolean isValidNumber(String number) {
        String regex = "^\\+?(\\(\\w+\\)|\\w+[ -]\\(\\w{2,}\\)|\\w+)([ -]\\w{2,})*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return String.format("Name: %s\n" +
                             "Number: %s\n" +
                             "Time created: %s\n" +
                             "Time last edit: %s\n",
                name,
                hasNumber() ? number : "[no data]",
                timestamp.created,
                timestamp.edited
        );
    }
}

class Person extends Contact {
    private String surname;
    private String birthdate;
    private String gender;

    public Person(String name, String surname, String birthdate, String gender, String number) {
        super(name, number);
        Objects.requireNonNull(surname);
        Objects.requireNonNull(birthdate);
        Objects.requireNonNull(gender);
        this.surname = surname;
        this.birthdate = isValidBirthdate(birthdate) ? birthdate : "";
        this.gender = isValidGenger(gender) ? gender : "";
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        super.timestamp.invoke();
        this.surname = surname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        super.timestamp.invoke();
        this.birthdate = isValidGenger(birthdate) ? birthdate : "";
    }

    public boolean hasBirthdate() {
        return !"".equals(birthdate);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        super.timestamp.invoke();
        this.gender = isValidGenger(gender) ? gender : "";
    }

    public boolean hasGender() {
        return !"".equals(gender);
    }

    public static boolean isValidBirthdate(String birthdate) {
        try {
            LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidGenger(String gender) {
        return "M".equals(gender) || "F".equals(gender);
    }

    @Override
    public String toString() {
        return String.format("Name: %s\n" +
                             "Surname: %s\n" +
                             "Birth date: %s\n" +
                             "Gender: %s\n" +
                             "Number: %s\n" +
                             "Time created: %s\n" +
                             "Time last edit: %s\n",
                super.getName(),
                surname,
                hasBirthdate() ? birthdate : "[no data]",
                hasGender() ? gender : "[no data]",
                hasNumber() ? super.getNumber() : "[no data]",
                timestamp.created,
                timestamp.edited
        );
    }
}

class Organization extends Contact {
    private String address;

    public Organization(String name, String address, String number) {
        super(name, number);
        Objects.requireNonNull(address);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        super.timestamp.invoke();
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Organization name: %s\n" +
                             "Address: %s\n" +
                             "Number: %s\n" +
                             "Time created: %s\n" +
                             "Time last edit: %s\n",
                super.getName(),
                address,
                hasNumber() ? super.getNumber() : "[no data]",
                timestamp.created,
                timestamp.edited
        );
    }
}

class Phonebook {
    private final static Scanner scanner = new Scanner(System.in);
    private final List<Contact> list = new ArrayList<>();

    public void menu() {
        String action;
        while (true) {
            System.out.println("Enter action (add, remove, edit, count, info, exit):");
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
                case "info":
                    if (hasRecords(action)) printInfo();
                    break;
                case "exit":
                    return;
                default:
                    System.out.printf("Action '%s' doesn't exist!\n", action);
                    break;
            }
            System.out.println();
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
        Contact contact;
        label:
        while (true) {
            System.out.println("Enter the type (person, organization):");
            switch (scanner.nextLine()) {
                case "person":
                    contact = new Person(
                            readField("name"),
                            readField("surname"),
                            readField("birthdate"),
                            readField("gender"),
                            readField("number")
                    );
                    break label;
                case "organization":
                    contact = new Organization(
                            readField("organization"),
                            readField("adress"),
                            readField("number")
                    );
                    break label;
                default:
                    System.out.println("Wrong type!");
                    break;
            }
        }
        list.add(contact);
        System.out.println("The record added.");
    }

    private void countContact() {
        System.out.printf("The Phone Book has %s records.\n", list.size());
    }

    private void removeContact() {
        printList();
        int index = readRecordIndex("Select a record:");
        list.remove(index);
        System.out.println("The record removed!");
        System.out.println();
    }

    private void editContact() {
        printList();
        int index = readRecordIndex("Select a record:");
        Contact contact = list.get(index);

        String fieldname;
        if (contact instanceof Person) {
            PersonLabel:
            while (true) {
                System.out.println("Select a field (name, surname, birthdate, gender, number):");
                fieldname = scanner.nextLine();
                switch (fieldname) {
                    case "name":
                        contact.setName(readField(fieldname));
                        break PersonLabel;
                    case "surname":
                        ((Person) contact).setSurname(readField(fieldname));
                        break PersonLabel;
                    case "birthdate":
                        ((Person) contact).setBirthdate(readField(fieldname));
                        break PersonLabel;
                    case "gender":
                        ((Person) contact).setGender(readField(fieldname));
                        break PersonLabel;
                    case "number":
                        contact.setNumber(readField(fieldname));
                        break PersonLabel;
                    default:
                        System.out.println("Wrong field!");
                        break;
                }
            }
        } else if (contact instanceof Organization) {
            OrganizationLabel:
            while (true) {
                System.out.println("Select a field (organization, address, number):");
                fieldname = scanner.nextLine();
                switch (fieldname) {
                    case "organization":
                        contact.setName(readField(fieldname));
                        break OrganizationLabel;
                    case "address":
                        ((Organization) contact).setAddress(readField(fieldname));
                        break OrganizationLabel;
                    case "number":
                        contact.setNumber(readField(fieldname));
                        break OrganizationLabel;
                    default:
                        System.out.println("Wrong field!");
                        break;
                }
            }
        }

        System.out.println("The record updated!");
    }

    private void printList() {
        int count = 0;
        for (Contact contact : list) {
            count++;
            if (contact instanceof Person) {
                System.out.printf("%d. %s %s\n", count, contact.getName(), ((Person) contact).getSurname());
            } else if (contact instanceof Organization) {
                System.out.printf("%d. %s\n", count, contact.getName());
            }
        }
    }

    private void printInfo() {
        printList();
        int index = readRecordIndex("Enter index to show info:");
        Contact contact = list.get(index);
        System.out.print(contact);
    }

    private int readRecordIndex(String msg) {
        int index;
        while (true) {
            System.out.printf("%s\n", msg);
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ignore) {
                index = -1;
            }
            if (index <= 0 || index > list.size()) {
                System.out.println("Invalid record index!");
            } else {
                return index - 1;
            }
        }
    }

    private String readField(String fieldname) {
        System.out.printf("Enter the %s:\n", fieldname);
        String field = scanner.nextLine();

        if ("number".equals(fieldname) && !Contact.isValidNumber(field)) {
            System.out.println("Wrong number format!");
        } else if ("birthdate".equals(fieldname) && !Person.isValidBirthdate(field)) {
            System.out.println("Bad birth date!");
        } else if ("gender".equals(fieldname) && !Person.isValidGenger(field)) {
            System.out.println("Bad gender!");
        }

        return field;
    }

}
