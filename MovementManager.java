package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;
import java.util.List;

/* Movement Manager class for movement component lifecycle and updates */
public class MovementManager {
    private List<MovementComponent> components;
    
    /* Initializes empty list of movement components */
    public MovementManager() {
        this.components = new ArrayList<>();
    }
    
    /* Register a movement component with MovementManager */
    public void registerComponent(MovementComponent component) {
        if (component != null && !components.contains(component)) {
            components.add(component);
        }
    }
    
    /* Remove movement component */
    public void unregisterComponent(MovementComponent component) {
        components.remove(component);
    }
    
    /* Get a specific entity associated with the registered component */
    public MovementComponent getComponent(Entity entity) {
        for (MovementComponent component : components) {
            if (component.getEntity() == entity) {
                return component;
            }
        }
        return null;
    }
    
    /* Update all registered movement components */
    public void update(float deltaTime) {
        for (MovementComponent component : components) {
            if (component.isEnabled()) {
                component.update(deltaTime);
            }
        }
    }
    
    /* Total number of registered components */
    public int getComponentCount() {
        return components.size();
    }
    
    /* Remove all registered components */
    public void clearComponents() {
        components.clear();
    }
    
    /* Array list of all registered components */
    public List<MovementComponent> getAllComponents() {
        return new ArrayList<>(components);
    }
}
