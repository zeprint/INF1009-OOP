package io.github.mathdash.logic.entity;

import io.github.mathdash.AbstractEngine.entity.GenericFactory;
import io.github.mathdash.AbstractEngine.entity.Renderable;

/**
 * ObstacleFactory - Creates Obstacle entities with pre-built Renderable components.
 * Design Pattern: Factory Method.
 *
 * Cycles through an array of Renderables (saw, spike, slime) using round-robin.
 */
public class ObstacleFactory implements GenericFactory<Obstacle> {

    private final Renderable[] renderables;
    private int renderableIndex = 0;
    private float scrollSpeed;

    public ObstacleFactory(float scrollSpeed, Renderable... renderables) {
        this.renderables = renderables;
        this.scrollSpeed = scrollSpeed;
    }

    @Override
    public Obstacle create(float x, float y) {
        Renderable renderable = renderables[renderableIndex % renderables.length];
        renderableIndex++;
        return new Obstacle(renderable, x, y, scrollSpeed);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
