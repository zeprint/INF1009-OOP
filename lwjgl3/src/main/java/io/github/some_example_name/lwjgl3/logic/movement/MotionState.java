package io.github.some_example_name.lwjgl3.logic.movement;

// This is a shared storage to store movement data from the Horizontal Movement and Jump Movement

public class MotionState {

    private float x;
    private float y;
    private float verticalVelocity;
    private boolean grounded;

    public MotionState(float x, float y) {
        this.x = x;
        this.y = y;
        this.verticalVelocity = 0f;
        this.grounded = true;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVerticalVelocity() {
        return verticalVelocity;
    }

    public void setVerticalVelocity(float verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }
}
