package com.akhiltay.lab5.services;

import com.akhiltay.lab5.entities.Category;
import com.akhiltay.lab5.entities.Task;
import com.akhiltay.lab5.entities.User;
import com.akhiltay.lab5.repositories.CategoryRepository;
import com.akhiltay.lab5.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public static final List<String> AVAILABLE_STATUSES = Arrays.asList(
            "NEW", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "CANCELLED"
    );

    public static final List<String> AVAILABLE_PRIORITIES = Arrays.asList(
            "LOW", "MEDIUM", "HIGH", "URGENT"
    );

    @Autowired
    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<String> getAvailableStatuses() {
        return AVAILABLE_STATUSES;
    }

    public List<String> getAvailablePriorities() {
        return AVAILABLE_PRIORITIES;
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUserOrderByDueDateAsc(user);
    }

    public List<Task> getTasksByUserAndCategory(User user, Category category) {
        return taskRepository.findByUserAndCategory(user, category);
    }

    public List<Task> getTasksByUserAndStatus(User user, String status) {
        return taskRepository.findByUserAndStatus(user, status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}