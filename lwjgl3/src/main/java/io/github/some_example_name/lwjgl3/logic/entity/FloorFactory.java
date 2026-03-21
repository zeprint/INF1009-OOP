package io.github.some_example_name.lwjgl3;

/**
 * FloorFactory - Creates infinite-scrolling Floor entities via GenericFactory.
 *
 * Pre-configured with tile width, thickness, and scroll speed.
 * The {@code x} parameter of {@link #create(float, float)} is unused
 * (the floor always starts at x = 0); {@code y} sets the floor bottom edge.
 */
public class FloorFactory implements GenericFactory<Floor> {

    private final float tileWidth;
    private final float height;
    private final float scrollSpeed;

    /**
     * @param tileWidth   width of one tile segment (e.g. screen width)
     * @param height      floor thickness in pixels
     * @param scrollSpeed horizontal scroll speed (pixels / sec)
     */
    public FloorFactory(float tileWidth, float height, float scrollSpeed) {
        this.tileWidth = tileWidth;
        this.height = height;
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * @param x ignored (floor tiles always start at x = 0)
     * @param y bottom-edge y-coordinate of the floor
     */
    @Override
    public Floor create(float x, float y) {
        return new Floor(y, tileWidth, height, scrollSpeed);
    }
}
