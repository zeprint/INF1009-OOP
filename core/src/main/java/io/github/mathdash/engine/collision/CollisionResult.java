package io.github.mathdash.engine.collision;

/**
 * CollisionResult - Immutable data object describing a detected collision
 * from one Collidable's perspective.
 *
 * Contains the other Collidable involved, the overlap depths on each axis,
 * and the direction of impact relative to the receiver.
 *
 * This is a pure engine data structure with no game-specific knowledge.
 */
public class CollisionResult {

    private final Collidable other;
    private final float overlapX;
    private final float overlapY;
    private final CollisionDirection direction;

    public CollisionResult(Collidable other, float overlapX, float overlapY,
                           CollisionDirection direction) {
        this.other     = other;
        this.overlapX  = overlapX;
        this.overlapY  = overlapY;
        this.direction = direction;
    }

    /** The other Collidable involved in this collision. */
    public Collidable getOther() {
        return other;
    }

    /** Horizontal overlap depth in pixels. */
    public float getOverlapX() {
        return overlapX;
    }

    /** Vertical overlap depth in pixels. */
    public float getOverlapY() {
        return overlapY;
    }

    /** Direction of impact relative to the entity that received this result. */
    public CollisionDirection getDirection() {
        return direction;
    }
}
