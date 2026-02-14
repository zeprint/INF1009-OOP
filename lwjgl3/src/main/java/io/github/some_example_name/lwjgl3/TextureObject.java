package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * TextureObject - Entity rendered via a SpriteBatch texture.
 *
 */

public class TextureObject extends Entity {

    private static final String TAG = "TextureObject";

    private Texture texture;
    private float height;
    private float width;
    private boolean disposed;

    public TextureObject(Texture t, float x, float y, float h, float w) {
        super(x, y);

        if (t == null) {
            throw new IllegalArgumentException("TextureObject texture must not be null");
        }
        if (!Float.isFinite(h) || h <= 0f) {
            throw new IllegalArgumentException(
                "TextureObject height must be finite and positive: " + h);
        }
        if (!Float.isFinite(w) || w <= 0f) {
            throw new IllegalArgumentException(
                "TextureObject width must be finite and positive: " + w);
        }

        this.texture = t;
        this.height = h;
        this.width = w;
        this.disposed = false;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean setTexture(Texture t) {
        if (t == null) {
            Gdx.app.error(TAG, "setTexture rejected null texture");
            return false;
        }
        if (disposed) {
            Gdx.app.error(TAG, "setTexture rejected: entity is disposed");
            return false;
        }
        texture = t;
        return true;
    }

    public float getHeight() {
        return height;
    }

    public boolean setHeight(float h) {
        if (!Float.isFinite(h) || h <= 0f) {
            Gdx.app.error(TAG, "setHeight rejected invalid value: " + h);
            return false;
        }
        height = h;
        return true;
    }

    public float getWidth() {
        return width;
    }

    public boolean setWidth(float w) {
        if (!Float.isFinite(w) || w <= 0f) {
            Gdx.app.error(TAG, "setWidth rejected invalid value: " + w);
            return false;
        }
        width = w;
        return true;
    }

    // --- Renderable ---

    @Override
    public boolean draw(SpriteBatch batch) {
        if (batch == null || texture == null) {
            return false;
        }
        if (disposed) {
            Gdx.app.error(TAG, "draw skipped: entity is disposed");
            return false;
        }

        try {
            batch.draw(texture, posX, posY, width, height);
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Exception during draw", e);
            return false;
        }
    }

    @Override
    public boolean dispose() {
        if (disposed) {
            Gdx.app.error(TAG, "dispose called on already-disposed entity");
            return false;
        }

        try {
            if (texture != null) {
                texture.dispose();
                texture = null;
            }
            disposed = true;
            return super.dispose();
        } catch (Exception e) {
            Gdx.app.error(TAG, "Exception during dispose", e);
            disposed = true;
            return false;
        }
    }
}