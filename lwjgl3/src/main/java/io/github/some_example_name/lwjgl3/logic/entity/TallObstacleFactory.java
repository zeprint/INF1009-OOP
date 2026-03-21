package io.github.some_example_name.lwjgl3.logic.entity;

/**
 * TallObstacleFactory - Creates TallObstacle entities via GenericFactory.
 *
 * Pre-configured with scroll speed; callers only supply the spawn position.
 */
public class TallObstacleFactory implements GenericFactory<TallObstacle> {

    private final float scrollSpeed;

    /**
     * @param scrollSpeed approach speed in pixels per second
     */
    public TallObstacleFactory(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * @param x spawn x-coordinate (lane centre)
     * @param y floor surface y-coordinate
     */
    @Override
    public TallObstacle create(float x, float y) {
        return new TallObstacle(x, y, scrollSpeed);
    }
}
