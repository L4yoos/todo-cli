package repository;

import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CsvTaskStorage implements TaskStrategy {
    private static final String FILE_PATH = "tasks.csv";

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
        //TODO add creating tasks.csv if is empty
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("Unexpected Error." + e);
        }

        List<Task> tasks = new ArrayList<>();
        //TODO refactor this method. Do we need BufferedReader, maybe better use Files.readLines()
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                //TODO add validation for header?
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");

                tasks.add(Task.fromParts(
                        UUID.fromString(parts[0]),
                        parts[1],
                        TaskType.valueOf(parts[2]),
                        TaskStatus.valueOf(parts[3]),
                        LocalDateTime.parse(parts[4]),
                        LocalDateTime.parse(parts[5])
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error", e);
        }
        return tasks;
    }

    private void saveAll(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("taskId,name,type,status,createdAt,updatedAt\n");
        for (Task task : tasks) {
            sb.append(task.toCsv());
            sb.append("\n");
        }
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
