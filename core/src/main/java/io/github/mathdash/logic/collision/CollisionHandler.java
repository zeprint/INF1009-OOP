package io.github.mathdash.logic.collision;

import io.github.mathdash.engine.collision.CollisionResult;
import io.github.mathdash.engine.entity.Entity;

/**
 * CollisionHandler - Observer interface for game-layer collision events.
 * Design Pattern: Observer.
 *
 * Entities hold a reference to this interface and delegate collision
 * responses to it, decoupling entities from game rules.
 */
public interface CollisionHandler {
    void onPlayerCollision(Entity player, CollisionResult result);
    void onObstacleCollision(Entity obstacle, CollisionResult result);
    void onAnswerCollision(Entity answer, CollisionResult result);
}
