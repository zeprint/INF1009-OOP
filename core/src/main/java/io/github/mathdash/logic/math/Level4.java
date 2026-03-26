package io.github.mathdash.logic.math;

/**
 * Level 4: Level 3 operations + Bracket expressions.
 * Uses iterative retry loops instead of recursion to avoid stack overflow
 */
public class Level4 extends Level3 {

    private static final int MAX_ATTEMPTS = 100;

    @Override
    protected void registerOperations() {
        super.registerOperations();
        operations.add(this::generateAddBracket);
        operations.add(this::generateSubtractBracket);
        operations.add(this::generateMultiplyAdd);
        operations.add(this::generateDivideAdd);
    }

    private MathQuestion generateAddBracket() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int a = random.nextInt(20) + 1, b = random.nextInt(20) + 1, c = random.nextInt(10) + 1;
            int answer = (a + b) * c;
            if (answer <= 100) {
                return createQuestion("(" + a + " + " + b + ") x " + c, answer);
            }
        }
        return createQuestion("(1 + 2) x 3", 9);
    }

    private MathQuestion generateSubtractBracket() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int a = random.nextInt(26) + 5, b = random.nextInt(a - 1) + 1, c = random.nextInt(4) + 2;
            int answer = (a - b) * c;
            if (answer <= 100) {
                return createQuestion("(" + a + " - " + b + ") x " + c, answer);
            }
        }
        return createQuestion("(5 - 2) x 3", 9);
    }

    private MathQuestion generateMultiplyAdd() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int a = random.nextInt(9) + 2, b = random.nextInt(9) + 2, c = random.nextInt(20) + 1;
            int answer = a * b + c;
            if (answer <= 100) {
                return createQuestion(a + " x " + b + " + " + c, answer);
            }
        }
        return createQuestion("2 x 3 + 4", 10);
    }

    private MathQuestion generateDivideAdd() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int c = random.nextInt(4) + 2, inner = random.nextInt(19) + 2, b = random.nextInt(10) + 1;
            int a = inner * c, answer = inner + b;
            if (answer <= 100 && a <= 100) {
                return createQuestion(a + " / " + c + " + " + b, answer);
            }
        }
        return createQuestion("6 / 2 + 1", 4);
    }
}
