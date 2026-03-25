package io.github.mathdash.logic.math;

import java.util.HashMap;
import java.util.Map;

/**
 * MathQuestionGenerator - Delegates to the correct QuestionStrategy by level.
 * Strategies are registered in a map — no switch statement.
 * Design Pattern: Strategy.
 */
public class MathQuestionGenerator {

    private static final Map<Integer, Questions> STRATEGIES = new HashMap<>();

    static {
        STRATEGIES.put(1, new Level1());
        STRATEGIES.put(2, new Level2());
        STRATEGIES.put(3, new Level3());
        STRATEGIES.put(4, new Level4());
    }

    private final Questions strategy;

    public MathQuestionGenerator(int level) {
        Questions s = STRATEGIES.get(level);
        if (s == null) {
            throw new IllegalArgumentException("No strategy registered for level: " + level);
        }
        this.strategy = s;
    }

    public MathQuestion generate() {
        return strategy.generate();
    }
}