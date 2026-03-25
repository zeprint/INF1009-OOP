package io.github.mathdash.logic.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Level 1: Addition and Subtraction.
 */
public class Level1 implements Questions {

    protected final Random random = new Random();
    protected final List<Questions> operations = new ArrayList<>();

    public Level1() {
        registerOperations();
    }

    protected void registerOperations() {
        operations.add(this::generateAddition);
        operations.add(this::generateSubtraction);
    }

    @Override
    public MathQuestion generate() {
        return operations.get(random.nextInt(operations.size())).generate();
    }

    private MathQuestion generateAddition() {
        int a = random.nextInt(50) + 1;
        int b = random.nextInt(50) + 1;
        return createQuestion(a + " + " + b, a + b);
    }

    private MathQuestion generateSubtraction() {
        int a = random.nextInt(91) + 10;
        int b = random.nextInt(a) + 1;
        return createQuestion(a + " - " + b, a - b);
    }

    protected MathQuestion createQuestion(String text, int correctAnswer) {
        int wrong1 = generateWrong(correctAnswer);
        int wrong2;
        do { wrong2 = generateWrong(correctAnswer); } while (wrong2 == wrong1);
        return new MathQuestion(text + " = ?", correctAnswer, wrong1, wrong2);
    }

    protected int generateWrong(int correct) {
        int offset = random.nextInt(10) + 1;
        int wrong = random.nextBoolean() ? correct + offset : correct - offset;
        if (wrong < 0) wrong = correct + offset;
        if (wrong == correct) wrong = correct + 1;
        return wrong;
    }
}