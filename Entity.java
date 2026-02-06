package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


// Default functions for now, remove or add any if needed
public abstract class Entity{
    protected float posX;
    protected float posY; 
    protected Color color;
    
    public float getX() {
        return posX;
    }
    public void setX(float x) {
        posX = x;
    }
    
    public float getY() {
        return posY;
    }
    public void setY(float y) {
        posY = y;
    }
    
    public Color getColor() {
        return color;
    }
    public void setColor(Color c) {
        color = c;
    }
    
    public void draw(ShapeRenderer shape) {
        // Override in subclasses to draw shapes
    }

    public void draw(SpriteBatch batch) {
        // Override in subclasses to draw TexturedObjects
    }

    public void update(float deltaTime) {
        // Override in subclasses
    }

    public void dispose() {
        // Override in subclasses to dispose of resources
    }

    // Constructor
    public Entity(float x, float y) {
        posX = x;
        posY = y;
        color = Color.WHITE;
    }

}
