package io.github.mathdash.logic.movement;

import io.github.mathdash.engine.entity.PhysicsBody;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.engine.movement.MovementComponent;

/**
 * ScrollMovement - Moves entities from right to left at a configurable speed.
 * Design Pattern: Strategy (concrete movement strategy).
 */
public class ScrollMovement extends MovementComponent {

    private float speed;

    public ScrollMovement(float speed) {
        this.speed = speed;
    }

    @Override
    protected void applyMovement(float deltaTime, Transform transform, PhysicsBody physics) {
        transform.translate(-speed * deltaTime, 0);
    }

    public float getSpeed() { 
        return speed; 
    }
    public void setSpeed(float speed) { 
        this.speed = speed; 
    }
}
