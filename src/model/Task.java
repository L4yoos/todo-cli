package model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private UUID taskId;
    private String name;
    private TaskType type;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Task(String name, TaskType type) {
        this.taskId = UUID.randomUUID();
        this.name = validateName(name);
        this.type = Objects.requireNonNull(type, "Type is required.");
        this.status = TaskStatus.PLANNED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Task of(String name, TaskType type) {
        return new Task(
                name,
                type
        );
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "Name is required.");
        name = name.trim();
        if (name.length() < 2) {
            throw new IllegalArgumentException("Name is too short.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name is too long.");
        }
        return name;
    }

    public UUID getTaskId() { return taskId; }
    public String getName() { return name; }
    public TaskType getType() { return type; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public void setName(String name) { this.name = validateName(name); }
    public void setType(TaskType type) { this.type = type; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String toJson() {
        return "{\n" +
                    " \"taskId\": \"" + taskId + "\",\n" +
                    " \"name\": \"" + name.replace("\"", "\\\"") + "\",\n" +
                    " \"type\": \"" + type.name() + "\",\n" +
                    " \"status\": \"" + status.name() + "\",\n" +
                    " \"createdAt\": \"" + createdAt.toString() + "\",\n" +
                    " \"updatedAt\": \"" + updatedAt.toString() + "\"\n" +
                "}\n";
    }

    public static Task fromJsonParts(
            UUID taskId, String name, TaskType type,
            TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        Task task = new Task(name, type);
        task.setTaskId(taskId);
        task.setStatus(status);
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(updatedAt);
        return task;
    }
}
