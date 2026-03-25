package io.github.mathdash.engine.difficulty;

/**
 * DifficultyAdapter - Abstract base for adaptive difficulty systems.
 * Design Pattern: Template Method.
 *
 * The engine defines the abstract hooks (onCorrect, onWrong) and the
 * concrete query methods (getSpeedMultiplier, getDifficultyLevel).
 * Game-specific subclasses fill in the adaptation logic.
 *
 * This class uses no LibGDX imports - pure Java only.
 */
public abstract class DifficultyAdapter {

    protected float speedMultiplier = 1.0f;
    protected int streak = 0;

    /**
     * Called when the player answers correctly.
     * Subclasses define how the difficulty should adapt.
     */
    public abstract void onCorrect();

    /**
     * Called when the player answers incorrectly.
     * Subclasses define how the difficulty should adapt.
     */
    public abstract void onWrong();

    /**
     * Called when the player hits an obstacle.
     * Subclasses define how the difficulty should adapt.
     */
    public abstract void onObstacleHit();

    /**
     * Returns the current speed multiplier relative to the base speed.
     * A value of 1.0 means no change; > 1.0 means faster; < 1.0 means slower.
     */
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    /** Returns the current streak (positive = correct streak, negative = wrong streak). */
    public int getStreak() {
        return streak;
    }

    /** Resets the adapter to its initial state. */
    public void reset() {
        speedMultiplier = 1.0f;
        streak = 0;
    }
}
