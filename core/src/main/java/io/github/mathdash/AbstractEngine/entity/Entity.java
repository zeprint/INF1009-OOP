package io.github.mathdash.AbstractEngine.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Entity - Abstract base for every object managed by the engine.
 * Uses a component-based architecture for flexible composition.
 */

public abstract class Entity {

    private final String id;
    private boolean active;
    private final ObjectMap<Class<? extends Component>, Component> components;

    // Creates a new Entity with an auto-generated unique ID.
    public Entity() {
        this(generateId());
    }

    // Creates a new Entity with the given ID.
    public Entity(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be null or empty.");
        }
        this.id = id;
        this.active = true;
        this.components = new ObjectMap<>();
    }

    // Generates a unique ID using libGDX's MathUtils and system time.
    private static String generateId() {
        return "entity_" + System.nanoTime() + "_" + MathUtils.random(0, 999999);
    }

    // ---- Component Management ----

    // Adds a component to this entity. Only one component of each type can exist on an entity at a time.
    public <T extends Component> Entity addComponent(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null.");
        }

        Class<? extends Component> type = component.getClass();

        // Dispose old component of same type if it exists
        Component existing = components.get(type);
        if (existing != null) {
            existing.dispose();
        }

        components.put(type, component);
        component.init(this);
        return this;
    }

    // Retrieves a component of the specified type from this entity.
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.get(type);
    }

    // Checks whether this entity has a component of the given type.
    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    // Removes a component of the given type from this entity. The removed component is disposed before removal.
    @SuppressWarnings("unchecked")
    public <T extends Component> T removeComponent(Class<T> type) {
        Component removed = components.remove(type);
        if (removed != null) {
            removed.dispose();
        }
        return (T) removed;
    }

    // ---- Lifecycle Methods ----

    // Called every frame to update this entity's state.
    public void update(float deltaTime) {
        if (!active) return;

        for (Component component : components.values()) {
            component.update(deltaTime);
        }
    }

    // Called when this entity is being removed from the simulation. Disposes all attached components and releases resources.
    public void dispose() {
        for (Component component : components.values()) {
            component.dispose();
        }
        components.clear();
    }

    // ---- Getters and Setters ----

    // Returns this entity's unique identifier.
    public String getId() {
        return id;
    }

    // Returns whether this entity is active. Inactive entities are skipped during update and render.
    public boolean isActive() {
        return active;
    }

    // Sets whether this entity is active.
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", active=" + active + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entity)) return false;
        return this.id.equals(((Entity) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}