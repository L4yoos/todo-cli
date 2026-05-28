package service;

import model.Task;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskService {

    private static final String FILE_PATH = "tasks.json";

    public void createTask(String name, String type) {
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
                raf.getFD().sync();

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
                raf.flush();
                System.out.println("File JSON saved!");
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public void readTasks() throws IOException {
        this.checkFileExists();
        String content = Files.readString(Path.of(FILE_PATH));
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
        File file = this.checkFileExists();
        String content = Files.readString(Path.of(FILE_PATH));

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
                try (FileWriter raf = new FileWriter(file)) {
                    raf.write(sb.toString());
                    raf.flush();
                    System.out.println("File JSON saved!");
                }
            } else {
                System.out.println("We don't found Task with last four chars: " + lastFourChars);
            }
        }
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) throws IOException {
        File file = this.checkFileExists();
        String content = Files.readString(Path.of(FILE_PATH));

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
            try (FileWriter raf = new FileWriter(file)) {
                raf.write(sb.toString());
                raf.flush();
                System.out.println("File JSON saved!");
            }
        } else {
            System.out.println("We don't found Task with last four chars: " + lastFourChars);
        }
    }

    public void deleteTask(String lastFourChars) throws IOException {
        File file = this.checkFileExists();
        String content = Files.readString(Path.of(FILE_PATH));

        String idRegex = "\"taskId\"\\s*:\\s*\"([^\"]*" + lastFourChars + ")\"";
        Matcher idMatcher = Pattern.compile(idRegex).matcher(content);

        if (idMatcher.find()) {
            String taskId = idMatcher.group(1);
            String taskRegex = "\\s*\\{\\s*\"taskId\"\\s*:\\s*\"[^\"]*" + taskId + "\".*?\\}(,)?";
            Matcher taskMatcher = Pattern.compile(taskRegex, Pattern.DOTALL).matcher(content);

            String result = taskMatcher.replaceAll("");

            try (FileWriter raf = new FileWriter(file)) {
                raf.write(result);
                raf.flush();
                System.out.println("File JSON saved!");
            }
        }
    }

    private File checkFileExists() throws IOException {
        File f = new File(FILE_PATH);
        if (!f.exists() || f.length() == 0) {
            throw new IOException("File is empty or didn't exists.");
        }
        return f;
    }
}