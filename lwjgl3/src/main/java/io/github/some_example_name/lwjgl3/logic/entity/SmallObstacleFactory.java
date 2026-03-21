package io.github.some_example_name.lwjgl3;

/**
 * SmallObstacleFactory - Creates SmallObstacle entities via GenericFactory.
 *
 * The factory is pre-configured with the scroll speed so that callers
 * only need to specify the spawn position.
 */
public class SmallObstacleFactory implements GenericFactory<SmallObstacle> {

    private final float scrollSpeed;

    /**
     * @param scrollSpeed approach speed in pixels per second
     */
    public SmallObstacleFactory(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * @param x spawn x-coordinate
     * @param y floor surface y-coordinate
     */
    @Override
    public SmallObstacle create(float x, float y) {
        return new SmallObstacle(x, y, scrollSpeed);
    }
}
