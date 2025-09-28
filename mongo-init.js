// MongoDB initialization script
db = db.getSiblingDB('taskmanagement');

// Create collections
db.createCollection('tasks');
db.createCollection('task_executions');

// Create indexes for better performance
db.tasks.createIndex({ "name": 1 });
db.tasks.createIndex({ "owner": 1 });
db.tasks.createIndex({ "id": 1 }, { unique: true });

db.task_executions.createIndex({ "startTime": 1 });
db.task_executions.createIndex({ "endTime": 1 });

print('Database initialized successfully');
