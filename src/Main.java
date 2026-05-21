import service.TaskService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome in your todo-app!");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Create new task");
        System.out.println("2. View list");
        System.out.println("3. Make checked task");
        System.out.println("4. Update task");
        System.out.println("5. Delete task");
        System.out.println("6. Settings");
        System.out.println("0. Quit");

        //todo exception for non-int input
        System.out.print("Type your option: ");
        int option = scanner.nextInt();

        while (option != 0) {
            switch (option) {
                case 1:
                    System.out.println("Create your new Task: ");
                    System.out.print("Type name of task: ");
                    String name = scanner.next();
                    System.out.print("Type type of task [DAILY, ROUTINE, DISPOSABLE]: ");
                    String type = scanner.next();
                    TaskService.createTask(name, type);
                    break;
                case 2:
                    System.out.println("This is your list: ");
                    //todo reader from CSV or JSON
                    break;
                case 3:
                    System.out.println("Check your Task: ");
                    //todo taskService
                    break;
                case 4:
                    System.out.println("Update your Task: ");
                    //todo taskService
                    break;
                case 5:
                    System.out.println("Delete your Task: ");
                    //todo taskService
                    break;
                case 6:
                    System.out.println("Settings: ");
                    break;
                default:
                    break;
            }
            System.out.println("You need to type from 0 to 6. Type 0 to quit.");
            System.out.print("Type your option: ");
            option = scanner.nextInt();
        }

        System.out.println("Goodbye!");
    }
}