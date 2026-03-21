package io.github.some_example_name.lwjgl3.logic.movement;

// Scrolls an obsacle leftward a floor's speed

public class ObstacleScrollMovement extends io.github.some_example_name.lwjgl3.AbstractEngine.movement.MovementComponent {

    private final CoordinateTarget target;
    private float scrollSpeed;       // pixels per second (positive = leftward)
    private final float despawnX;    // x-coordinate at which the obstacle is off-screen

    private boolean offScreen;

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

    // Returns true once the obstacle has scrolled past the despawn boundary
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
