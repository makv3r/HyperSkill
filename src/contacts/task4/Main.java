package contacts.task4;

import java.io.*;
import java.lang.reflect.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        Menu menu = (args.length == 1) ? new Menu(args[0]) : new Menu();
        menu.start();
    }
}

abstract class Contact implements Serializable {
    private static final long serialVersionUID = 0L;

    final Timestamp timestamp;
    private String name;
    private String number;

    public Contact(String name, String number) {
        timestamp = new Timestamp();
        this.name = (name != null) ? name : "";
        this.number = (number != null) ? isValidNumber(number) ? number : "" : "";
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

    abstract String getRecordName();

    @Override
    public String toString() {
        return String.format("Name: %s\n" +
                             "Number: %s\n" +
                             "%s\n",
                name,
                hasNumber() ? number : "[no data]",
                timestamp
        );
    }
}

enum ContactType {
    PERSON("person"),
    ORGANIZATION("organization");

    private final String name;

    ContactType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

class ContactFactory {
    private static final Scanner scanner = new Scanner(System.in);

    public Contact newRecord(ContactType contactType) {
        switch (contactType) {
            case PERSON:
                return new Person(
                        readField("name"),
                        readField("surname"),
                        readField("birthdate"),
                        readField("gender"),
                        readField("number")
                );
            case ORGANIZATION:
                return new Organization(
                        readField("name"),
                        readField("adress"),
                        readField("number")
                );
            default:
                throw new IllegalArgumentException();
        }

    }

    public String readField(String fieldname) {
        System.out.printf("Enter the %s:\n", fieldname);
        String field = scanner.nextLine();
        validateField(fieldname, field);
        return field;
    }

    private void validateField(String fieldname, String field) {
        if ("number".equals(fieldname) && !Contact.isValidNumber(field)) {
            System.out.println("Wrong number format!");
        } else if ("birthdate".equals(fieldname) && !Person.isValidBirthdate(field)) {
            System.out.println("Bad birth date!");
        } else if ("gender".equals(fieldname) && !Person.isValidGender(field)) {
            System.out.println("Bad gender!");
        }
    }
}

class Timestamp implements Serializable {
    private static final long serialVersionUID = 0L;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String created;
    private String edited;

    Timestamp() {
        created = dtf.format(LocalDateTime.now());
        edited = created;
    }

    public void invoke() {
        edited = dtf.format(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return String.format("Time created: %s\n" +
                             "Time last edit: %s\n",
                LocalDateTime.parse(created).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.parse(edited).truncatedTo(ChronoUnit.MINUTES)
        );
    }
}

class Person extends Contact {
    private String surname;
    private String birthdate;
    private String gender;

    public Person(String name, String surname, String birthdate, String gender, String number) {
        super(name, number);
        this.surname = (surname != null) ? surname : "";
        this.birthdate = (birthdate != null) ? isValidBirthdate(birthdate) ? birthdate : "" : "";
        this.gender = (gender != null) ? isValidGender(gender) ? gender : "" : "";
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
        this.birthdate = isValidGender(birthdate) ? birthdate : "";
    }

    public boolean hasBirthdate() {
        return !"".equals(birthdate);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        super.timestamp.invoke();
        this.gender = isValidGender(gender) ? gender : "";
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

    public static boolean isValidGender(String gender) {
        return "M".equals(gender) || "F".equals(gender);
    }

    @Override
    public String toString() {
        return String.format("Name: %s\n" +
                             "Surname: %s\n" +
                             "Birth date: %s\n" +
                             "Gender: %s\n" +
                             "Number: %s\n" +
                             "%s",
                super.getName(),
                surname,
                hasBirthdate() ? birthdate : "[no data]",
                hasGender() ? gender : "[no data]",
                hasNumber() ? super.getNumber() : "[no data]",
                super.timestamp
        );
    }

    @Override
    public String getRecordName() {
        return super.getName() + " " + surname;
    }

}

class Organization extends Contact {
    private String address;

    public Organization(String name, String address, String number) {
        super(name, number);
        this.address = (address != null) ? address : "";
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
                             "%s",
                super.getName(),
                address,
                hasNumber() ? super.getNumber() : "[no data]",
                super.timestamp
        );
    }

    @Override
    public String getRecordName() {
        return super.getName();
    }
}

interface Action {
    Contact get(int index);

    void add(Contact contact);

    int count();

    String list();

    Phonebook search(String key);

    void delete(Contact contact);

    void save();
}

class Phonebook implements Action, Serializable {
    private static final long serialVersionUID = 0L;

    private final ArrayList<Contact> list = new ArrayList<>();
    private final String filename;

    public Phonebook() {
        this.filename = "";
    }

    public Phonebook(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public void add(Contact contact) {
        list.add(contact);
        save();
    }

    @Override
    public Contact get(int index) {
        return list.get(index);
    }

    @Override
    public int count() {
        return list.size();
    }

    @Override
    public String list() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(String.format("%d. %s\n", i + 1, list.get(i).getRecordName()));
        }
        return stringBuilder.toString();
    }

    @Override
    public Phonebook search(String regex) {
        Phonebook searchResult = new Phonebook();
        for (Contact contact : list) {
            List<String> values = ReflectionUtils.getFieldsValues(contact);
            for (String value : values) {
                if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(value).find()) {
                    searchResult.add(contact);
                    break;
                }
            }
        }
        return searchResult;
    }

    @Override
    public void delete(Contact contact) {
        list.remove(contact);
        save();
    }

    @Override
    public void save() {
        if (!"".equals(filename)) {
            try {
                SerializationUtils.serialize(this, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Menu {
    private static final Scanner scanner = new Scanner(System.in);
    private final ContactFactory contactFactory = new ContactFactory();
    private Phonebook phonebook;

    enum Option {
        MENU("menu", "Enter action (add, list, search, count, exit):"),
        ADD("add", "Enter the type (person, organization, back):"),
        SEARCH("search", "Enter action ([number], back, again):"),
        RECORD("record", "Enter action (edit, delete, menu):"),
        LIST("list", "Enter action ([number], back):");

        private final String name;
        private final String msg;

        Option(String name, String msg) {
            this.name = name;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s", name, msg);
        }
    }

    Menu() {
        phonebook = new Phonebook();
    }

    Menu(String filename) {
        try {
            File file = new File(filename);
            phonebook = (file.exists() && !file.isDirectory()) ? (Phonebook) SerializationUtils.deserialize(filename) : new Phonebook(filename);
            System.out.printf("open %s\n\n", filename);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        String action;
        while (true) {
            System.out.println(Option.MENU);
            switch (action = scanner.nextLine()) {
                case "add":
                    addRecord();
                    break;
                case "list":
                    if (hasRecords(action)) {
                        System.out.print(phonebook.list());
                        Contact contact = selectRecord();
                        if (contact != null) modifyRecord(contact);
                    }
                    break;
                case "search":
                    if (hasRecords(action)) {
                        Contact contact = searchRecord();
                        if (contact != null) modifyRecord(contact);
                    }
                    break;
                case "count":
                    System.out.printf("The Phone Book has %d records.\n", phonebook.count());
                    break;
                case "exit":
                    phonebook.save();
                    return;
                default:
                    System.out.printf("Action '%s' doesn't exist!\n", action);
                    break;
            }
            System.out.println();
        }
    }

    private void addRecord() {
        Contact contact;
        label:
        while (true) {
            System.out.println(Option.ADD);
            switch (scanner.nextLine()) {
                case "person":
                    contact = contactFactory.newRecord(ContactType.PERSON);
                    break label;
                case "organization":
                    contact = contactFactory.newRecord(ContactType.ORGANIZATION);
                    break label;
                case "back":
                    return;
                default:
                    System.out.println("Wrong type!");
                    break;
            }
        }
        phonebook.add(contact);
        System.out.println("The record added.");
    }

    private Contact selectRecord() {
        while (true) {
            System.out.println();
            System.out.println(Option.LIST);
            String action = scanner.nextLine();
            if ("back".equals(action)) return null;

            int index = parseIndex(action);

            if (index > 0 && index <= phonebook.count()) {
                Contact contact = phonebook.get(index - 1);
                System.out.println(contact);
                return contact;
            } else {
                System.out.println("Wrong index!");
            }
        }
    }

    private void modifyRecord(Contact contact) {
        String action;
        label:
        while (true) {
            System.out.println(Option.RECORD);
            action = scanner.nextLine();
            switch (action) {
                case "edit":
                    editRecord(contact);
                    break label;
                case "delete":
                    removeRecord(contact);
                    break label;
                case "menu":
                    return;
                default:
                    System.out.println("Wrong action!");
            }
        }
    }

    private void editRecord(Contact contact) {
        String fieldname;
        label:
        while (true) {
            if (contact.getClass() == Person.class) {
                System.out.println("Select a field (name, surname, birthdate, gender, number):");
                fieldname = scanner.nextLine();
                switch (fieldname) {
                    case "name":
                        contact.setName(contactFactory.readField(fieldname));
                        break label;
                    case "surname":
                        ((Person) contact).setSurname(contactFactory.readField(fieldname));
                        break label;
                    case "birthdate":
                        ((Person) contact).setBirthdate(contactFactory.readField(fieldname));
                        break label;
                    case "gender":
                        ((Person) contact).setGender(contactFactory.readField(fieldname));
                        break label;
                    case "number":
                        contact.setNumber(contactFactory.readField(fieldname));
                        break label;
                    default:
                        System.out.println("Wrong field!");
                }

            } else if (contact.getClass() == Organization.class) {
                System.out.println("Select a field (name, address, number):");
                fieldname = scanner.nextLine();
                switch (fieldname) {
                    case "name":
                        contact.setName(contactFactory.readField(fieldname));
                        break label;
                    case "address":
                        ((Organization) contact).setAddress(contactFactory.readField(fieldname));
                        break label;
                    case "number":
                        contact.setNumber(contactFactory.readField(fieldname));
                        break label;
                    default:
                        System.out.println("Wrong field!");
                }
            }
        }

        phonebook.save();
        System.out.println("The record updated!");
    }

    private void removeRecord(Contact contact) {
        phonebook.delete(contact);
        System.out.println("The record removed!");
    }

    private Contact searchRecord() {
        while (true) {
            System.out.println("Enter search query:");
            String regex = scanner.nextLine();
            Phonebook searchResult = phonebook.search(regex);
            System.out.printf("Found %d results:\n", searchResult.count());
            System.out.println(searchResult.list());

            while (true) {
                System.out.println(Option.SEARCH);
                String action = scanner.nextLine();

                if (action.equals("back")) return null;
                if (action.equals("again")) break;

                int index = parseIndex(action);
                if (isValidIndex(index, searchResult)) {
                    Contact contact = searchResult.get(index - 1);
                    System.out.println(contact);
                    return contact;
                } else {
                    System.out.println("Wrong index!");
                }
            }
        }
    }

    private int parseIndex(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isValidIndex(int index, Phonebook phonebook) {
        return index > 0 && index <= phonebook.count();
    }

    private boolean hasRecords(String action) {
        if (phonebook.count() == 0) {
            System.out.printf("No records to %s!\n", action);
            return false;
        }
        return true;
    }
}

final class SerializationUtils {

    private SerializationUtils() {
    }

    public static void serialize(Object obj, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }

    public static Object deserialize(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
}

final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<String> getFieldsNames(Class<?> clazz) {
        // A method that returns all private fields from clazz.
        ArrayList<Field> fields = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        Collections.addAll(fields, clazz.getSuperclass().getDeclaredFields());
        Collections.addAll(fields, clazz.getDeclaredFields());

        for (Field field : fields) {
            if (field.getModifiers() == Modifier.PRIVATE) {
                names.add(field.getName());
            }
        }

        return names;
    }

    public static List<String> getFieldsValues(Contact contact) {
        // A method that takes a string representation of all private fields
        // and returns the list of values of this fields.

        List<Field> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();

        Collections.addAll(fields, contact.getClass().getSuperclass().getDeclaredFields());
        Collections.addAll(fields, contact.getClass().getDeclaredFields());

        for (Field field : fields) {
            if (field.getModifiers() == Modifier.PRIVATE) {
                try {
                    field.setAccessible(true);
                    values.add((String) field.get(contact));
                } catch (IllegalAccessException ignore) {
                }
            }
        }

        return values;
    }
}
