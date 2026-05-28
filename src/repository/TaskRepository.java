package repository;

import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskRepository {
    private static final String FILE_PATH = "tasks.json";

    public String readContent() throws IOException {
        this.checkFileExists();
        return Files.readString(Path.of(FILE_PATH));
    }

    public void addTask(Task task) {
        File file = readFile();
        if (!file.exists() || file.length() == 0) {
            saveContent("[\n" + task.toJson() + "]");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long pointer = raf.length() - 1;

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
    }

    public void saveContent(String content) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(content);
            fw.flush();
            System.out.println("File JSON saved!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File readFile() {
        return new File(FILE_PATH);
    }

    private void checkFileExists() throws IOException {
        File f = new File(FILE_PATH);
        if (!f.exists() || f.length() == 0) {
            throw new FileNotFoundException("File is empty or didn't exists.");
        }
    }
}
