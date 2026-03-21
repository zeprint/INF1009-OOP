package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.PhysicsBody;

/**
 * Floor - An infinite scrolling ground entity that the Character runs on.
 *
 * Internally the floor uses two tile segments, each as wide as
 * {@code tileWidth} (typically the screen width).  Every frame the tiles
 * scroll to the left at the speed stored in this entity's PhysicsBody.
 * When a tile moves entirely off-screen on the left it is repositioned
 * to the right of the other tile, creating a seamless infinite-floor illusion.
 *
 * The Floor is purely visual — ground detection is handled analytically
 * by {@link io.github.some_example_name.lwjgl3.logic.movement.JumpMovement}
 * using the floor surface y-coordinate, so no collision is needed.
 *
 * Transform stores the floor's y-coordinate (bottom edge).
 * PhysicsBody stores the horizontal scroll velocity (negative = leftward).
 */
public class Floor extends Entity {

    private final float tileWidth;
    private final float height;

    /** Left edge of each tile segment. */
    private float tileAX;
    private float tileBX;

    /**
     * Creates an infinite Floor.
     *
     * @param y           bottom-edge y-coordinate (floor surface = y + height)
     * @param tileWidth   width of one tile segment (use screen width)
     * @param height      floor thickness in pixels
     * @param scrollSpeed horizontal scroll speed (pixels / sec, positive = leftward)
     */
    public Floor(float y, float tileWidth, float height, float scrollSpeed) {
        super();
        this.tileWidth = tileWidth;
        this.height = height;

        // Place tile A at the origin and tile B directly to its right
        this.tileAX = 0f;
        this.tileBX = tileWidth;

        // ---- Attach engine components ----
        // Transform stores the floor's y-position (x is unused for the floor itself)
        addComponent(new Transform(0f, y));
        // PhysicsBody stores the scroll velocity (negative x = leftward scroll)
        addComponent(new PhysicsBody(-scrollSpeed, 0f, 1f));
    }

    // ---- Update (scrolling logic) ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        PhysicsBody body = getComponent(PhysicsBody.class);
        float offset = -body.getVelocity().x * deltaTime;  // positive offset = leftward movement
        tileAX -= offset;
        tileBX -= offset;

        // When a tile scrolls completely off the left edge, wrap it to the right
        if (tileAX + tileWidth <= 0f) {
            tileAX = tileBX + tileWidth;
        }
        if (tileBX + tileWidth <= 0f) {
            tileBX = tileAX + tileWidth;
        }

        super.update(deltaTime);
    }

    // ---- Accessors ----

    /** Y-coordinate of the floor bottom edge (from Transform). */
    public float getY() {
        return getComponent(Transform.class).getY();
    }

    /** Y-coordinate of the floor surface (top edge). */
    public float getSurfaceY() {
        return getComponent(Transform.class).getY() + height;
    }

    public float getHeight()    { return height; }
    public float getTileWidth() { return tileWidth; }

    /** Left-edge x of tile A (useful for rendering). */
    public float getTileAX()    { return tileAX; }

    /** Left-edge x of tile B (useful for rendering). */
    public float getTileBX()    { return tileBX; }

    public float getScrollSpeed() {
        return -getComponent(PhysicsBody.class).getVelocity().x;
    }

    public void setScrollSpeed(float speed) {
        PhysicsBody body = getComponent(PhysicsBody.class);
        body.setVelocity(-speed, body.getVelocity().y);
    }
}
