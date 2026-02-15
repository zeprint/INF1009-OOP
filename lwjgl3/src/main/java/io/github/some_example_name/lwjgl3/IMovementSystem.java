package io.github.some_example_name.lwjgl3;

/**
 * IMovementSystem - Contract for movement management.
 */
public interface IMovementSystem {

    void registerComponent(MovementComponent component);

    void unregisterComponent(MovementComponent component);

    MovementComponent getComponent(Entity entity);

    void update(float deltaTime);
}
