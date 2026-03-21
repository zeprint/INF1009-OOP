package io.github.some_example_name.lwjgl3;

/**
 * CollisionDispatcher - Concrete implementation of CollisionHandler.
 *
 * Design pattern: Observer (this is the concrete observer).
 *
 * This is the ONLY place in the codebase where game collision rules live:
 *   - Character hits a SmallObstacle or TallObstacle → lose one health,
 *     trigger hit-flash, play hurt sound, check for death
 *   - Obstacle is hit by the Character → deactivate the obstacle
 *
 * GameScene creates one CollisionDispatcher, injects it into every
 * Character and obstacle via setCollisionHandler(), and also implements
 * the CollisionDispatcher.GameEventListener to respond to health loss
 * and death (e.g. switching to the death scene).
 *
 * Invincibility window:
 *   After taking damage the character is invincible for INVINCIBILITY_DURATION
 *   seconds. This prevents a single obstacle from draining multiple health
 *   points while the overlap persists across frames.
 */
public class CollisionDispatcher implements CollisionHandler {

    // ---- Invincibility window after taking damage ----
    private static final float INVINCIBILITY_DURATION = 1.0f;
    private float invincibilityTimer = 0f;

    // ---- Max health (used to validate and report) ----
    private static final int MAX_HEALTH = 3;
    private int health;

    // ---- Dependencies injected at construction ----
    private final IAudioSystem  audioSystem;
    private final GameEventListener gameEventListener;

    /**
     * GameEventListener - callback interface for the GameScene.
     *
     * Keeps CollisionDispatcher decoupled from any concrete scene class.
     */
    public interface GameEventListener {
        /** Called immediately after the character loses one health point. */
        void onHealthChanged(int newHealth, int maxHealth);
        /** Called when health reaches zero. */
        void onPlayerDeath();
    }

    // ---- Constructor ----

    /**
     * @param audioSystem       engine audio system for sound effects
     * @param gameEventListener callback to the GameScene for health/death events
     */
    public CollisionDispatcher(IAudioSystem audioSystem,
                               GameEventListener gameEventListener) {
        this.audioSystem       = audioSystem;
        this.gameEventListener = gameEventListener;
        this.health            = MAX_HEALTH;
    }

    // ---- Per-frame update (invincibility countdown) ----

    /**
     * Must be called once per frame from GameScene.update() so the
     * invincibility timer counts down correctly.
     *
     * @param deltaTime seconds since last frame
     */
    public void update(float deltaTime) {
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= deltaTime;
        }
    }

    // ---- CollisionHandler implementation ----

    /**
     * Handles the Character being notified of a collision.
     *
     * Rules:
     *   - Other is SmallObstacle or TallObstacle → apply damage if not invincible
     *   - Any other type → ignored (e.g. character overlapping its own bounds check)
     */
    @Override
    public void onCharacterCollision(Character character, CollisionResult result) {
        Collidable other = result.getOther();

        // Only obstacles cause damage
        if (!(other instanceof SmallObstacle) && !(other instanceof TallObstacle)) {
            return;
        }

        // Invincibility window — ignore hit
        if (invincibilityTimer > 0f) {
            return;
        }

        // Apply damage
        health--;
        invincibilityTimer = INVINCIBILITY_DURATION;

        // Visual feedback on the character
        character.triggerHitFlash();

        // Audio feedback
        if (audioSystem != null) {
            audioSystem.playSound("hurt");
        }

        // Notify GameScene
        if (gameEventListener != null) {
            gameEventListener.onHealthChanged(health, MAX_HEALTH);
            if (health <= 0) {
                gameEventListener.onPlayerDeath();
            }
        }
    }

    /**
     * Handles an obstacle being notified of a collision.
     *
     * Rule:
     *   - Any obstacle hit by anything → deactivate it (scroll it off-screen
     *     and remove from collision checks next frame)
     */
    @Override
    public void onObstacleCollision(Entity obstacle, CollisionResult result) {
        // Only deactivate if the other collidable is the Character
        if (result.getOther() instanceof Character) {
            obstacle.setActive(false);

            // Optional: play a sound for hitting an obstacle
            if (audioSystem != null) {
                audioSystem.playSound("obstacle_hit");
            }
        }
    }

    // ---- Accessors ----

    public int  getHealth()       { return health; }
    public int  getMaxHealth()    { return MAX_HEALTH; }
    public boolean isInvincible() { return invincibilityTimer > 0f; }

    /** Resets health and invincibility — call when restarting the game. */
    public void reset() {
        health             = MAX_HEALTH;
        invincibilityTimer = 0f;
    }
}
