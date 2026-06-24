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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskRepository {
    private static final String FILE_PATH = "tasks.json";

    public String readContent() throws IOException {
        try {
            this.checkFileExists();
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return Files.readString(Path.of(FILE_PATH));
    }

    public Task addTask(Task task) throws IOException {
        List<Task> tasks = this.readTasks();
        tasks.add(task);
        this.saveAll(tasks);
        return task;
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

    private void saveContent(String content) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(content);
            fw.flush();
            System.out.println("File JSON saved!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFileExists() throws IOException {
        File f = new File(FILE_PATH);
        if (!f.exists() || f.length() == 0) {
            throw new FileNotFoundException("File is empty or didn't exists.");
        }
    }

    public List<Task> readTasks() throws IOException {
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
}
