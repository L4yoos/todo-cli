package cli;

import exception.TaskNotFoundException;
import exception.WrongTaskTypeException;
import model.Task;
import service.TaskService;

import java.util.List;
import java.util.Scanner;

public class SwitchCLI {

    private final Scanner scanner;
    private final TaskService taskService;

    public SwitchCLI(Scanner scanner, TaskService taskService) {
        this.scanner = scanner;
        this.taskService = taskService;
    }

    public void getOption(int option) {
        try {
            switch (option) {
                case 1 -> createTask();
                case 2 -> displayTasks();
                case 3 -> checkTask();
                case 4 -> updateTask();
                case 5 -> deleteTask();
                //todo add CSV or JSON format file
                case 6 -> System.out.println("Settings: ");
                default -> System.out.println("You need to type from 0 to 6. Type 0 to quit.");
            }
        } catch (WrongTaskTypeException | TaskNotFoundException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public static int readOption(Scanner scanner) {
        try {
            System.out.print("Type your option: ");
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number from menu.");
            return -1;
        }
    }

    private void createTask() {
        System.out.print("Type name of task: ");
        String name = scanner.nextLine();
        System.out.print("Type type of task [DAILY, ROUTINE, DISPOSABLE]: ");
        String type = scanner.nextLine();

        System.out.println(taskService.createTask(name, type));
    }

    private void displayTasks() {
        List<Task> tasks = taskService.displayTasks();
        if (tasks.isEmpty()) System.out.println("No tasks found.");
        tasks.forEach(task -> {
            System.out.println("TaskId: " + task.getTaskId());
            System.out.println("Name: " + task.getName());
            System.out.println("Type: " + task.getType());
            System.out.println("Status: " + task.getStatus());
            System.out.println("createdAt: " + task.getCreatedAt());
            System.out.println("updatedAt: " + task.getUpdatedAt());
            System.out.println("---");
        });
    }

    private void checkTask() {
        System.out.println("Check your Task: ");
        taskService.checkTask(getLastFourChars());
    }

    private String getLastFourChars() {
        System.out.print("Type last 4 chars from taskId: ");
        return scanner.nextLine();
    }

    private void updateTask() {
        System.out.println("Update your Task: ");
        String lastFourChars = getLastFourChars();

        System.out.println("1. Update name of Task");
        System.out.println("2. Update type of Task");
        System.out.print("Type a number of option: ");
        int optionUpdate = readOption(scanner);
        switch (optionUpdate) {
            case 1 -> {
                System.out.print("Type a new name: ");
                String newName = scanner.nextLine();
                taskService.updateTask(lastFourChars, optionUpdate, newName);
            }
            case 2 -> {
                System.out.print("Type type of task [DAILY, ROUTINE, DISPOSABLE]: ");
                String newType = scanner.nextLine();
                taskService.updateTask(lastFourChars, optionUpdate, newType);
            }
            default -> System.out.println("Invalid update option. Aborting update.");
        }
    }

    private void deleteTask() {
        System.out.println("Delete your Task: ");
        taskService.deleteTask(getLastFourChars());
    }
}
