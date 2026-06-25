package repository;

import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskRepository {
    private static final String FILE_PATH = "tasks.json";

    public Task addTask(Task task) {
        List<Task> tasks = this.readTasks();
        tasks.add(task);
        this.saveAll(tasks);
        return task;
    }

    public List<Task> readTasks() {
        String content = this.readContent();

        String regex = "\"taskId\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"name\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"type\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"status\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"createdAt\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"updatedAt\"\\s*:\\s*\"([^\"]+)\"";
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
        List<Task> tasks = new ArrayList<>();
        while (matcher.find()) {
            tasks.add(
                    Task.fromJsonParts(
                            UUID.fromString(matcher.group(1)),
                            matcher.group(2),
                            TaskType.valueOf(matcher.group(3)),
                            TaskStatus.valueOf(matcher.group(4)),
                            LocalDateTime.parse(matcher.group(5)),
                            LocalDateTime.parse(matcher.group(6))
                    )
            );
        }
        return tasks;
    }

    public Optional<Task> findByLastFourChars(String lastFourChars) {
        List<Task> tasks = this.readTasks();
        return tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();
    }

    //todo maybe add cache to memory with classes
    public void save(Task task) {
        List<Task> tasks = this.readTasks();

        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskId().equals(task.getTaskId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            tasks.set(index, task);
        } else {
            tasks.add(task);
        }

        this.saveAll(tasks);
    }

    public void saveAll(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i ++) {
            sb.append(tasks.get(i).toJson());
            if (i < tasks.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("\n]");
        saveContent(sb.toString());
    }

    public void delete(String lastFourChars) {
        List<Task> tasks = this.readTasks();

        findByLastFourChars(lastFourChars).ifPresentOrElse(
                task -> {
                    tasks.remove(task);
                    this.saveAll(tasks);
                },
                () -> System.err.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    private void saveContent(String content) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.");
        }
    }

    private String readContent() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists() || f.length() == 0) {
                throw new FileNotFoundException("File is empty or didn't exists.");
            }
            return Files.readString(Path.of(FILE_PATH));
        } catch (IOException e) {
            System.err.println("Unexpected Error.");
        }
        return "";
    }
}
