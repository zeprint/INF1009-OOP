package io.github.mathdash.logic.render;

import com.badlogic.gdx.utils.Array;

import io.github.mathdash.engine.collision.CollisionManager;
import io.github.mathdash.engine.entity.EntityManager;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.engine.movement.MovementManager;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.movement.ScrollMovement;

/**
 * EntityCleaner - Removes off-screen or inactive entities from
 * all engine managers.
 *
 * Extracted from GameScene to follow the Single Responsibility Principle.
 */
public class EntityCleaner {

    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;

    public EntityCleaner(CollisionManager collisionManager, EntityManager entityManager,
                         MovementManager movementManager) {
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
    }

    /**
     * Cleans up off-screen or inactive obstacles and answer blocks.
     *
     * @return true if all answer blocks have been removed (triggering new question generation)
     */
    public boolean cleanup(Array<Obstacle> activeObstacles, Array<AnswerBlock> activeAnswers) {
        for (int i = activeObstacles.size - 1; i >= 0; i--) {
            Obstacle obs = activeObstacles.get(i);
            Transform t = obs.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !obs.isActive()) {
                collisionManager.removeObject(obs);
                entityManager.removeEntity(obs);
                movementManager.remove(obs.getComponent(ScrollMovement.class));
                activeObstacles.removeIndex(i);
            }
        }

        boolean allAnswersGone = true;
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            Transform t = block.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !block.isActive()) {
                collisionManager.removeObject(block);
                entityManager.removeEntity(block);
                movementManager.remove(block.getComponent(ScrollMovement.class));
                activeAnswers.removeIndex(i);
            } else {
                allAnswersGone = false;
            }
        }

        return allAnswersGone;
    }
}
