package io.github.mathdash.AbstractEngine.movement;

import com.badlogic.gdx.utils.Array;

/**
 * MovementManager - Manages a list of MovementComponents.
 */
public class MovementManager {

    private final Array<MovementComponent> components;

    public MovementManager() {
        this.components = new Array<>();
    }

    // Called when an entity with a MovementComponent is spawned.
    public void add(MovementComponent component) {
        if (component != null && !components.contains(component, true)) {
            components.add(component);
        }
    }

    // Called when an entity with a MovementComponent is removed.
    public void remove(MovementComponent component) {
        components.removeValue(component, true);
    }

    // Asks all components to disable themselves.
    public void freezeAll() {
        for (int i = 0; i < components.size; i++) {
            components.get(i).disable();
        }
    }

    // Asks all components to enable themselves.
    public void unfreezeAll() {
        for (int i = 0; i < components.size; i++) {
            components.get(i).enable();
        }
    }

    public void setMovementEnabled(MovementComponent component, boolean enabled) {
        if (component == null) {
            return;
        }
        
        if (enabled) {
            component.enable();
        }
        else {
            component.disable();
        }
    }

    public int getCount() {
        return components.size;
    }
}