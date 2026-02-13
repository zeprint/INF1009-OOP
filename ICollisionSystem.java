package io.github.some_example_name.lwjgl3;

// ICollisionSystem interface - Defines contract for collision detection and response
public interface ICollisionSystem {
    
    // Adds a collidable object to the collision system
    void addObject(Collidable collidable);
    
    // Removes a collidable object from the collision system
    void removeObject(Collidable collidable);
    
    // Performs collision detection and response checks
    void checkCollisions();
}
