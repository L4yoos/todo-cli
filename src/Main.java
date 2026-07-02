import cli.SwitchCLI;
import repository.TaskRepository;
import service.TaskService;

import java.util.Scanner;

import static cli.SwitchCLI.readOption;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome in your todo-app!");
        try (Scanner scanner = new Scanner(System.in)) {
            printMenu();

            int option = readOption(scanner);

            TaskRepository taskRepository = new TaskRepository();
            TaskService taskService = new TaskService(taskRepository);
            SwitchCLI cli = new SwitchCLI(scanner, taskService);

            while (option != 0) {
                cli.getOption(option);
                printMenu();
                option = readOption(scanner);
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("1. Create new task");
        System.out.println("2. View list");
        System.out.println("3. Make checked task");
        System.out.println("4. Update task");
        System.out.println("5. Delete task");
        System.out.println("6. Settings");
        System.out.println("0. Quit");
    }
}