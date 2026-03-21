package io.github.some_example_name.lwjgl3.logic.movement;

/**
 * ScrollMovement - Drives infinite horizontal scrolling for the Floor entity.
 *
 * Manages two tile segments that scroll leftward.  When a tile moves entirely
 * off-screen it wraps to the right of the other tile, creating a seamless
 * infinite-floor illusion (identical to what Floor.update() did inline, but
 * now delegated to the MovementManager pipeline).
 *
 * Registered with MovementManager; the manager calls {@link #update(float)}
 * every frame.
 */
public class ScrollMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final float tileWidth;
    private float scrollSpeed;    // pixels per second (positive = leftward)

    /** Left-edge x of each tile segment. */
    private float tileAX;
    private float tileBX;

    /**
     * @param tileWidth   width of one tile segment (typically the screen width)
     * @param scrollSpeed horizontal scroll speed in pixels/sec (positive = leftward)
     */
    public ScrollMovement(float tileWidth, float scrollSpeed) {
        super();
        if (tileWidth <= 0f) {
            throw new IllegalArgumentException("tileWidth must be positive");
        }

        this.tileWidth = tileWidth;
        this.scrollSpeed = scrollSpeed;

        // Tile A starts at the origin; tile B directly to its right
        this.tileAX = 0f;
        this.tileBX = tileWidth;
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        float offset = scrollSpeed * deltaTime;
        tileAX -= offset;
        tileBX -= offset;

        // Wrap tiles that scroll completely off the left edge
        if (tileAX + tileWidth <= 0f) {
            tileAX = tileBX + tileWidth;
        }
        if (tileBX + tileWidth <= 0f) {
            tileBX = tileAX + tileWidth;
        }
    }

    // ---- Accessors (used by the rendering layer) ----

    /** Left-edge x of tile A. */
    public float getTileAX() {
        return tileAX;
    }

    /** Left-edge x of tile B. */
    public float getTileBX() {
        return tileBX;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public float getTileWidth() {
        return tileWidth;
    }
}
