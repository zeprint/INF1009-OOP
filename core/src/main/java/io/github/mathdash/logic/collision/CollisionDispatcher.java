package io.github.mathdash.logic.collision;

import io.github.mathdash.engine.collision.Collidable;
import io.github.mathdash.engine.collision.CollisionResult;
import io.github.mathdash.engine.entity.Entity;
import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.entity.Player;

/**
 * CollisionDispatcher - Concrete Observer for game collision events.
 * Design Pattern: Observer (concrete observer).
 *
 * Centralises all game-specific collision rules:
 *   - Player hits Obstacle  -> lose a life (always, regardless of surge state)
 *   - Player hits wrong AnswerBlock -> lose a life
 *   - Player hits correct AnswerBlock -> gain a life, increase speed
 *
 * Surge mode only affects scroll speed via SurgeComponent; it confers
 * no collision immunity. Life loss and invincibility frames behave
 * identically whether the player is surging or not.
 */
public class CollisionDispatcher implements CollisionHandler {

    private static final float INVINCIBILITY_DURATION = 1.0f;
    private float invincibilityTimer = 0f;

    private final IAudioSystem audioSystem;
    private final GameEventListener listener;

    public interface GameEventListener {
        void onHealthChanged(int newHealth);
        void onPlayerDeath();
        void onCorrectAnswer();
        void onWrongAnswer();
        void onObstacleHit();
    }

    public CollisionDispatcher(IAudioSystem audioSystem, GameEventListener listener) {
        this.audioSystem = audioSystem;
        this.listener = listener;
    }

    public void update(float deltaTime) {
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= deltaTime;
        }
    }

    @Override
    public void onPlayerCollision(Entity player, CollisionResult result) {
        Collidable other = result.getOther();

        if (other instanceof Obstacle) {
            handleObstacleHit((Player) player);
        } else if (other instanceof AnswerBlock) {
            handleAnswerHit((Player) player, (AnswerBlock) other);
        }
    }

    @Override
    public void onObstacleCollision(Entity obstacle, CollisionResult result) {
        if (result.getOther() instanceof Player) {
            obstacle.setActive(false);
        }
    }

    @Override
    public void onAnswerCollision(Entity answer, CollisionResult result) {
        if (result.getOther() instanceof Player) {
            answer.setActive(false);
        }
    }

    private void handleObstacleHit(Player player) {
        if (invincibilityTimer > 0f) return;

        player.loseLife();
        invincibilityTimer = INVINCIBILITY_DURATION;
        player.triggerHitFlash();

        if (audioSystem != null) audioSystem.playSound("hurt");

        if (listener != null) {
            listener.onHealthChanged(player.getLives());
            listener.onObstacleHit();
            if (player.getLives() <= 0) {
                listener.onPlayerDeath();
            }
        }
    }

    private void handleAnswerHit(Player player, AnswerBlock answer) {
        if (!answer.isActive()) return;

        if (answer.isCorrect()) {
            player.gainLife();
            if (audioSystem != null) audioSystem.playSound("correct");
            if (listener != null) {
                listener.onHealthChanged(player.getLives());
                listener.onCorrectAnswer();
            }
        } else {
            if (invincibilityTimer > 0f) return;
            player.loseLife();
            invincibilityTimer = INVINCIBILITY_DURATION;
            player.triggerHitFlash();
            if (audioSystem != null) audioSystem.playSound("wrong");
            if (listener != null) {
                listener.onHealthChanged(player.getLives());
                listener.onWrongAnswer();
                if (player.getLives() <= 0) {
                    listener.onPlayerDeath();
                }
            }
        }
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0f;
    }

    public void reset() {
        invincibilityTimer = 0f;
    }
}