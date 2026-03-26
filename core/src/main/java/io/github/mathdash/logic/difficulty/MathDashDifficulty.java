package io.github.mathdash.logic.difficulty;

import io.github.mathdash.engine.difficulty.DifficultyAdapter;

/**
 * MathDashDifficulty - Concrete adaptive difficulty for MathDash.
 * Extends the engine's DifficultyAdapter (Template Method pattern).
 *
 * Adapts game speed based on player performance:
 *  - Correct answers: speed increases gradually (rewarding)
 *  - Wrong answers: speed decreases gently (forgiving)
 *  - Obstacle hits: slight speed decrease (give breathing room)
 *
 * This creates the "flow state" loop:
 *  - Good players = faster, harder, more exciting
 *  - Struggling players = slower, easier, less frustrating
 *
 * The player never notices the adaptation - it's seamless stealth
 * difficulty that keeps everyone in their optimal learning zone.
 */
public class MathDashDifficulty extends DifficultyAdapter {

    private static final float SPEED_UP_PER_CORRECT = 0.08f;  // +8% per correct
    private static final float SLOW_DOWN_ON_WRONG = 0.15f;     // -15% on wrong answer
    private static final float SLOW_DOWN_ON_HIT = 0.10f;       // -10% on obstacle hit
    private static final float MIN_MULTIPLIER = 0.6f;          // never slower than 60%
    private static final float MAX_MULTIPLIER = 2.5f;          // cap at 250%

    /** Streak bonus: every 3 correct in a row gives an extra speed bump. */
    private static final int STREAK_BONUS_THRESHOLD = 3;
    private static final float STREAK_BONUS = 0.05f;

    @Override
    public void onCorrect() {
        streak = Math.max(0, streak) + 1;
        speedMultiplier += SPEED_UP_PER_CORRECT;

        // Streak bonus every 3 correct in a row
        if (streak % STREAK_BONUS_THRESHOLD == 0) {
            speedMultiplier += STREAK_BONUS;
        }

        speedMultiplier = Math.min(speedMultiplier, MAX_MULTIPLIER);
    }

    @Override
    public void onWrong() {
        streak = Math.min(0, streak) - 1;
        speedMultiplier -= SLOW_DOWN_ON_WRONG;
        speedMultiplier = Math.max(speedMultiplier, MIN_MULTIPLIER);
    }

    @Override
    public void onObstacleHit() {
        streak = 0;
        speedMultiplier -= SLOW_DOWN_ON_HIT;
        speedMultiplier = Math.max(speedMultiplier, MIN_MULTIPLIER);
    }

    /** Returns whether the player is on a positive streak. */
    @Override
    public boolean isOnStreak() {
        return streak >= STREAK_BONUS_THRESHOLD;
    }

    /** Returns the current streak count (only positive streaks). */
    @Override
    public int getCorrectStreak() {
        return Math.max(0, streak);
    }
}
