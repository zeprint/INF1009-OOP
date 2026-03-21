package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;

public class MovementManager {

    private final Array<MovementComponent> components;

    public MovementManager() {
        this.components = new Array<>();
    }

    public void registerComponent(MovementComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        if (!components.contains(component, true)) {
            components.add(component);
        }
    }

    public void unregisterComponent(MovementComponent component) {
        if (component == null) {
            return;
        }
        components.removeValue(component, true);
    }

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
