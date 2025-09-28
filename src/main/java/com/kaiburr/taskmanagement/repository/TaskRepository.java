package com.kaiburr.taskmanagement.repository;

import com.kaiburr.taskmanagement.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    
    /**
     * Find tasks by name containing the given string (case-insensitive)
     */
    @Query("{ 'name': { $regex: '?0', $options: 'i' } }")
    List<Task> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find task by exact name
     */
    Optional<Task> findByName(String name);
    
    /**
     * Find tasks by owner
     */
    List<Task> findByOwner(String owner);
}
