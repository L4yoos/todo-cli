package service;

import model.Task;
import model.TaskStatus;
import model.TaskType;
import repository.TaskRepository;

import java.util.List;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String name, String type) {
        TaskType taskType = TaskType.fromString(type)
                .orElseThrow(() -> new IllegalArgumentException("Wrong TaskType."));
        return taskRepository.save(Task.of(name, taskType));
    }

    public List<Task> displayTasks() {
        return taskRepository.getTasks();
    }

    public void checkTask(String lastFourChars) {
        taskRepository.findByLastFourChars(lastFourChars).ifPresentOrElse(
                task -> {
                    if (task.getStatus().equals(TaskStatus.DONE)) {
                        System.out.println("This Task is already DONE.");
                        return;
                    }
                    task.setStatus(TaskStatus.DONE);
                    taskRepository.save(task);
                },
                () -> System.err.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) {
        taskRepository.findByLastFourChars(lastFourChars).ifPresentOrElse(
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
                taskRepository.save(task);
            },
            () -> System.err.println("We didn't find Task with last four chars: " + lastFourChars)
        );
    }

    public void deleteTask(String lastFourChars) {
        taskRepository.delete(lastFourChars);
    }
}