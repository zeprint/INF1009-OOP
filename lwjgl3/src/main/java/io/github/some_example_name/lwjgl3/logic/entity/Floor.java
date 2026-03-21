package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;

/**
 * Floor - An infinite scrolling ground entity that the Character runs on.
 *
 * Internally the floor uses two tile segments, each as wide as
 * {@code tileWidth} (typically the screen width).  Every frame the tiles
 * scroll to the left at {@code scrollSpeed}.  When a tile moves entirely
 * off-screen on the left it is repositioned to the right of the other
 * tile, creating a seamless infinite-floor illusion.
 *
 * The Floor is purely visual — ground detection is handled analytically
 * by {@link io.github.some_example_name.lwjgl3.logic.movement.JumpMovement}
 * using the floor surface y-coordinate, so no collision is needed.
 */
public class Floor extends Entity {

    private final float tileWidth;
    private final float height;
    private final float y;
    private float scrollSpeed;

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
        this.y = y;
        this.tileWidth = tileWidth;
        this.height = height;
        this.scrollSpeed = scrollSpeed;

        // Place tile A at the origin and tile B directly to its right
        this.tileAX = 0f;
        this.tileBX = tileWidth;
    }

    // ---- Update (scrolling logic) ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        float offset = scrollSpeed * deltaTime;
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

    /** Y-coordinate of the floor bottom edge. */
    public float getY()         { return y; }

    /** Y-coordinate of the floor surface (top edge). */
    public float getSurfaceY()  { return y + height; }

    public float getHeight()    { return height; }
    public float getTileWidth() { return tileWidth; }

    /** Left-edge x of tile A (useful for rendering). */
    public float getTileAX()    { return tileAX; }

    /** Left-edge x of tile B (useful for rendering). */
    public float getTileBX()    { return tileBX; }

    public float getScrollSpeed() { return scrollSpeed; }
    public void  setScrollSpeed(float speed) { this.scrollSpeed = speed; }
}
