package repository;

import exception.TaskNotFoundException;
import model.Task;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class TaskRepository {
    private TaskStrategy currentStorage;
    private final List<Task> tasks;

    public TaskRepository(TaskStrategy currentStorage) {
        this.currentStorage = currentStorage;
        this.tasks = currentStorage.load();
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public Optional<Task> findByLastFourChars(String lastFourChars) {
        return tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();
    }

    public Task save(Task task) {
        //it's still O(n) maybe change this to Map<UUID,Task>?
        tasks.removeIf(existingTask -> existingTask.getTaskId().equals(task.getTaskId()));
        tasks.add(task);
        return task;
    }

    public void delete(String lastFourChars) {
        Task task = this.findByLastFourChars(lastFourChars)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with: " + lastFourChars));
        tasks.remove(task);
    }

    public void flush() {
        this.currentStorage.save(tasks);
    }
}
