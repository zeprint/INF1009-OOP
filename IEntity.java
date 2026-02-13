package io.github.some_example_name.lwjgl3;

// IEntity interface - Defines core lifecycle methods and inherits subsystem interfaces
public interface IEntity extends ISceneSystem, ICollisionSystem, IInputSystem, IAudioSystem, IMovement {
    
    // Initializes the entity
    void initialize();
    
    // Updates the entity with delta time
    void update(float deltaTime);
    
    // Disposes of entity resources
    void dispose();
}
