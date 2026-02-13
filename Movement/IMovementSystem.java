package io.github.some_example_name.lwjgl3;

/**
 * IMovementSystem - Contract for movement management (DIP).
 *
 * Callers depend on this interface rather than MovementManager directly,
 * allowing the movement system to be swapped without touching call sites.
 *
 * Implementing class: MovementManager
 */
public interface IMovementSystem {

    /** Register a movement component. */
    void registerComponent(MovementComponent component);

    /** Unregister a movement component. */
    void unregisterComponent(MovementComponent component);

    /** Find the component attached to a specific entity. */
    MovementComponent getComponent(Entity entity);

    /** Update all enabled components. */
    void update(float deltaTime);
}
