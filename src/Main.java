import cli.SwitchCLI;
import model.Settings;
import repository.CsvTaskStorage;
import repository.JsonTaskStorage;
import repository.TaskRepository;
import repository.TaskStrategy;
import service.TaskService;

import java.util.Scanner;

import static cli.SwitchCLI.readOption;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome in your todo-app!");
        try (Scanner scanner = new Scanner(System.in)) {
            printMenu();

            int option = readOption(scanner);

            Settings settings = Settings.init();
            TaskStrategy strategy = getStrategy(settings);

            TaskRepository taskRepository = new TaskRepository(strategy);
            TaskService taskService = new TaskService(taskRepository);

            Runtime.getRuntime().addShutdownHook(new Thread(taskService::flush));

            SwitchCLI cli = new SwitchCLI(scanner, taskService, settings);

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

    private static TaskStrategy getStrategy(Settings settings) {
        return switch (settings.getFileType()) {
            case CSV -> new CsvTaskStorage(settings.getSeparator());
            case JSON -> new JsonTaskStorage();
            default -> new JsonTaskStorage();
        };
    }
}