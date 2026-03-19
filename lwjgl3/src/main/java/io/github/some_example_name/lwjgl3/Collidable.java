package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

/**
 * Collidable - Interface for entities that participate in collision detection and resolution.
 */

public interface Collidable {

    // Returns the axis-aligned bounding box used for collision detection.
    Rectangle getBounds();

    // Called by the Collision Manager when this entity collides with another.
    void onCollision(Entity other);

    // Returns whether this entity is currently able to collide.
    boolean isCollidable();
}