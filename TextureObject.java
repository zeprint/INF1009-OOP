package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Import added

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
    
    public TextureObject(Texture t, float x, float y, int h, int w) {
        super(x, y);
        texture = t;
        height = h;
        width = w;
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        if (texture != null && batch != null) {
            batch.draw(texture, posX, posY, width, height);
        }
    }
}