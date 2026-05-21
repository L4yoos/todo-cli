package service;

import model.Task;
import model.TaskType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                String newContent = "},\n" + task.toJson() + "\n]";
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

//    public static List<Task> readTasks() {
//        List<Task> tasks = new ArrayList<>();
//        File file = new File(FILE_PATH);
//        if (!file.exists() || file.length() == 0) {
//            return tasks;
//        }
//    }
}
