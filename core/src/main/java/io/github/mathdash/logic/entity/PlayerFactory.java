package io.github.mathdash.logic.entity;

import io.github.mathdash.engine.entity.GenericFactory;
import io.github.mathdash.engine.entity.Renderable;

/**
 * PlayerFactory - Creates Player entities with pre-built Renderable components.
 * Design Pattern: Factory Method.
 *
 * Accepts Renderable objects (created in the scene layer from Textures)
 * so the logic layer stays decoupled from raw LibGDX texture loading.
 */
public class PlayerFactory implements GenericFactory<Player> {

    private final Renderable walkA;
    private final Renderable walkB;
    private final Renderable idle;
    private final Renderable hit;

    public PlayerFactory(Renderable walkA, Renderable walkB, Renderable idle, Renderable hit) {
        this.walkA = walkA;
        this.walkB = walkB;
        this.idle = idle;
        this.hit = hit;
    }

    @Override
    public Player create(float x, float y) {
        return new Player(walkA, walkB, idle, hit);
    }
}
