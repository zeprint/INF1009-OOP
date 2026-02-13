package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * TextureObject - Entity rendered via a SpriteBatch texture.
 *
 * FIX: Added dispose() to release the Texture, preventing GPU resource leaks.
 */
public class TextureObject extends Entity {

    private Texture texture;
    private int height;
    private int width;

    public TextureObject(Texture t, float x, float y, int h, int w) {
        super(x, y);
        this.texture = t;
        this.height = h;
        this.width = w;
    }

    // --- Accessors ---

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture t) {
        texture = t;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int h) {
        height = h;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int w) {
        width = w;
    }

    // --- Renderable ---

    @Override
    public void draw(SpriteBatch batch) {
        if (texture != null && batch != null) {
            batch.draw(texture, posX, posY, width, height);
        }
    }

    // --- Resource cleanup (FIX: was missing, caused GPU leak) ---

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        super.dispose();
    }
}
