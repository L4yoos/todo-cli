package model;

import java.util.Optional;

public enum FileType {
    JSON,
    CSV;

    public static Optional<FileType> fromString(String fileType) {
        try {
            return Optional.of(FileType.valueOf(fileType));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
