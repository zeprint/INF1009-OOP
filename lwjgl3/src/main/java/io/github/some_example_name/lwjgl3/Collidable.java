package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

/**
 * Collidable - Contract for entities that participate in collision detection (ISP).
 *
 * Any entity that needs collision behaviour implements this interface
 * and registers with the CollisionManager.
 */
public interface Collidable {

    /** @return axis-aligned bounding box for this object. */
    Rectangle getBounds();

    /** @return collision type controlling detection/response behaviour. */
    CollisionType getType();

    /* Called by CollisionManager when a collision is detected. */
    void onCollision(CollisionResult result);
}
