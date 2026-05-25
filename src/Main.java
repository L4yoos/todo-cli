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

        System.out.print("Type your option: ");
        int option;
        option = readOption();

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
                    TaskService.readTasks();
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
                    System.out.print("Type last 4 chars from taskId: ");
                    String lastFourChars = scanner.next();
                    TaskService.deleteTask(lastFourChars);
                    break;
                case 6:
                    System.out.println("Settings: ");
                    //todo add CSV or JSON format file
                    break;
                default:
                    break;
            }
            System.out.println("You need to type from 0 to 6. Type 0 to quit.");
            System.out.print("Type your option: ");
            option = readOption();
        }

        System.out.println("Goodbye!");
    }

    private static int readOption() {
        Scanner scanner = new Scanner(System.in);
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number from menu.");
            return -1;
        }
    }
}