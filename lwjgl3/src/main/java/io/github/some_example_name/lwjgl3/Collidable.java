package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

/**
 * Collidable - Contract for entities that participate in collision detection.
 *
 * Any entity that needs collision behaviour implements this interface
 * and registers with the CollisionManager.
 */
public interface Collidable {

    Rectangle getBounds();

    CollisionType getType();

    void onCollision(CollisionResult result);
}
