package com.kaiburr.taskmanagement.service;

import com.kaiburr.taskmanagement.model.TaskExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

@Service
public class CommandExecutionService {
    
    @Autowired
    private CommandValidationService commandValidationService;
    
    /**
     * Executes a shell command and returns the result
     * @param command the command to execute
     * @return TaskExecution object with execution details
     */
    public TaskExecution executeCommand(String command) {
        if (!commandValidationService.isCommandSafe(command)) {
            throw new IllegalArgumentException("Command is not safe to execute: " + command);
        }
        
        Date startTime = new Date();
        StringBuilder output = new StringBuilder();
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Use appropriate shell based on OS
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Read output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // Wait for process to complete
            process.waitFor();
            
        } catch (Exception e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        
        Date endTime = new Date();
        
        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setStartTime(startTime);
        taskExecution.setEndTime(endTime);
        taskExecution.setOutput(output.toString().trim());
        
        return taskExecution;
    }
}
