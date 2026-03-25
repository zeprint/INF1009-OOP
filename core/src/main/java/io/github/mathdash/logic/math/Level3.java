package io.github.mathdash.logic.math;

/**
 * Level 3: Level 2 operations + Division.
 */
public class Level3 extends Level2 {

    @Override
    protected void registerOperations() {
        super.registerOperations();
        operations.add(this::generateDivision);
    }

    private MathQuestion generateDivision() {
        int b      = random.nextInt(9) + 2;
        int answer = random.nextInt(10) + 1;
        return createQuestion((b * answer) + " / " + b, answer);
    }
}