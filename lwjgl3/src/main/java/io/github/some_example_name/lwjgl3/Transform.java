package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Vector2;

/**
 * Transform - Component that stores an entity's position and rotation in the world.
 */

public class Transform implements Component {

    private Vector2 position;
    private float rotation; // in degrees

    // Creates a Transform at the origin with no rotation.

    public Transform() {
        this(0f, 0f, 0f);
    }

    // Creates a Transform at the given position with no rotation.
    public Transform(float x, float y) {
        this(x, y, 0f);
    }

    // Creates a Transform at the given position and rotation.
    public Transform(float x, float y, float rotation) {
        this.position = new Vector2(x, y);
        this.rotation = rotation;
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

    // ---- Getters and Setters ----

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public float getX() {
        return position.x;
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public float getY() {
        return position.y;
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }


    // Moves the entity by the given offset.

    public void translate(float dx, float dy) {
        this.position.add(dx, dy);
    }


    // Rotates the entity by the given number of degrees.
    public void rotate(float degrees) {
        this.rotation += degrees;
    }
}