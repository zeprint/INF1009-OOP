package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * CollisionManager - Collision detection with light fault tolerance.
 */
public class CollisionManager implements ICollisionSystem {

    private final Array<Collidable> collidables = new Array<>();
    private final Array<Collidable> toRemove = new Array<>();

    @Override
    public void addObject(Collidable obj) {
        if (obj == null || collidables.contains(obj, true)) {
            return;
        }

        // Basic validation
        if (!isValid(obj)) {
            System.err.println("[Collision] Invalid object rejected");
            return;
        }

        collidables.add(obj);
    }

    @Override
    public void removeObject(Collidable obj) {
        collidables.removeValue(obj, true);
    }

    public Array<Collidable> getCollidables() {
        return collidables;
    }

    @Override
    public void checkCollisions() {
        toRemove.clear();

        for (int i = 0; i < collidables.size; i++) {
            for (int j = i + 1; j < collidables.size; j++) {

                Collidable a = collidables.get(i);
                Collidable b = collidables.get(j);

                if (a == null || b == null) continue;

                // Validate before processing
                if (!isValid(a)) {
                    toRemove.add(a);
                    continue;
                }
                if (!isValid(b)) {
                    toRemove.add(b);
                    continue;
                }

                try {
                    if (detectCollision(a, b)) {
                        applyBehaviourSafely(a, resolveCollision(a, b));
                        applyBehaviourSafely(b, resolveCollision(b, a));
                    }
                } catch (Exception e) {
                    System.err.println("[Collision Error] " + e.getMessage());
                }
            }
        }

        // Clean up invalid objects
        for (Collidable obj : toRemove) {
            collidables.removeValue(obj, true);
        }

        if (toRemove.size > 0) {
            System.err.println("[Collision] Removed " + toRemove.size + " invalid objects");
        }
    }

    private boolean isValid(Collidable obj) {
        if (obj == null) return false;

        try {
            Rectangle bounds = obj.getBounds();
            if (bounds == null) return false;

            // Check for NaN/Infinity
            if (Float.isNaN(bounds.x) || Float.isInfinite(bounds.x)) return false;
            if (Float.isNaN(bounds.y) || Float.isInfinite(bounds.y)) return false;
            if (bounds.width < 0 || bounds.height < 0) return false;

            if (obj.getType() == null) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean detectCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();

        if (ra == null || rb == null) return false;

        return ra.overlaps(rb);
    }

    private CollisionResult resolveCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();

        float overlapX = Math.min(ra.x + ra.width - rb.x, rb.x + rb.width - ra.x);
        float overlapY = Math.min(ra.y + ra.height - rb.y, rb.y + rb.height - ra.y);

        CollisionDirection direction;
        if (overlapX < overlapY) {
            direction = (ra.x < rb.x) ? CollisionDirection.RIGHT : CollisionDirection.LEFT;
        } else {
            direction = (ra.y < rb.y) ? CollisionDirection.TOP : CollisionDirection.BOTTOM;
        }

        return new CollisionResult(b, overlapX, overlapY, direction);
    }

    private void applyBehaviourSafely(Collidable obj, CollisionResult result) {
        try {
            if (obj.getType() != null && obj.getType().triggersEvent()) {
                obj.onCollision(result);
            }
        } catch (Exception e) {
            System.err.println("[Collision Error] Callback failed: " + e.getMessage());
        }
    }

    public void clear() {
        collidables.clear();
        toRemove.clear();
    }
}
