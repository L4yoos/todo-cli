package repository;

import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTaskStorage implements TaskStrategy {
    private static final String FILE_PATH = "tasks.json";

    @Override
    public List<Task> load() {
        return List.copyOf(this.readTasks());
    }

    @Override
    public void save(List<Task> tasks) {
        this.saveAll(tasks);
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
                    Task.fromParts(
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

    //TODO refactor this method to (java.nio NEW API)
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

    private void saveAll(List<Task> tasks) {
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

    // change fw to files.lines() or bufferedWriter
    private void saveContent(String content) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
