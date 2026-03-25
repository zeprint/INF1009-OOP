package io.github.mathdash.logic.math;

/**
 * Level 4: Level 3 operations + Bracket expressions.
 */
public class Level4 extends Level3 {

    @Override
    protected void registerOperations() {
        super.registerOperations();
        operations.add(this::generateAddBracket);
        operations.add(this::generateSubtractBracket);
        operations.add(this::generateMultiplyAdd);
        operations.add(this::generateDivideAdd);
    }

    private MathQuestion generateAddBracket() {
        int a = random.nextInt(20) + 1, b = random.nextInt(20) + 1, c = random.nextInt(10) + 1;
        int answer = (a + b) * c;
        if (answer > 100) return generateAddBracket();
        return createQuestion("(" + a + " + " + b + ") x " + c, answer);
    }

    private MathQuestion generateSubtractBracket() {
        int a = random.nextInt(26) + 5, b = random.nextInt(a - 1) + 1, c = random.nextInt(4) + 2;
        int answer = (a - b) * c;
        if (answer > 100) return generateSubtractBracket();
        return createQuestion("(" + a + " - " + b + ") x " + c, answer);
    }

    private MathQuestion generateMultiplyAdd() {
        int a = random.nextInt(9) + 2, b = random.nextInt(9) + 2, c = random.nextInt(20) + 1;
        int answer = a * b + c;
        if (answer > 100) return generateMultiplyAdd();
        return createQuestion(a + " x " + b + " + " + c, answer);
    }

    private MathQuestion generateDivideAdd() {
        int c = random.nextInt(4) + 2, inner = random.nextInt(19) + 2, b = random.nextInt(10) + 1;
        int a = inner * c, answer = inner + b;
        if (answer > 100 || a > 100) return generateDivideAdd();
        return createQuestion(a + " / " + c + " + " + b, answer);
    }
}