package com.kaiburr.taskmanagement.controller;

import com.kaiburr.taskmanagement.model.Task;
import com.kaiburr.taskmanagement.model.TaskExecution;
import com.kaiburr.taskmanagement.repository.TaskRepository;
import com.kaiburr.taskmanagement.service.CommandExecutionService;
import com.kaiburr.taskmanagement.service.CommandValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CommandExecutionService commandExecutionService;
    
    @Autowired
    private CommandValidationService commandValidationService;
    
    /**
     * GET /api/tasks - Get all tasks or a specific task by ID
     */
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null && !id.trim().isEmpty()) {
            // Return specific task by ID
            Optional<Task> task = taskRepository.findById(id);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            // Return all tasks
            List<Task> tasks = taskRepository.findAll();
            return ResponseEntity.ok(tasks);
        }
    }
    
    /**
     * PUT /api/tasks - Create or update a task
     */
    @PutMapping
    public ResponseEntity<?> createOrUpdateTask(@Valid @RequestBody Task task) {
        try {
            // Validate command
            if (!commandValidationService.isCommandSafe(task.getCommand())) {
                return ResponseEntity.badRequest()
                    .body("Error: Command is not safe to execute. Allowed commands: " + 
                          commandValidationService.getAllowedCommands());
            }
            
            // Save or update task
            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error creating/updating task: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/tasks/{id} - Delete a task by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        try {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return ResponseEntity.ok("Task deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error deleting task: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/tasks/search?name={name} - Find tasks by name
     */
    @GetMapping("/search")
    public ResponseEntity<?> findTasksByName(@RequestParam String name) {
        try {
            List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(name);
            if (tasks.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(tasks);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error searching tasks: " + e.getMessage());
        }
    }
    
    /**
     * PUT /api/tasks/{id}/execute - Execute a task and add TaskExecution
     */
    @PutMapping("/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        try {
            Optional<Task> taskOptional = taskRepository.findById(id);
            if (!taskOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Task task = taskOptional.get();
            
            // Validate command before execution
            if (!commandValidationService.isCommandSafe(task.getCommand())) {
                return ResponseEntity.badRequest()
                    .body("Error: Command is not safe to execute. Allowed commands: " + 
                          commandValidationService.getAllowedCommands());
            }
            
            // Execute the command
            TaskExecution taskExecution = commandExecutionService.executeCommand(task.getCommand());
            
            // Add execution to task
            task.addTaskExecution(taskExecution);
            
            // Save updated task
            Task updatedTask = taskRepository.save(task);
            
            return ResponseEntity.ok(updatedTask);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error executing task: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/tasks/allowed-commands - Get list of allowed commands
     */
    @GetMapping("/allowed-commands")
    public ResponseEntity<List<String>> getAllowedCommands() {
        return ResponseEntity.ok(commandValidationService.getAllowedCommands());
    }
    
    /**
     * GET /api/tasks/health - Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Task Management API is running");
    }
}
