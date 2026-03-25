package io.github.mathdash.logic.component;

import io.github.mathdash.engine.entity.Component;
import io.github.mathdash.engine.entity.Entity;

/**
 * SurgeComponent - Tracks the player's "Surge Meter" for the
 * risk/reward combo system.
 *
 * Correct answers fill the meter. When full, the player enters
 * "Surge Mode" - temporary invincibility and a speed burst
 * that makes the game feel exhilarating.
 *
 * This creates stealth learning: players WANT to solve math
 * because it fuels power, not because they're being tested.
 */
public class SurgeComponent implements Component {

    private static final float MAX_SURGE = 1.0f;
    private static final float SURGE_DURATION = 4.0f;     // seconds of surge mode
    private static final float SURGE_PER_CORRECT = 0.35f; // each correct answer adds this
    private static final float SURGE_SPEED_BONUS = 1.5f;  // speed multiplier during surge

    private float surgeAmount = 0f;
    private boolean surging = false;
    private float surgeTimer = 0f;

    @Override
    public void init(Entity owner) {}

    @Override
    public void update(float deltaTime) {
        if (surging) {
            surgeTimer -= deltaTime;
            if (surgeTimer <= 0f) {
                surging = false;
                surgeTimer = 0f;
                surgeAmount = 0f;
            }
        }
    }

    @Override
    public void dispose() {}

    /** Adds surge energy from a correct answer. Triggers surge mode when full. */
    public void addSurge() {
        if (surging) return; // Already surging, don't stack
        surgeAmount = Math.min(MAX_SURGE, surgeAmount + SURGE_PER_CORRECT);
        if (surgeAmount >= MAX_SURGE) {
            activateSurge();
        }
    }

    /** Resets the surge meter (on wrong answer or obstacle hit). */
    public void resetSurge() {
        if (!surging) { // Don't reset mid-surge
            surgeAmount = 0f;
        }
    }

    private void activateSurge() {
        surging = true;
        surgeTimer = SURGE_DURATION;
    }

    // --- Queries ---

    /** Returns 0.0 to 1.0 representing how full the meter is. */
    public float getSurgeAmount() { return surgeAmount; }

    /** Returns true if the player is currently in surge mode. */
    public boolean isSurging() { return surging; }

    /** Returns time remaining in surge mode, or 0 if not surging. */
    public float getSurgeTimeRemaining() { return surgeTimer; }

    /** Returns the speed bonus multiplier during surge mode. */
    public float getSpeedBonus() {
        return surging ? SURGE_SPEED_BONUS : 1.0f;
    }

    /** Returns progress through surge mode (1.0 = just started, 0.0 = ending). */
    public float getSurgeProgress() {
        return surging ? (surgeTimer / SURGE_DURATION) : 0f;
    }

    public void reset() {
        surgeAmount = 0f;
        surging = false;
        surgeTimer = 0f;
    }
}
