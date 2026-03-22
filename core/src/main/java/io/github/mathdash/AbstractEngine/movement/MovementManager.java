package io.github.mathdash.AbstractEngine.movement;

import com.badlogic.gdx.utils.Array;
import io.github.mathdash.AbstractEngine.entity.Entity;
import io.github.mathdash.AbstractEngine.entity.EntityManager;

/**
 * MovementManager - Provides query and bulk control over entities with MovementComponents.
 * Movement updates happen through the Entity/Component update pipeline,
 * so this manager focuses on management operations rather than driving updates.
 */
public class MovementManager {

    private final EntityManager entityManager;

    // Creates a MovementManager linked to the given EntityManager.
    public MovementManager(EntityManager entityManager) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager cannot be null.");
        }
        this.entityManager = entityManager;
    }

    // Returns all entities that have a MovementComponent attached.
    public Array<Entity> getMovingEntities() {
        return entityManager.getEntitiesWithComponent(MovementComponent.class);
    }

    // Enables or disables movement on a specific entity.
    public void setMovementEnabled(Entity entity, boolean enabled) {
        if (entity == null) return;
        MovementComponent mc = entity.getComponent(MovementComponent.class);
        if (mc != null) {
            if (enabled) {
                mc.enable();
            } else {
                mc.disable();
            }
        }
    }

    // Disables movement on all entities.
    public void freezeAll() {
        Array<Entity> moving = getMovingEntities();
        for (int i = 0; i < moving.size; i++) {
            setMovementEnabled(moving.get(i), false);
        }
    }

    // Enables movement on all entities.
    public void unfreezeAll() {
        Array<Entity> moving = getMovingEntities();
        for (int i = 0; i < moving.size; i++) {
            setMovementEnabled(moving.get(i), true);
        }
    }

    // Returns the number of entities with movement components.
    public int getMovingEntityCount() {
        return getMovingEntities().size;
    }
}
