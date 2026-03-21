package io.github.some_example_name.lwjgl3.logic.movement;

// Infinite horizontal scrolling for Floor entity

public class ScrollMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final float tileWidth;
    private float scrollSpeed;    // pixels per second (positive = leftward)

    private float tileAX;
    private float tileBX;

    public ScrollMovement(float tileWidth, float scrollSpeed) {
        super();
        if (tileWidth <= 0f) {
            throw new IllegalArgumentException("tileWidth must be positive");
        }

        this.tileWidth = tileWidth;
        this.scrollSpeed = scrollSpeed;

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

    public float getTileAX() {
        return tileAX;
    }

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
