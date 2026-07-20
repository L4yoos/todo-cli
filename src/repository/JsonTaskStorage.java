package repository;

import model.Task;
import model.TaskStatus;
import model.TaskType;

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

    private String readContent() {
        try {
            Path path = Path.of(FILE_PATH);
            if (!Files.exists(path)) {
                return "[]";
            }
            return Files.readString(path);
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

    private void saveContent(String content) {
        try {
            Files.write(Path.of(FILE_PATH), content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
