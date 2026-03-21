package io.github.some_example_name.lwjgl3.logic.entity;

/**
 * SmallObstacleFactory - Creates SmallObstacle entities via GenericFactory.
 *
 * Scroll speed is no longer passed here — it lives in ObstacleScrollMovement,
 * which is created and registered with MovementManager by the game scene.
 */
public class SmallObstacleFactory implements GenericFactory<SmallObstacle> {

    /**
     * @param x spawn x-coordinate
     * @param y floor surface y-coordinate
     */
    @Override
    public SmallObstacle create(float x, float y) {
        return new SmallObstacle(x, y);
    }
}