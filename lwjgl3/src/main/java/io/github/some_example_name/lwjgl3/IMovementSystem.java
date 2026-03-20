package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;

/**
 * IMovementSystem - Contract for movement management.
 */
public interface IMovementSystem {

    void registerComponent(MovementComponent component);

    void unregisterComponent(MovementComponent component);

    void update(float deltaTime);

    Array<MovementComponent> getAllComponents();
}
