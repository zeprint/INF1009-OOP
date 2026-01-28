package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureObject extends Entity {
    private Texture texture;
    private int height;
    private int width;
    
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
    
    // constructor for static texture object
    public TextureObject(Texture t, float x, float y, int h, int w) {
        super(x, y);
        texture = t;
        height = h;
        width = w;
    }

    // constructor for dynamic texture object
    public TextureObject(Texture t, float x, float y, float spdX, float spdY, int h, int w) {
        super(x, y, spdX, spdY);
        texture = t;
        height = h;
        width = w;
    }
}