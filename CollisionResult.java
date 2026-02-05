package io.github.some_example_name.lwjgl3;

public class CollisionResult {

    private final Collidable other;
    private final float overlapX;
    private final float overlapY;
    private final String direction;

    public CollisionResult(Collidable other, float overlapX, float overlapY, String direction) {
        this.other = other;
        this.overlapX = overlapX;
        this.overlapY = overlapY;
        this.direction = direction;
    }

    public Collidable getOther() {
        return other;
    }

    public float getOverlapX() {
        return overlapX;
    }

    public float getOverlapY() {
        return overlapY;
    }

    public String getDirection() {
        return direction;
    }
}
