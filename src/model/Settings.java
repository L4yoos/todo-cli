package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Settings {
    private final static String FILE_PATH = "settings.txt";
    private FileType fileType;

    Settings() {
        this.fileType = this.loadType();
    }

    public static Settings init() {
        return new Settings();
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        saveContent(this.fileType.toString());
    }

    private FileType loadType() {
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            saveContent("JSON");
            return FileType.JSON;
        }

        try {
            if (Files.readString(path).trim().equalsIgnoreCase("CSV")) {
                return FileType.CSV;
            }
        } catch (IOException e) {
            System.out.println("Unexpected error." + e);
        }
        return FileType.JSON;
    }

    private void saveContent(String content) {
        try {
            Files.write(Path.of(FILE_PATH), content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
