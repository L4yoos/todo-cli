package service;

import model.Task;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskService {

    private static final String FILE_PATH = "tasks.json";

    public static void createTask(String name, String type) {
        Task task = Task.of(name, TaskType.valueOf(type.toUpperCase()));
        File file = new File(FILE_PATH);

        if (file.exists() && file.length() > 0) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                long length = raf.length();
                long pointer = length - 1;

                while (pointer > 0) {
                    raf.seek(pointer);
                    char c = (char) raf.readByte();
                    if (c == '}') {
                        break;
                    }
                    pointer--;
                }

                raf.seek(pointer);
                String newContent = "},\n" + task.toJson() + "]";
                raf.write(newContent.getBytes());

                System.out.println("New task added to your list!");
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            try (FileWriter raf = new FileWriter(file)) {
                raf.write("[\n");
                raf.write(task.toJson());
                raf.write("]");
                System.out.println("File JSON saved!");
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public static void readTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try {
            String content = Files.readString(Path.of(FILE_PATH));
            String regex = "\"taskId\"\\s*:\\s*\"([^\"]+)\"" +
                            ".*?\"name\"\\s*:\\s*\"([^\"]+)\"" +
                            ".*?\"type\"\\s*:\\s*\"([^\"]+)\"" +
                            ".*?\"status\"\\s*:\\s*\"([^\"]+)\"" +
                            ".*?\"createdAt\"\\s*:\\s*\"([^\"]+)\"" +
                            ".*?\"updatedAt\"\\s*:\\s*\"([^\"]+)\"";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                System.out.println("TaskId: " + matcher.group(1));
                System.out.println("Name: " + matcher.group(2));
                System.out.println("Type: " + matcher.group(3));
                System.out.println("Status: " + matcher.group(4));
                System.out.println("createdAt: " + matcher.group(5));
                System.out.println("updatedAt: " + matcher.group(6));
                System.out.println("---");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void deleteTask(String lastFourChars) {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try {
            String content = Files.readString(Path.of(FILE_PATH));
            String regex = "\"taskId\"\\s*:\\s*\"([^\"]*" + lastFourChars + ")\"";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String taskId = matcher.group(1);
                regex = "\\s*\\{\\s*\"taskId\"\\s*:\\s*\"[^\"]*" + taskId + "\".*?\\}(,)?";
                Pattern p = Pattern.compile(regex, Pattern.DOTALL);
                Matcher m = p.matcher(content);

                String result = m.replaceAll("");

                try (FileWriter raf = new FileWriter(file)) {
                    raf.write(result);
                    System.out.println("File JSON saved!");
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}