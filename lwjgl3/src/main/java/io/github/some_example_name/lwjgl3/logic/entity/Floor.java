package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;
import io.github.some_example_name.lwjgl3.logic.movement.ScrollMovement;

/**
 * Floor - An infinite scrolling ground entity that the Character runs on.
 *
 * Tile scrolling and wrapping is now delegated to ScrollMovement, which is
 * registered with the MovementManager.  Floor only retains its y-position
 * (via Transform) and a reference to the ScrollMovement for tile-position
 * queries needed by the rendering layer.
 *
 * The Floor is purely visual — ground detection is handled analytically
 * by JumpMovement using the floor surface y-coordinate.
 */
public class Floor extends Entity {

    private final float tileWidth;
    private final float height;

    /** Reference to the movement component for tile-position queries. */
    private ScrollMovement scrollMovement;

    /**
     * Creates an infinite Floor.
     *
     * @param y           bottom-edge y-coordinate (floor surface = y + height)
     * @param tileWidth   width of one tile segment (use screen width)
     * @param height      floor thickness in pixels
     */
    public Floor(float y, float tileWidth, float height) {
        super();
        this.tileWidth = tileWidth;
        this.height = height;
        this.scrollMovement = null;

        // Transform stores the floor's y-position
        addComponent(new Transform(0f, y));
    }

    // No update() override — ScrollMovement handles tile logic via MovementManager

    // ---- ScrollMovement injection ----

    /**
     * Injects the ScrollMovement component created by the game scene.
     * Must be called before the rendering layer queries tile positions.
     */
    public void setScrollMovement(ScrollMovement scrollMovement) {
        this.scrollMovement = scrollMovement;
    }

    public ScrollMovement getScrollMovement() {
        return scrollMovement;
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

    /** Left-edge x of tile A (for rendering). */
    public float getTileAX() { return scrollMovement.getTileAX(); }

    /** Left-edge x of tile B (for rendering). */
    public float getTileBX() { return scrollMovement.getTileBX(); }

    public float getScrollSpeed() {
        return scrollMovement.getScrollSpeed();
    }

    public void setScrollSpeed(float speed) {
        scrollMovement.setScrollSpeed(speed);
    }
}