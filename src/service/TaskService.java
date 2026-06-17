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

    public void createTask(String name, String type) throws IOException {
        Task task = Task.of(name, TaskType.valueOf(type.toUpperCase()));
        taskRepository.addTask(task);
    }

    public void displayTasks() throws IOException {
        List<Task> tasks = taskRepository.readTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        tasks.forEach(task -> {
            System.out.println("TaskId: " + task.getTaskId());
            System.out.println("Name: " + task.getName());
            System.out.println("Type: " + task.getType());
            System.out.println("Status: " + task.getStatus());
            System.out.println("createdAt: " + task.getCreatedAt());
            System.out.println("updatedAt: " + task.getUpdatedAt());
            System.out.println("---");
        });
    }

    public void checkTask(String lastFourChars) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        Optional<Task> taskOptional = tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            if (task.getStatus().equals(TaskStatus.DONE)) {
                System.out.println("This Task is already DONE.");
                return;
            }

            task.setStatus(TaskStatus.DONE);
            taskRepository.saveAll(tasks);
        } else {
            System.out.println("We don't found Task with last four chars: " + lastFourChars);
        }
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        Optional<Task> taskOptional = tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            switch(updateOption) {
                case 1:
                    task.setName(newUpdate);
                    break;
                case 2:
                    task.setType(TaskType.valueOf(newUpdate.toUpperCase()));
                    break;
                default:
                    break;
            }
            taskRepository.saveAll(tasks);
        } else {
            System.out.println("We don't found Task with last four chars: " + lastFourChars);
        }
    }

    public void deleteTask(String lastFourChars) throws IOException {
        List<Task> tasks = taskRepository.readTasks();

        Optional<Task> taskOptional = tasks.stream()
                .filter(t -> t.getTaskId().toString().endsWith(lastFourChars))
                .findFirst();

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            tasks.remove(task);
            taskRepository.saveAll(tasks);
        }
    }
}