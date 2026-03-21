package io.github.some_example_name.lwjgl3.logic.entity;

/**
 * FloorFactory - Creates infinite-scrolling Floor entities via GenericFactory.
 *
 * Pre-configured with tile width and thickness.
 * Scroll speed is no longer passed here — it lives in ScrollMovement,
 * which is created and registered with MovementManager by the game scene.
 *
 * The {@code x} parameter of {@link #create(float, float)} is unused
 * (the floor always starts at x = 0); {@code y} sets the floor bottom edge.
 */
public class FloorFactory implements GenericFactory<Floor> {

    private final float tileWidth;
    private final float height;

    /**
     * @param tileWidth width of one tile segment (e.g. screen width)
     * @param height    floor thickness in pixels
     */
    public FloorFactory(float tileWidth, float height) {
        this.tileWidth = tileWidth;
        this.height = height;
    }

    /**
     * @param x ignored (floor tiles always start at x = 0)
     * @param y bottom-edge y-coordinate of the floor
     */
    @Override
    public Floor create(float x, float y) {
        return new Floor(y, tileWidth, height);
    }
}