package io.github.some_example_name.lwjgl3.logic.Collision;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.logic.entity.Character;
/**
 * CollisionHandler - Observer interface for game-layer collision events.
 *
 * Design pattern: Observer.
 *
 * Entities (Character, SmallObstacle, TallObstacle) hold a reference to
 * this interface and call it from onCollision(). This decouples the entity
 * from any game rule — the entity only knows "something hit me", and the
 * CollisionDispatcher (the concrete observer) decides what happens next.
 *
 * This interface lives in the engine package because it is referenced by
 * Collidable entities that are also in the engine package. It contains
 * no game-specific logic itself — it is purely a callback contract.
 *
 * GameScene wires the concrete CollisionDispatcher to every entity
 * at scene creation time.
 */
public interface CollisionHandler {

    /**
     * Called when the Character overlaps any other Collidable.
     *
     * @param character  the Character that was hit
     * @param result     collision geometry from the engine
     */
    void onCharacterCollision(Character character, CollisionResult result);

    /**
     * Called when an obstacle (SmallObstacle or TallObstacle) is hit.
     *
     * @param obstacle   the Entity obstacle that was struck
     * @param result     collision geometry from the engine
     */
    void onObstacleCollision(Entity obstacle, CollisionResult result);
}
