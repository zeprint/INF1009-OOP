package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// Rotating shape entity (circle, triangle, or square) 
public class RotatingShape extends Entity {
    private float radius;
    private boolean isCircle;
    private boolean isSquare;
    private float rotationAngle;
    
    public RotatingShape(float x, float y, float radius, Color color, boolean isCircle) {
        super(x, y);
        this.radius = radius;
        this.color = color;
        this.isCircle = isCircle;
        this.isSquare = false;
        this.rotationAngle = 0f;
    }
    
    public float getRadius() {
        return radius;
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    public boolean isCircle() {
        return isCircle;
    }
    
    public void setIsSquare(boolean isSquare) {
        this.isSquare = isSquare;
        this.isCircle = false;
    }
    
    public void setRotationAngle(float angle) {
        this.rotationAngle = angle;
    }
    
    public float getRotationAngle() {
        return rotationAngle;
    }
    
    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        if (isCircle) {
            shape.circle(posX, posY, radius);
        } else if (isSquare) {
            drawSquare(shape);
        } else {
            // Draws equilateral triangle
            drawTriangle(shape);
        }
    }
    
    // Draws a rotating square 
    private void drawSquare(ShapeRenderer shape) {
        float angle = (float) Math.toRadians(rotationAngle);
        float halfSize = radius;
        
        // Calculate the 4 corners of the square rotated by the current angle
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        
        float x1 = posX + (-halfSize * cos - (-halfSize) * sin);
        float y1 = posY + (-halfSize * sin + (-halfSize) * cos);
        
        float x2 = posX + (halfSize * cos - (-halfSize) * sin);
        float y2 = posY + (halfSize * sin + (-halfSize) * cos);
        
        float x3 = posX + (halfSize * cos - halfSize * sin);
        float y3 = posY + (halfSize * sin + halfSize * cos);
        
        float x4 = posX + (-halfSize * cos - halfSize * sin);
        float y4 = posY + (-halfSize * sin + halfSize * cos);
        
        // Draw the square as two triangles
        shape.triangle(x1, y1, x2, y2, x3, y3);
        shape.triangle(x1, y1, x3, y3, x4, y4);
    }
    
    // Draw an equilateral triangle rotated by the current rotation angle 
    private void drawTriangle(ShapeRenderer shape) {
        float angle1 = (float) Math.toRadians(rotationAngle);
        float angle2 = angle1 + (float) (2 * Math.PI / 3);
        float angle3 = angle1 + (float) (4 * Math.PI / 3);
        
        float x1 = posX + radius * (float) Math.cos(angle1);
        float y1 = posY + radius * (float) Math.sin(angle1);
        float x2 = posX + radius * (float) Math.cos(angle2);
        float y2 = posY + radius * (float) Math.sin(angle2);
        float x3 = posX + radius * (float) Math.cos(angle3);
        float y3 = posY + radius * (float) Math.sin(angle3);
        
        shape.triangle(x1, y1, x2, y2, x3, y3);
    }
}
