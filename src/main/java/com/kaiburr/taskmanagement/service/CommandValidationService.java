package com.kaiburr.taskmanagement.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class CommandValidationService {
    
    // List of dangerous commands that should be blocked
    private static final List<String> DANGEROUS_COMMANDS = Arrays.asList(
        "rm", "rmdir", "del", "rd", "format", "fdisk", "mkfs", "dd",
        "shutdown", "reboot", "halt", "poweroff", "init 0", "init 6",
        "sudo", "su", "passwd", "useradd", "userdel", "usermod",
        "chmod", "chown", "chgrp", "chattr", "lsattr",
        "mount", "umount", "fstab", "crontab", "at",
        "kill", "killall", "pkill", "xkill",
        "wget", "curl", "nc", "netcat", "telnet", "ssh", "scp", "rsync",
        "python", "perl", "ruby", "node", "php", "bash", "sh", "zsh", "csh", "tcsh",
        "java", "javac", "mvn", "gradle", "npm", "pip", "gem",
        "git", "svn", "hg", "bzr",
        "docker", "kubectl", "helm", "terraform",
        "vi", "vim", "nano", "emacs", "pico",
        "cat", "less", "more", "head", "tail", "grep", "awk", "sed",
        "find", "locate", "which", "whereis", "type",
        "ps", "top", "htop", "free", "df", "du", "ls", "dir",
        "cd", "pwd", "mkdir", "touch", "cp", "mv", "ln",
        "echo", "printf", "printenv", "env", "set", "unset",
        "export", "alias", "unalias", "history", "clear"
    );
    
    // Pattern to detect command injection attempts
    private static final Pattern INJECTION_PATTERN = Pattern.compile(
        "[;&|`$(){}[\\]<>\"'\\\\]|\\$\\{.*\\}|\\(.*\\)|`.*`"
    );
    
    // Pattern to detect multiple commands (command chaining)
    private static final Pattern COMMAND_CHAINING_PATTERN = Pattern.compile(
        "[;&|]|&&|\\|\\|"
    );
    
    /**
     * Validates if a command is safe to execute
     * @param command the command to validate
     * @return true if command is safe, false otherwise
     */
    public boolean isCommandSafe(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        String trimmedCommand = command.trim();
        
        // Check for command chaining
        if (COMMAND_CHAINING_PATTERN.matcher(trimmedCommand).find()) {
            return false;
        }
        
        // Check for injection patterns
        if (INJECTION_PATTERN.matcher(trimmedCommand).find()) {
            return false;
        }
        
        // Extract the first word (command name)
        String[] parts = trimmedCommand.split("\\s+");
        if (parts.length == 0) {
            return false;
        }
        
        String commandName = parts[0].toLowerCase();
        
        // Check if command is in the dangerous commands list
        if (DANGEROUS_COMMANDS.contains(commandName)) {
            return false;
        }
        
        // Additional safety checks
        return isValidCommand(trimmedCommand);
    }
    
    /**
     * Additional validation for specific command patterns
     */
    private boolean isValidCommand(String command) {
        // Allow only basic safe commands
        String lowerCommand = command.toLowerCase();
        
        // Allow echo commands with basic text
        if (lowerCommand.startsWith("echo ")) {
            return true;
        }
        
        // Allow basic file operations (read-only)
        if (lowerCommand.startsWith("ls ") || lowerCommand.equals("ls")) {
            return true;
        }
        
        if (lowerCommand.startsWith("cat ") || lowerCommand.startsWith("head ") || 
            lowerCommand.startsWith("tail ") || lowerCommand.startsWith("grep ")) {
            return true;
        }
        
        // Allow basic system info commands
        if (lowerCommand.equals("pwd") || lowerCommand.equals("whoami") || 
            lowerCommand.equals("date") || lowerCommand.equals("uptime")) {
            return true;
        }
        
        // Allow basic network commands (read-only)
        if (lowerCommand.startsWith("ping ") || lowerCommand.startsWith("nslookup ")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets a list of allowed commands for reference
     */
    public List<String> getAllowedCommands() {
        return Arrays.asList(
            "echo <text>",
            "ls [directory]",
            "cat <file>",
            "head <file>",
            "tail <file>",
            "grep <pattern> <file>",
            "pwd",
            "whoami",
            "date",
            "uptime",
            "ping <host>",
            "nslookup <domain>"
        );
    }
}
