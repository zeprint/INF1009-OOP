package io.github.some_example_name.lwjgl3.logic.entity;

/**
 * TallObstacleFactory - Creates TallObstacle entities via GenericFactory.
 *
 * Scroll speed is no longer passed here — it lives in ObstacleScrollMovement,
 * which is created and registered with MovementManager by the game scene.
 */
public class TallObstacleFactory implements GenericFactory<TallObstacle> {

    /**
     * @param x spawn x-coordinate (lane centre)
     * @param y floor surface y-coordinate
     */
    @Override
    public TallObstacle create(float x, float y) {
        return new TallObstacle(x, y);
    }
}