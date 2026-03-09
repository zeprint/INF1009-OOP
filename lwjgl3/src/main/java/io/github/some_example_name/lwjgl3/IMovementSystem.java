package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;

/**
 * IMovementSystem - Contract for movement management.
 */
public interface IMovementSystem {

    void registerComponent(MovementComponent component);

    void unregisterComponent(MovementComponent component);

    MovementComponent getComponent(Positionable entity);

    Array<MovementComponent> getComponents(Positionable entity);

    void update(float deltaTime);
}
