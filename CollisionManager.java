package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CollisionManager implements ICollisionSystem {

    private final Array<Collidable> collidables = new Array<>();

    public void addObject(Collidable obj) {
        collidables.add(obj);
    }

    public void removeObject(Collidable obj) {
        collidables.removeValue(obj, true);
    }

    public Array<Collidable> getCollidables() {
        return collidables;
    }

    public void checkCollisions() {
        for (int i = 0; i < collidables.size; i++) {
            for (int j = i + 1; j < collidables.size; j++) {

                Collidable a = collidables.get(i);
                Collidable b = collidables.get(j);

                if (detectCollision(a, b)) {
                    CollisionResult resultA = resolveCollision(a, b);
                    CollisionResult resultB = resolveCollision(b, a);

                    applyBehaviour(a, resultA);
                    applyBehaviour(b, resultB);
                }
            }
        }
    }

    private boolean detectCollision(Collidable a, Collidable b) {
        return a.getBounds().overlaps(b.getBounds());
    }

    private CollisionResult resolveCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();

        float overlapX = Math.min(
                ra.x + ra.width - rb.x,
                rb.x + rb.width - ra.x
        );

        float overlapY = Math.min(
                ra.y + ra.height - rb.y,
                rb.y + rb.height - ra.y
        );

        String direction;
        if (overlapX < overlapY) {
            direction = (ra.x < rb.x) ? "LEFT" : "RIGHT";
        } else {
            direction = (ra.y < rb.y) ? "TOP" : "BOTTOM";
        }

        return new CollisionResult(b, overlapX, overlapY, direction);
    }

    private void applyBehaviour(Collidable a, CollisionResult result) {
        if (a.getType().triggersEvent()) {
            a.onCollision(result);
        }
    }
}
