package io.github.mathdash.logic.entity;

import com.badlogic.gdx.graphics.Texture;

/**
 * PlayerFactory - Creates Player entities with all required textures.
 * Design Pattern: Factory Method.
 */
public class PlayerFactory implements GenericFactory<Player> {

    private final Texture walkA;
    private final Texture walkB;
    private final Texture idle;
    private final Texture hit;

    public PlayerFactory(Texture walkA, Texture walkB, Texture idle, Texture hit) {
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
