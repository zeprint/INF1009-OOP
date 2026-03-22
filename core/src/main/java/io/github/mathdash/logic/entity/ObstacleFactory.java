package io.github.mathdash.logic.entity;

import com.badlogic.gdx.graphics.Texture;

/**
 * ObstacleFactory - Creates Obstacle entities.
 * Design Pattern: Factory Method.
 */
public class ObstacleFactory implements GenericFactory<Obstacle> {

    private final Texture[] textures;
    private int textureIndex = 0;
    private float scrollSpeed;

    public ObstacleFactory(float scrollSpeed, Texture... textures) {
        this.textures = textures;
        this.scrollSpeed = scrollSpeed;
    }

    @Override
    public Obstacle create(float x, float y) {
        Texture tex = textures[textureIndex % textures.length];
        textureIndex++;
        return new Obstacle(tex, x, y, scrollSpeed);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
