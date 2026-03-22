package io.github.mathdash.AbstractEngine.movement;

import io.github.mathdash.AbstractEngine.entity.Component;
import io.github.mathdash.AbstractEngine.entity.Entity;
import io.github.mathdash.AbstractEngine.entity.Transform;
import io.github.mathdash.AbstractEngine.entity.PhysicsBody;

/**
 * MovementComponent - Abstract base for all movement behaviors.
 * Implements Component so it can be attached to an Entity.
 * Reads velocity from PhysicsBody and writes position to Transform.
 */
public abstract class MovementComponent implements Component {

    protected Entity owner;
    protected boolean enabled;

    public MovementComponent() {
        this.enabled = true;
    }

    @Override
    public void init(Entity owner) {
        this.owner = owner;
    }

    // Fetches Transform and PhysicsBody from the owner entity, then delegates to applyMovement.
    @Override
    public final void update(float deltaTime) {
        if (!enabled || owner == null) return;
        validateDeltaTime(deltaTime);

        Transform transform = owner.getComponent(Transform.class);
        PhysicsBody physics = owner.getComponent(PhysicsBody.class);

        if (transform == null) return;

        applyMovement(deltaTime, transform, physics);
    }

    // Subclasses implement their specific movement logic.
    // Transform is guaranteed non-null. PhysicsBody may be null if the entity has none.
    protected abstract void applyMovement(float deltaTime, Transform transform, PhysicsBody physics);

    @Override
    public void dispose() {
        owner = null;
    }

    // Shared guard to keep all movement updates consistent.
    protected final void validateDeltaTime(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("deltaTime must be a finite, non-negative value");
        }
    }

    // --- Enable / Disable ---

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
