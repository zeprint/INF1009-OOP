package io.github.some_example_name.lwjgl3;

// IMovement interface - Defines contract for movement component management
public interface IMovement {
    
    // Registers a movement component with the manager
    void registerComponent(MovementComponent component);
    
    // Unregisters a movement component from the manager
    void unregisterComponent(MovementComponent component);
    
    // Gets the movement component associated with an entity
    MovementComponent getComponent(Entity entity);
}
