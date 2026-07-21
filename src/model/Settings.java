package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
    private final static String FILE_PATH = "settings.txt";
    private final Path PATH = Path.of(FILE_PATH);
    private FileType fileType;

    private String separator;

    Settings() {
        String[] parts = this.loadFile();

        this.fileType = parts[0].equals(FileType.JSON.name()) ? FileType.JSON : FileType.CSV;
        this.separator = parts[1];
    }

    public static Settings init() {
        return new Settings();
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getSeparator() {
        return separator;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        saveContent(this.fileType.toString() + "\n" + this.separator);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        saveContent(this.fileType.toString() + "\n" + this.separator);
    }

    private String[] loadFile() {
        if (!Files.exists(PATH)) {
            saveContent("JSON\n,");
            return new String[]{"Json", ","};
        }
        try {
            String file = Files.readString(PATH);
            return file.split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveContent(String content) {
        try {
            Files.write(PATH, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Error.", e);
        }
    }
}
