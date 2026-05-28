package service;

import model.Task;
import model.TaskType;
import repository.TaskRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(String name, String type) {
        Task task = Task.of(name, TaskType.valueOf(type.toUpperCase()));
        taskRepository.addTask(task);
    }

    public void readTasks() throws IOException {
        String content = taskRepository.readContent();

        String regex = "\"taskId\"\\s*:\\s*\"([^\"]+)\"" +
                        ".*?\"name\"\\s*:\\s*\"([^\"]+)\"" +
                        ".*?\"type\"\\s*:\\s*\"([^\"]+)\"" +
                        ".*?\"status\"\\s*:\\s*\"([^\"]+)\"" +
                        ".*?\"createdAt\"\\s*:\\s*\"([^\"]+)\"" +
                        ".*?\"updatedAt\"\\s*:\\s*\"([^\"]+)\"";
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
        while (matcher.find()) {
            System.out.println("TaskId: " + matcher.group(1));
            System.out.println("Name: " + matcher.group(2));
            System.out.println("Type: " + matcher.group(3));
            System.out.println("Status: " + matcher.group(4));
            System.out.println("createdAt: " + matcher.group(5));
            System.out.println("updatedAt: " + matcher.group(6));
            System.out.println("---");
        }
    }

    public void checkTask(String lastFourChars) throws IOException {
        String content = taskRepository.readContent();

        String idRegex = "\"taskId\"\\s*:\\s*\"([^\"]*" + lastFourChars + ")\"";
        Matcher idMatcher = Pattern.compile(idRegex).matcher(content);

        if (idMatcher.find()) {
            String taskId = idMatcher.group(1);
            String taskRegex = "\\s*\\{\\s*\"taskId\"\\s*:\\s*\"[^\"]*" + taskId + "\".*?\\}(,)?";
            Matcher taskMatcher = Pattern.compile(taskRegex, Pattern.DOTALL).matcher(content);

            StringBuilder sb = new StringBuilder();

            if (taskMatcher.find()) {
                if (taskMatcher.group().contains("\"DONE\"")) {
                    System.out.println("This Task is already DONE.");
                    return;
                }
                String updated = taskMatcher.group().replaceFirst("\"PLANNED\"", "\"DONE\"");
                updated = updated.replaceFirst("\"updatedAt\"\\s*:\\s*\"([^\"]+)\"", "\"updatedAt\": \"" + LocalDateTime.now() + "\"");
                taskMatcher.appendReplacement(sb, Matcher.quoteReplacement(updated));

                taskMatcher.appendTail(sb);
                taskRepository.saveContent(sb.toString());
            } else {
                System.out.println("We don't found Task with last four chars: " + lastFourChars);
            }
        }
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) throws IOException {
        String content = taskRepository.readContent();

        String idRegex = "\"taskId\"\\s*:\\s*\"([^\"]*" + lastFourChars + ")\"";
        Matcher idMatcher = Pattern.compile(idRegex).matcher(content);

        if (idMatcher.find()) {
            String taskId = idMatcher.group(1);
            String taskRegex = "\\s*\\{\\s*\"taskId\"\\s*:\\s*\"[^\"]*" + taskId + "\".*?\\}(,)?";
            Matcher taskMatcher = Pattern.compile(taskRegex, Pattern.DOTALL).matcher(content);

            StringBuilder sb = new StringBuilder();

            if (taskMatcher.find()) {
                String taskBlock = taskMatcher.group();
                String updatedBlock = taskBlock;

                switch(updateOption) {
                    case 1:
                        updatedBlock = taskBlock.replaceFirst("(\"name\"\\s*:\\s*\")[^\"]+(\")", "$1" + newUpdate + "$2");
                        updatedBlock = updatedBlock.replaceFirst("\"updatedAt\"\\s*:\\s*\"([^\"]+)\"", "\"updatedAt\": \"" + LocalDateTime.now() + "\"");
                        break;
                    case 2:
                        String typeName = TaskType.valueOf(newUpdate.toUpperCase()).toString();
                        updatedBlock = taskBlock.replaceFirst("(\"type\"\\s*:\\s*\")[^\"]+(\")", "$1" + typeName + "$2");
                        updatedBlock = updatedBlock.replaceFirst("\"updatedAt\"\\s*:\\s*\"([^\"]+)\"", "\"updatedAt\": \"" + LocalDateTime.now() + "\"");
                        break;
                    default:
                        break;
                }

                taskMatcher.appendReplacement(sb, Matcher.quoteReplacement(updatedBlock));
            }

            taskMatcher.appendTail(sb);
            taskRepository.saveContent(sb.toString());
        } else {
            System.out.println("We don't found Task with last four chars: " + lastFourChars);
        }
    }

    public void deleteTask(String lastFourChars) throws IOException {
        String content = taskRepository.readContent();

        String idRegex = "\"taskId\"\\s*:\\s*\"([^\"]*" + lastFourChars + ")\"";
        Matcher idMatcher = Pattern.compile(idRegex).matcher(content);

        if (idMatcher.find()) {
            String taskId = idMatcher.group(1);
            String taskRegex = "\\s*\\{\\s*\"taskId\"\\s*:\\s*\"[^\"]*" + taskId + "\".*?\\}(,)?";
            Matcher taskMatcher = Pattern.compile(taskRegex, Pattern.DOTALL).matcher(content);

            String result = taskMatcher.replaceAll("");

            taskRepository.saveContent(result);
        }
    }
}