package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;

/**
 * MovementManager - Manages the lifecycle and per-frame updates of all MovementComponent instances.
 */
public class MovementManager implements IMovementSystem {

    private final Array<MovementComponent> components;

    public MovementManager() {
        this.components = new Array<>();
    }

    @Override
    public void registerComponent(MovementComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        if (!components.contains(component, true)) {
            components.add(component);
        }
    }

    @Override
    public void unregisterComponent(MovementComponent component) {
        components.removeValue(component, true);
    }

    @Override
    public MovementComponent getComponent(Entity entity) {
        for (MovementComponent c : components) {
            if (c.getEntity() == entity) return c;
        }
        return null;
    }

    @Override
    public void update(float deltaTime) {
        if (Float.isNaN(deltaTime) || Float.isInfinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("deltaTime must be a finite, non-negative value");
        }
        for (MovementComponent c : components) {
            if (c.isEnabled()) {
                c.update(deltaTime);
            }
        }
    }

    public int getComponentCount() {
        return components.size;
    }

    public void clearComponents() {
        components.clear();
    }

    public Array<MovementComponent> getAllComponents() {
        return new Array<>(components);
    }
}
