package io.github.some_example_name.lwjgl3;

/**
 * CollisionResult - Immutable data object describing a detected collision.
 *
 * Contains the other Collidable, overlap depths, and the direction.
 * FIX: Uses CollisionDirection enum instead of raw Strings for type safety.
 */
public class CollisionResult {

    private final Collidable         other;
    private final float              overlapX;
    private final float              overlapY;
    private final CollisionDirection direction;

    public CollisionResult(Collidable other, float overlapX, float overlapY,
                           CollisionDirection direction) {
        this.other     = other;
        this.overlapX  = overlapX;
        this.overlapY  = overlapY;
        this.direction = direction;
    }

    public Collidable         getOther()     { return other;     }
    public float              getOverlapX()  { return overlapX;  }
    public float              getOverlapY()  { return overlapY;  }
    public CollisionDirection getDirection() { return direction;  }
}
