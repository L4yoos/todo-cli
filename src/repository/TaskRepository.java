package repository;

import exception.TaskNotFoundException;
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

    private final List<Task> tasks = this.readTasks();

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public Optional<Task> findByLastFourChars(String lastFourChars) {
        return tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();
    }

    public Task save(Task task) {
        //it's still O(n) maybe change this to Map<UUID,Task>?
        tasks.removeIf(existingTask -> existingTask.getTaskId().equals(task.getTaskId()));
        tasks.add(task);
        return task;
    }

    public void delete(String lastFourChars) {
        Task task = this.findByLastFourChars(lastFourChars)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with: " + lastFourChars));
        tasks.remove(task);
    }

    public void flush() {
        this.saveAll();
    }

    private List<Task> readTasks() {
        List<Task> tasks = new ArrayList<>();
        String content = this.readContent();

        String regex = "\"taskId\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"name\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"type\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"status\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"createdAt\"\\s*:\\s*\"([^\"]+)\"" +
                ".*?\"updatedAt\"\\s*:\\s*\"([^\"]+)\"";
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);
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

    private void saveAll() {
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

    private void saveContent(String content) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }

    private String readContent() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists() || f.length() == 0) {
                return "[]";
            }
            return Files.readString(Path.of(FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
