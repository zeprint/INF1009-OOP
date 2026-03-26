package io.github.mathdash.engine.collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * CollisionManager - Generic AABB collision detection and notification.
 *
 * Responsibilities:
 *   - Maintain a registry of active Collidable objects
 *   - Detect overlaps each frame using axis-aligned bounding boxes
 *   - Resolve overlap geometry and notify both parties via onCollision()
 *   - Self-clean invalid or null entries with fault tolerance
 */
public class CollisionManager {

    private final Array<Collidable> collidables = new Array<>();
    private final Array<Collidable> toRemove = new Array<>();

    // ---- Registry ----

    /**
     * Registers a Collidable for collision checks.
     * Null objects, duplicates, and objects with invalid bounds are silently rejected.
     */
    public void addObject(Collidable obj) {
        if (obj == null || collidables.contains(obj, true)) {
            return;
        }

        if (!isValid(obj)) {
            System.err.println("[CollisionManager] Invalid object rejected on add");
            return;
        }

        collidables.add(obj);
    }

    /**
     * Removes a Collidable from the registry.
     */
    public void removeObject(Collidable obj) {
        collidables.removeValue(obj, true);
    }

    /**
     * Returns the current list of registered Collidables (read-only intent).
     */
    public Array<Collidable> getCollidables() {
        return collidables;
    }

    /**
     * Clears all registered Collidables.
     */
    public void clear() {
        collidables.clear();
        toRemove.clear();
    }

    // ---- Per-frame check ----

    /**
     * Runs one full pass of collision detection and notification.
     *
     * For every overlapping pair (a, b):
     *   - Computes overlap geometry and direction for each perspective
     *   - Calls a.onCollision(result) and b.onCollision(result)
     *
     * Objects that fail validation mid-frame are deferred for removal
     * after the loop to avoid modifying the list during iteration.
     */
    public void checkCollisions() {
        toRemove.clear();

        for (int i = 0; i < collidables.size; i++) {
            for (int j = i + 1; j < collidables.size; j++) {

                Collidable a = collidables.get(i);
                Collidable b = collidables.get(j);

                if (a == null || b == null) {
                    continue;
                }

                if (!isValid(a)) { 
                    toRemove.add(a); 
                    continue; 
                }
                if (!isValid(b)) { 
                    toRemove.add(b); 
                    continue; 
                }

                if (!a.isCollidable() || !b.isCollidable()) {
                    continue;
                }

                try {
                    if (detectCollision(a, b)) {
                        notifySafely(a, resolveCollision(a, b));
                        notifySafely(b, resolveCollision(b, a));
                    }
                } catch (Exception e) {
                    System.err.println("[CollisionManager] Detection error: " + e.getMessage());
                }
            }
        }

        // Deferred removal of invalid objects
        if (toRemove.size > 0) {
            for (Collidable obj : toRemove) {
                collidables.removeValue(obj, true);
            }
            System.err.println("[CollisionManager] Removed " + toRemove.size + " invalid object(s)");
            toRemove.clear();
        }
    }

    // ---- Internal helpers ----

    /**
     * Validates a Collidable's bounds for NaN, Infinity, and negative dimensions.
     */
    private boolean isValid(Collidable obj) {
        if (obj == null) return false;
        try {
            Rectangle b = obj.getBounds();
            if (b == null) {
                return false;
            }
            if (Float.isNaN(b.x) || Float.isInfinite(b.x))  {
                return false;
            }
            if (Float.isNaN(b.y) || Float.isInfinite(b.y))  {
                return false;
            }
            if (b.width < 0 || b.height < 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the bounding boxes of a and b overlap.
     */
    private boolean detectCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();
        if (ra == null || rb == null) {
            return false;
        }
        return ra.overlaps(rb);
    }

    private CollisionResult resolveCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();

        float overlapX = Math.min(ra.x + ra.width  - rb.x, rb.x + rb.width  - ra.x);
        float overlapY = Math.min(ra.y + ra.height - rb.y, rb.y + rb.height - ra.y);

        CollisionDirection direction;
        if (overlapX < overlapY) {
            direction = (ra.x < rb.x) ? CollisionDirection.RIGHT : CollisionDirection.LEFT;
        } else {
            direction = (ra.y < rb.y) ? CollisionDirection.TOP : CollisionDirection.BOTTOM;
        }

        return new CollisionResult(b, overlapX, overlapY, direction);
    }

    /**
     * Notifies a Collidable of a collision, catching any exception thrown
     * by the Logic Engine's implementation to keep the engine stable.
     */
    private void notifySafely(Collidable obj, CollisionResult result) {
        try {
            obj.onCollision(result);
        } catch (Exception e) {
            System.err.println("[CollisionManager] onCollision callback failed: " + e.getMessage());
        }
    }
}
