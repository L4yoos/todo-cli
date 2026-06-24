package service;

import model.Task;
import model.TaskStatus;
import model.TaskType;
import repository.TaskRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String name, String type) throws IOException {
        TaskType taskType = TaskType.fromString(type)
                .orElseThrow(() -> new IllegalArgumentException("Wrong TaskType."));
        return taskRepository.addTask(Task.of(name, taskType));
    }

    public List<Task> displayTasks() throws IOException {
        List<Task> tasks = taskRepository.readTasks();
        if (tasks.isEmpty()) {
            return List.of();
        }
        return tasks;
    }

    public void checkTask(String lastFourChars) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        findByLastFourChars(tasks, lastFourChars).ifPresentOrElse(
                task -> {
                    if (task.getStatus().equals(TaskStatus.DONE)) {
                        System.out.println("This Task is already DONE.");
                        return;
                    }


                    task.setStatus(TaskStatus.DONE);
                    taskRepository.saveAll(tasks);
                },
                () -> System.out.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        findByLastFourChars(tasks, lastFourChars).ifPresentOrElse(
        task -> {
                switch(updateOption) {
                    case 1:
                        task.setName(newUpdate);
                        break;
                    case 2:
                        TaskType taskType = TaskType.fromString(newUpdate)
                            .orElseThrow(() -> new IllegalArgumentException("Wrong TaskType."));
                        task.setType(taskType);
                        break;
                    default:
                        break;
                }
                taskRepository.saveAll(tasks);
            },
            () -> System.out.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    public void deleteTask(String lastFourChars) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        findByLastFourChars(tasks, lastFourChars).ifPresentOrElse(
        task -> {
                tasks.remove(task);
                taskRepository.saveAll(tasks);
            },
            () -> System.out.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    private Optional<Task> findByLastFourChars(List<Task> tasks, String lastFourChars) {
        return tasks.stream()
                    .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                    .findFirst();
    }
}