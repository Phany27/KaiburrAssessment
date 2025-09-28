# Task Management REST API

A Java Spring Boot REST API for managing shell command tasks with MongoDB storage. This application allows you to create, search, delete, and execute shell commands safely with built-in security validation.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Command Execution**: Safely execute shell commands with output capture
- **Security**: Built-in command validation to prevent malicious code execution
- **MongoDB Storage**: Persistent storage for tasks and execution history
- **REST API**: Full RESTful API with proper HTTP status codes
- **Docker Support**: Easy setup with Docker Compose


## Output
![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212555.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212605.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212740.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212832.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212851.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20212913.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20213208.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20213330.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20213419.png)


![Screenshot](https://github.com/Phany27/KaiburrAssessment/blob/main/output/Screenshot%202025-09-28%20213551.png)










## Project Structure

```
task-management-api/
├── src/main/java/com/kaiburr/taskmanagement/
│   ├── TaskManagementApplication.java          # Main Spring Boot application
│   ├── controller/
│   │   └── TaskController.java                 # REST API endpoints
│   ├── model/
│   │   ├── Task.java                          # Task entity
│   │   └── TaskExecution.java                 # Task execution entity
│   ├── repository/
│   │   └── TaskRepository.java                # MongoDB repository
│   └── service/
│       ├── CommandExecutionService.java       # Command execution logic
│       └── CommandValidationService.java      # Security validation
├── src/main/resources/
│   └── application.properties                 # Application configuration
├── docker-compose.yml                         # MongoDB Docker setup
├── mongo-init.js                             # MongoDB initialization
├── pom.xml                                   # Maven dependencies
└── README.md                                 # This file
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for MongoDB)
- Git

## Quick Start

### 1. Clone and Setup

```bash
# Navigate to project directory
cd task-management-api

# Build the project
mvn clean compile
```

### 2. Start MongoDB with Docker

```bash
# Start MongoDB and Mongo Express
docker-compose up -d

# Verify MongoDB is running
docker ps
```

MongoDB will be available at `localhost:27017`
Mongo Express (web UI) will be available at `http://localhost:8081`
- Username: `admin`
- Password: `admin123`

### 3. Run the Application

```bash
# Run the Spring Boot application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Base URL: `http://localhost:8080/api/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all tasks or specific task by ID |
| PUT | `/` | Create or update a task |
| DELETE | `/{id}` | Delete a task by ID |
| GET | `/search?name={name}` | Find tasks by name (partial match) |
| PUT | `/{id}/execute` | Execute a task and add execution record |
| GET | `/allowed-commands` | Get list of allowed commands |
| GET | `/health` | Health check endpoint |

## API Usage Examples

### 1. Create a Task

```bash
curl -X PUT http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "123",
    "name": "Print Hello",
    "owner": "John Smith",
    "command": "echo Hello World!"
  }'
```

### 2. Get All Tasks

```bash
curl -X GET http://localhost:8080/api/tasks
```

### 3. Get Specific Task

```bash
curl -X GET "http://localhost:8080/api/tasks?id=123"
```

### 4. Search Tasks by Name

```bash
curl -X GET "http://localhost:8080/api/tasks/search?name=Hello"
```

### 5. Execute a Task

```bash
curl -X PUT http://localhost:8080/api/tasks/123/execute
```

### 6. Delete a Task

```bash
curl -X DELETE http://localhost:8080/api/tasks/123
```

### 7. Get Allowed Commands

```bash
curl -X GET http://localhost:8080/api/tasks/allowed-commands
```

## Sample JSON Responses

### Task Object
```json
{
  "id": "123",
  "name": "Print Hello",
  "owner": "John Smith",
  "command": "echo Hello World!",
  "taskExecutions": [
    {
      "id": "exec123",
      "startTime": "2023-04-21 15:51:42.276Z",
      "endTime": "2023-04-21 15:51:43.276Z",
      "output": "Hello World!"
    }
  ]
}
```

## Security Features

The application includes built-in security validation to prevent execution of dangerous commands:

### Allowed Commands
- `echo <text>` - Print text
- `ls [directory]` - List directory contents
- `cat <file>` - Display file contents
- `head <file>` - Show first lines of file
- `tail <file>` - Show last lines of file
- `grep <pattern> <file>` - Search in files
- `pwd` - Print working directory
- `whoami` - Show current user
- `date` - Show current date/time
- `uptime` - Show system uptime
- `ping <host>` - Ping a host
- `nslookup <domain>` - DNS lookup

### Blocked Commands
- File system modification commands (`rm`, `rmdir`, `del`, `format`, etc.)
- System control commands (`shutdown`, `reboot`, `halt`, etc.)
- User management commands (`sudo`, `su`, `passwd`, etc.)
- Permission commands (`chmod`, `chown`, etc.)
- Network commands that could be dangerous (`wget`, `curl`, `nc`, etc.)
- Command chaining (`;`, `|`, `&&`, `||`)
- Command injection patterns

## Testing with Postman

### Import Collection
1. Open Postman
2. Create a new collection called "Task Management API"
3. Add the following requests:

#### Create Task
- Method: PUT
- URL: `http://localhost:8080/api/tasks`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "id": "test123",
  "name": "Test Task",
  "owner": "Test User",
  "command": "echo Testing API"
}
```

#### Get All Tasks
- Method: GET
- URL: `http://localhost:8080/api/tasks`

#### Execute Task
- Method: PUT
- URL: `http://localhost:8080/api/tasks/test123/execute`

#### Search Tasks
- Method: GET
- URL: `http://localhost:8080/api/tasks/search?name=Test`

#### Delete Task
- Method: DELETE
- URL: `http://localhost:8080/api/tasks/test123`

## Development

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn clean package
```

### Running JAR
```bash
java -jar target/task-management-api-1.0.0.jar
```

## Troubleshooting

### MongoDB Connection Issues
1. Ensure Docker is running
2. Check if MongoDB container is up: `docker ps`
3. Verify MongoDB logs: `docker logs task-management-mongodb`

### Application Won't Start
1. Check if port 8080 is available
2. Verify Java 17+ is installed: `java -version`
3. Check application logs for errors

### Command Execution Fails
1. Ensure command is in the allowed list
2. Check command syntax
3. Verify the command doesn't contain blocked patterns

## Database Schema

### Tasks Collection
```json
{
  "_id": "ObjectId",
  "id": "String (unique)",
  "name": "String",
  "owner": "String", 
  "command": "String",
  "taskExecutions": ["Array of TaskExecution objects"]
}
```

### Task Executions Collection
```json
{
  "_id": "ObjectId",
  "startTime": "Date",
  "endTime": "Date",
  "output": "String"
}
```




