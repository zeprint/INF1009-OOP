package io.github.mathdash.logic.math;

/**
 * Level 2: Level 1 operations + Multiplication.
 */
public class Level2 extends Level1 {

    @Override
    protected void registerOperations() {
        super.registerOperations();
        operations.add(this::generateMultiplication);
    }

    private MathQuestion generateMultiplication() {
        int a = random.nextInt(9) + 2;
        int b = random.nextInt(9) + 2;
        return createQuestion(a + " x " + b, a * b);
    }
}