package io.github.some_example_name.lwjgl3.logic.movement;

// To control the 2D position of moving entity - used for Horizontal and Jump Movement

public interface CoordinateTarget {

    float getX();

    void setX(float x);

    float getY();

    void setY(float y);
}
