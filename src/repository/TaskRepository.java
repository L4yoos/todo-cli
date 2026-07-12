package repository;

import exception.TaskNotFoundException;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepository {
    private TaskStrategy currentStorage;
    private List<Task> tasks;

    public TaskRepository(TaskStrategy currentStorage) {
        this.currentStorage = currentStorage;
        this.tasks = new ArrayList<>(currentStorage.load());
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
        tasks.removeIf(existingTask -> existingTask.equals(task));
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

    public void changeStrategy(TaskStrategy newStrategy) {
        List<Task> tasksToMove = new ArrayList<>(this.tasks);
        this.flush();

        this.currentStorage = newStrategy;

        this.tasks = new ArrayList<>(this.currentStorage.load());
        for (Task task : tasksToMove) {
            if (!this.tasks.contains(task)) {
                this.tasks.add(task);
            }
        }
    }
}
