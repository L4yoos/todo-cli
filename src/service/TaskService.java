package service;

import exception.TaskNotFoundException;
import exception.WrongTaskTypeException;
import model.FileType;
import model.Task;
import model.TaskStatus;
import model.TaskType;
import repository.CsvTaskStorage;
import repository.JsonTaskStorage;
import repository.TaskRepository;

import java.util.List;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String name, String type) {
        TaskType taskType = TaskType.fromString(type)
                .orElseThrow(() -> new WrongTaskTypeException("Wrong TaskType."));
        return taskRepository.save(Task.of(name, taskType));
    }

    public List<Task> displayTasks() {
        return taskRepository.getTasks();
    }

    public void checkTask(String lastFourChars) {
       Task task = taskRepository.findByLastFourChars(lastFourChars)
               .orElseThrow(() -> new TaskNotFoundException("Task not found with: " + lastFourChars));

       if (task.getStatus().equals(TaskStatus.DONE)) {
           throw new IllegalStateException("This Task is already DONE.");
       }

       task.setStatus(TaskStatus.DONE);
       taskRepository.save(task);
    }

    public void updateTask(String lastFourChars, int updateOption, String newUpdate) {
        Task task = taskRepository.findByLastFourChars(lastFourChars)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with: " + lastFourChars));

        switch(updateOption) {
            case 1 -> task.setName(newUpdate);
            case 2 -> {
                TaskType taskType = TaskType.fromString(newUpdate)
                    .orElseThrow(() -> new WrongTaskTypeException("Wrong TaskType."));
                task.setType(taskType);
            }
            default -> throw new IllegalStateException("Invalid update option");
        }
        taskRepository.save(task);
    }

    public void deleteTask(String lastFourChars) {
        taskRepository.delete(lastFourChars);
    }

    public void flush() {
        taskRepository.flush();
    }

    public void changeStrategy(FileType fileType) {
        if (fileType == FileType.JSON) taskRepository.changeStrategy(new JsonTaskStorage());
        taskRepository.changeStrategy(new CsvTaskStorage());
    }
}