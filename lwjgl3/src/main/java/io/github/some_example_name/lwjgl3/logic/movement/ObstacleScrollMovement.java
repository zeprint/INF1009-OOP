package io.github.some_example_name.lwjgl3.logic.movement;

/**
 * ObstacleScrollMovement - Scrolls an obstacle leftward at the floor's speed.
 *
 * Each obstacle spawns off-screen to the right and moves left in sync with
 * the floor.  When it scrolls past the despawn boundary (off-screen left)
 * the component marks itself disabled so the game layer can recycle or
 * remove the entity.
 *
 * Registered with MovementManager; the manager calls {@link #update(float)}
 * every frame.
 */
public class ObstacleScrollMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final CoordinateTarget target;
    private float scrollSpeed;       // pixels per second (positive = leftward)
    private final float despawnX;    // x-coordinate at which the obstacle is off-screen

    private boolean offScreen;

    /**
     * @param target      receives the updated x/y position each frame
     * @param startX      initial x-coordinate (spawn point, typically off-screen right)
     * @param startY      initial y-coordinate (floor surface)
     * @param scrollSpeed scroll speed in pixels/sec (positive = leftward)
     * @param despawnX    x-coordinate threshold; when the obstacle passes this it is
     *                    marked off-screen (e.g. -100f to give a small buffer)
     */
    public ObstacleScrollMovement(CoordinateTarget target,
                                  float startX,
                                  float startY,
                                  float scrollSpeed,
                                  float despawnX) {
        super();
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }

        this.target = target;
        this.scrollSpeed = scrollSpeed;
        this.despawnX = despawnX;
        this.offScreen = false;

        setPosition(startX, startY);
        target.setX(startX);
        target.setY(startY);
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        // Move leftward
        float nextX = getPositionX() - scrollSpeed * deltaTime;
        setPosition(nextX, getPositionY());

        target.setX(nextX);
        target.setY(getPositionY());

        // Mark off-screen when past the despawn boundary
        if (nextX < despawnX) {
            offScreen = true;
            disable();
        }
    }

    /** Returns true once the obstacle has scrolled past the despawn boundary. */
    public boolean isOffScreen() {
        return offScreen;
    }

    public float getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }
}
