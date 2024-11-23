package com.akhiltay.lab5.repositories;

import com.akhiltay.lab5.entities.Category;
import com.akhiltay.lab5.entities.Task;
import com.akhiltay.lab5.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserOrderByDueDateAsc(User user);
    List<Task> findByUserAndCategory(User user, Category category);
    List<Task> findByUserAndStatus(User user, String status);
}