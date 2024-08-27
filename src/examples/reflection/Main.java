package examples.reflection;

import java.lang.reflect.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> student = Class.forName("reflection.Student");
        Class<?> superclass = student.getSuperclass();
        Constructor<?>[] declaredConstructors = student.getDeclaredConstructors();
        Constructor<?>[] constructors = student.getConstructors();
        Field[] declaredFields = student.getDeclaredFields();
        Field[] fields = student.getFields();
        Method[] declaredMethods = student.getDeclaredMethods();
        Method[] methods = student.getMethods();

        System.out.println("Superclass " + superclass);

        for (Constructor<?> dc : declaredConstructors) {
            System.out.println("Declared Constructor " + dc.getName());
        }
        for (Constructor<?> c : constructors) {
            System.out.println("Constructor " + c.getName());
        }
        for (Field df : declaredFields) {
            System.out.println("Declared Field " + df.getName());
        }
        for (Field f : fields) {
            System.out.println("Field " + f.getName());
        }
        for (Method dm : declaredMethods) {
            System.out.println("Declared Method " + dm.getName());
        }
        for (Method m : methods) {
            System.out.println("Method " + m.getName());
        }

        Object test = "1".getClass();

        System.out.println(test.getClass().getName());
    }
}
