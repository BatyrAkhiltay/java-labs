package com.akhiltay.lab5.controllers;

import com.akhiltay.lab5.entities.Category;
import com.akhiltay.lab5.entities.Task;
import com.akhiltay.lab5.entities.User;
import com.akhiltay.lab5.services.TaskService;
import com.akhiltay.lab5.services.UserService;
import com.akhiltay.lab5.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, CategoryService categoryService) {
        this.taskService = taskService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String viewTasks(Model model, Principal principal,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false, defaultValue = "asc") String direction) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks;
        
        if (categoryId != null) {
            Optional<Category> category = categoryService.getCategoryById(categoryId);
            if (category.isPresent()) {
                tasks = taskService.getTasksByUserAndCategory(user, category.get());
            } else {
                tasks = taskService.getTasksByUser(user);
            }
        } else if (status != null && !status.isEmpty()) {
            tasks = taskService.getTasksByUserAndStatus(user, status);
        } else {
            tasks = taskService.getTasksByUser(user);
        }

        if (sort != null) {
            tasks = sortTasks(tasks, sort, direction);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", taskService.getAvailableStatuses());
        model.addAttribute("priorities", taskService.getAvailablePriorities());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("currentCategory", categoryId);
        model.addAttribute("currentStatus", status);

        return "tasks/list";
    }

    private List<Task> sortTasks(List<Task> tasks, String sort, String direction) {
        Comparator<Task> comparator = switch (sort.toLowerCase()) {
            case "title" -> Comparator.comparing(Task::getTitle);
            case "duedate" -> Comparator.comparing(Task::getDueDate);
            case "status" -> Comparator.comparing(Task::getStatus);
            case "priority" -> Comparator.comparing(Task::getPriority);
            default -> Comparator.comparing(Task::getDueDate);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        Task task = new Task();
        task.setStatus(taskService.getAvailableStatuses().get(0)); // Устанавливаем статус по умолчанию
        task.setPriority(taskService.getAvailablePriorities().get(1)); // Устанавливаем средний приоритет по умолчанию

        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "tasks/add";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        task.setUser(user);
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model, Principal principal) {
        Optional<Task> taskOpt = taskService.getTaskById(id);
        User user = userService.findByUsername(principal.getName());

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (!task.getUser().getId().equals(user.getId())) {
                return "redirect:/tasks";
            }
            model.addAttribute("task", task);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "tasks/edit";
        }
        return "redirect:/tasks";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, @ModelAttribute Task task, Principal principal) {
        Optional<Task> existingTaskOpt = taskService.getTaskById(id);
        User user = userService.findByUsername(principal.getName());

        if (existingTaskOpt.isPresent()) {
            Task existingTask = existingTaskOpt.get();
            if (!existingTask.getUser().getId().equals(user.getId())) {
                return "redirect:/tasks";
            }
            task.setUser(existingTask.getUser());
            taskService.saveTask(task);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, Principal principal) {
        Optional<Task> taskOpt = taskService.getTaskById(id);
        User user = userService.findByUsername(principal.getName());

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (task.getUser().getId().equals(user.getId())) {
                taskService.deleteTask(id);
            }
        }
        return "redirect:/tasks";
    }
}