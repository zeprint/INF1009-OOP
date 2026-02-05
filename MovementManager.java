package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;

// Movement Manager class for movement component lifecycle and updates (SRP: Component management) 
public class MovementManager {
    private Array<MovementComponent> components;
    
    // Initialises the empty list of movement components 
    public MovementManager() {
        this.components = new Array<>();
    }
    
    // Registers a movement component with MovementManager 
    public void registerComponent(MovementComponent component) {
        if (component != null && !components.contains(component, true)) {
            components.add(component);
        }
    }
    
    // Removes movement component 
    public void unregisterComponent(MovementComponent component) {
        components.removeValue(component, true);
    }
    
    // Gets a specific entity associated with the registered component 
    public MovementComponent getComponent(Entity entity) {
        for (MovementComponent component : components) {
            if (component.getEntity() == entity) {
                return component;
            }
        }
        return null;
    }
    
    // Updates all registered movement components 
    public void update(float deltaTime) {
        for (MovementComponent component : components) {
            if (component.isEnabled()) {
                component.update(deltaTime);
            }
        }
    }
    
    // Total number of registered components 
    public int getComponentCount() {
        return components.size;
    }
    
    // Removes all registered components 
    public void clearComponents() {
        components.clear();
    }
    
    // Array list of all registered components 
    public Array<MovementComponent> getAllComponents() {
        return new Array<>(components);
    }
}
