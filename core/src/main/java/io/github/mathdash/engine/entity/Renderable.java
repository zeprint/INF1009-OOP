package io.github.mathdash.engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Renderable - Component that handles drawing an entity's texture on screen.
 */ 

public class Renderable implements Component {

    private TextureRegion textureRegion;
    private float width;
    private float height;
    private boolean visible;


    // Creates a Renderable with the given texture and dimensions.
    public Renderable(Texture texture, float width, float height) {
        this(new TextureRegion(texture), width, height);
    }


    // Creates a Renderable with the given texture region and dimensions.
    public Renderable(TextureRegion region, float width, float height) {
        this.textureRegion = region;
        this.width = width;
        this.height = height;
        this.visible = true;
    }

    @Override
    public void init(Entity owner) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {

    }


    // Draws this entity using the given SpriteBatch and Transform.
    public void render(SpriteBatch batch, Transform transform) {
        if (!visible || textureRegion == null || transform == null) {
            return;
        }

        batch.draw(
            textureRegion,
            transform.getX() - width / 2f,      // center the sprite on position
            transform.getY() - height / 2f,
            width / 2f,                         // origin x (for rotation)
            height / 2f,                        // origin y (for rotation)
            width,
            height,
            1f, 1f,              // scale x, scale y
            transform.getRotation()
        );
    }

    // ---- Getters and Setters ----

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion region) {
        this.textureRegion = region;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}