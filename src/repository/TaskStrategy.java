package repository;

import model.Task;

import java.util.List;

public interface TaskStrategy {
    List<Task> load();
    void save(List<Task> tasks);
}
