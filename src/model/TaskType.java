package model;

import java.util.Optional;

public enum TaskType {
    DAILY,
    ROUTINE,
    DISPOSABLE;

    public static Optional<TaskType> fromString(String value) {
        try {
            return Optional.of(TaskType.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.err.println("Wrong TaskType.");
            System.out.println();
            return Optional.empty();
        }
    }
}
