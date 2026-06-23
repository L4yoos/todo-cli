import repository.TaskRepository;
import service.TaskService;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
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
        String lastFourChars;
        option = readOption(scanner);

        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(taskRepository);

        while (option != 0) {
            switch (option) {
                case 1:
                    System.out.print("Type name of task: ");
                    String name = scanner.nextLine();
                    System.out.print("Type type of task [DAILY, ROUTINE, DISPOSABLE]: ");
                    String type = scanner.nextLine();
                    taskService.createTask(name, type);
                    break;
                case 2:
                    taskService.displayTasks();
                    break;
                case 3:
                    System.out.println("Check your Task: ");
                    System.out.print("Type last 4 chars from taskId: ");
                    lastFourChars = scanner.nextLine();
                    taskService.checkTask(lastFourChars);
                    break;
                case 4:
                    System.out.println("Update your Task: ");
                    System.out.print("Type last 4 chars from taskId: ");
                    lastFourChars = scanner.nextLine();
                    System.out.println("1. Update name of Task");
                    System.out.println("2. Update type of Task");
                    System.out.print("Type a number of option: ");
                    int optionUpdate = readOption(scanner);
                    String newUpdate = "";
                    switch (optionUpdate) {
                        case 1:
                            System.out.print("Type a new name: ");
                            newUpdate = scanner.nextLine();
                            break;
                        case 2:
                            System.out.print("Type type of task [DAILY, ROUTINE, DISPOSABLE]: ");
                            newUpdate = scanner.nextLine();
                            break;
                        default:
                            break;
                    }
                    taskService.updateTask(lastFourChars, optionUpdate, newUpdate);
                    break;
                case 5:
                    System.out.println("Delete your Task: ");
                    System.out.print("Type last 4 chars from taskId: ");
                    lastFourChars = scanner.nextLine();
                    taskService.deleteTask(lastFourChars);
                    break;
                case 6:
                    System.out.println("Settings: ");
                    //todo add CSV or JSON format file
                    break;
                default:
                    System.out.println("You need to type from 0 to 6. Type 0 to quit.");
                    break;
            }
            System.out.print("Type your option: ");
            option = readOption(scanner);
        }

        System.out.println("Goodbye!");
    }

    private static int readOption(Scanner scanner) {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number from menu.");
            return -1;
        }
    }
}