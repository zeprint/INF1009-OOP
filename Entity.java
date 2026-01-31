package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


//Default functions for now, remove or add any if needed
public abstract class Entity{ //rename any variable if needed
    protected float posX;
    protected float posY; 
    protected Color color;
    protected float speedX;
    protected float speedY;
    protected SpriteBatch batch;
    
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
    
    public float getSpeedX() {
        return speedX;
    }
    public void setSpeedX(float spdX) {
        speedX = spdX;
    }
    
    public float getSpeedY() {
        return speedY;
    }
    public void setSpeedY(float spdY) {
        speedY = spdY;
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
    public void setBatch(SpriteBatch b) {
        batch = b;
    }
    
    public void draw(ShapeRenderer shape) {

    }

    public void draw(SpriteBatch batch) {
        
    }

    // For RotatingShape support
    public void setRotationAngle(float angle) {
        // Override in subclasses that support rotation
    }


    // constructor for null values
    public Entity() {
        posX = 0f;
        posY = 0f;
        color = null;
        speedX = 0f;
        speedY = 0f;
        batch = null;
    }

    // constructor for TexturedObject (no color, static)
    public Entity(float x, float y) {
        posX = x;
        posY = y;
        color = null;
        speedX = 0f;
        speedY = 0f;
        batch = null;
    }

    // contructor for TexturedObject (no color, dynamic)
    public Entity(float x, float y, float spdX, float spdY) {
        posX = x;
        posY = y;
        color = null;
        speedX = spdX;
        speedY = spdY;
        batch = null;
    }

    // constructor for shapes (color, static)
    public Entity(float x, float y, Color c) {
        posX = x;
        posY = y;
        color = c;
        speedX = 0f;
        speedY = 0f;
        batch = null;
    }

    // constructor for shapes (color, dynamic)
    public Entity(float x, float y, Color c, float spdX, float spdY) {
        posX = x;
        posY = y;
        color = c;
        speedX = spdX;
        speedY = spdY;
        batch = null;
    }
}
