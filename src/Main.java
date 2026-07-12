import cli.SwitchCLI;
import model.FileType;
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

            //TODO maybe lets make public static Settings.init?
            Settings settings = new Settings();
            TaskStrategy strategy;
            if (settings.getFileType() == FileType.CSV) {
                strategy = new CsvTaskStorage();
            } else {
                strategy = new JsonTaskStorage();
            }

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
}