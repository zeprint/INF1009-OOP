package io.github.mathdash.AbstractEngine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Collidable - Interface for entities that participate in collision detection and resolution.
 */

public interface Collidable {

    // Returns the axis-aligned bounding box used for collision detection.
    Rectangle getBounds();

    // Called by the Collision Manager when this entity collides with another.
    void onCollision(CollisionResult result);

    // Returns whether this entity is currently able to collide.
    boolean isCollidable();
}
