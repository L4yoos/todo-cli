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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvTaskStorage implements TaskStrategy {
    private static final String FILE_PATH = "tasks.csv";
    private final String FILE_SEPARATOR;
    //TODO add validate header for CSV.

    public CsvTaskStorage(String separator) {
        this.FILE_SEPARATOR = separator;
    }

    @Override
    public List<Task> load() {
        return this.readTasks();
    }

    @Override
    public void save(List<Task> tasks) {
        this.saveAll(tasks);
    }

    private List<Task> readTasks() {
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines.skip(1)
                    .map(line -> line.split(FILE_SEPARATOR))
                    .map(parts -> Task.fromParts(
                            UUID.fromString(parts[0]),
                            parts[1],
                            TaskType.valueOf(parts[2]),
                            TaskStatus.valueOf(parts[3]),
                            LocalDateTime.parse(parts[4]),
                        LocalDateTime.parse(parts[5])
                    )).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error.", e);
        }
    }

    private void saveAll(List<Task> tasks) {
        StringBuilder sb = new StringBuilder(String.join(FILE_SEPARATOR, "taskId", "name", "type", "status", "createdAt", "updatedAt") + "\n");
        for (Task task : tasks) {
            sb.append(toCsv(task));
            sb.append("\n");
        }
        saveContent(sb.toString());
    }

    private String toCsv(Task task) {
        return String.join(FILE_SEPARATOR,
                task.getTaskId().toString(),
                task.getName(),
                task.getType().name(),
                task.getStatus().name(),
                task.getCreatedAt().toString(),
                task.getUpdatedAt().toString()
        );
    }

    private void saveContent(String content) {
        try {
            Files.write(Path.of(FILE_PATH), content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
