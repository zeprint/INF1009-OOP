package io.github.mathdash.logic.component;

import io.github.mathdash.AbstractEngine.entity.Component;
import io.github.mathdash.AbstractEngine.entity.Entity;

/**
 * HealthComponent - Tracks an entity's lives/health.
 * Game-specific component (contextual layer).
 */
public class HealthComponent implements Component {

    private int lives;
    private final int maxLives;

    public HealthComponent(int maxLives) {
        this.maxLives = maxLives;
        this.lives = maxLives;
    }

    @Override
    public void init(Entity owner) {}

    @Override
    public void update(float deltaTime) {}

    @Override
    public void dispose() {}

    public int getLives() { return lives; }
    public int getMaxLives() { return maxLives; }

    public void loseLife() {
        if (lives > 0) lives--;
    }

    public void gainLife() {
        if (lives < maxLives) lives++;
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public void reset() {
        lives = maxLives;
    }
}
