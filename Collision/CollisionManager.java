package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * CollisionManager - Detects and resolves collisions between Collidable objects (SRP).
 *
 * Implements ICollisionSystem so callers depend on the abstraction (DIP).
 * Detection  : AABB overlap test.
 * Resolution : Minimum-overlap axis determines the collision direction.
 * Direction labels: LEFT, RIGHT, TOP, BOTTOM (relative to entity A).
 */
public class CollisionManager implements ICollisionSystem {

    private final Array<Collidable> collidables = new Array<>();

    // --- Registration ---

    @Override
    public void addObject(Collidable obj) {
        if (obj != null && !collidables.contains(obj, true)) {
            collidables.add(obj);
        }
    }

    @Override
    public void removeObject(Collidable obj) {
        collidables.removeValue(obj, true);
    }

    public Array<Collidable> getCollidables() {
        return collidables;
    }

    // --- Per-frame collision pass ---

    @Override
    public void checkCollisions() {
        for (int i = 0; i < collidables.size; i++) {
            for (int j = i + 1; j < collidables.size; j++) {
                Collidable a = collidables.get(i);
                Collidable b = collidables.get(j);
                if (a == null || b == null) continue;

                if (detectCollision(a, b)) {
                    applyBehaviour(a, resolveCollision(a, b));
                    applyBehaviour(b, resolveCollision(b, a));
                }
            }
        }
    }

    // --- Detection ---

    private boolean detectCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();
        return ra != null && rb != null && ra.overlaps(rb);
    }

    // --- Resolution ---

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

    // --- Behaviour dispatch ---

    private void applyBehaviour(Collidable a, CollisionResult result) {
        if (a.getType() != null && a.getType().triggersEvent()) {
            a.onCollision(result);
        }
    }
}
