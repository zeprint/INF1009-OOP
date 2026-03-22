package io.github.mathdash.logic.math;

import com.badlogic.gdx.math.MathUtils;

/**
 * MathQuestionGenerator - Generates math questions based on difficulty level.
 *
 * Level 1: Addition and Subtraction (results 0-100)
 * Level 2: Level 1 + Multiplication
 * Level 3: Level 2 + Division
 * Level 4: Level 3 + Brackets/Parentheses
 *
 * All numbers are positive integers, max value 100.
 */
public class MathQuestionGenerator {

    private final int level;

    public MathQuestionGenerator(int level) {
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Level must be between 1 and 4");
        }
        this.level = level;
    }

    public MathQuestion generate() {
        switch (level) {
            case 1: return generateLevel1();
            case 2: return generateLevel2();
            case 3: return generateLevel3();
            case 4: return generateLevel4();
            default: return generateLevel1();
        }
    }

    private MathQuestion generateLevel1() {
        boolean isAdd = MathUtils.randomBoolean();
        if (isAdd) {
            int a = MathUtils.random(1, 50);
            int b = MathUtils.random(1, 50);
            int answer = a + b;
            return createQuestion(a + " + " + b, answer);
        } else {
            int a = MathUtils.random(10, 100);
            int b = MathUtils.random(1, a);
            int answer = a - b;
            return createQuestion(a + " - " + b, answer);
        }
    }

    private MathQuestion generateLevel2() {
        int type = MathUtils.random(0, 2);
        switch (type) {
            case 0: {
                int a = MathUtils.random(1, 50);
                int b = MathUtils.random(1, 50);
                return createQuestion(a + " + " + b, a + b);
            }
            case 1: {
                int a = MathUtils.random(10, 100);
                int b = MathUtils.random(1, a);
                return createQuestion(a + " - " + b, a - b);
            }
            default: {
                int a = MathUtils.random(2, 10);
                int b = MathUtils.random(2, 10);
                return createQuestion(a + " x " + b, a * b);
            }
        }
    }

    private MathQuestion generateLevel3() {
        int type = MathUtils.random(0, 3);
        switch (type) {
            case 0: {
                int a = MathUtils.random(1, 50);
                int b = MathUtils.random(1, 50);
                return createQuestion(a + " + " + b, a + b);
            }
            case 1: {
                int a = MathUtils.random(10, 100);
                int b = MathUtils.random(1, a);
                return createQuestion(a + " - " + b, a - b);
            }
            case 2: {
                int a = MathUtils.random(2, 10);
                int b = MathUtils.random(2, 10);
                return createQuestion(a + " x " + b, a * b);
            }
            default: {
                int b = MathUtils.random(2, 10);
                int answer = MathUtils.random(1, 10);
                int a = b * answer;
                return createQuestion(a + " / " + b, answer);
            }
        }
    }

    private MathQuestion generateLevel4() {
        int type = MathUtils.random(0, 3);
        switch (type) {
            case 0: {
                int a = MathUtils.random(1, 20);
                int b = MathUtils.random(1, 20);
                int c = MathUtils.random(1, 10);
                int answer = (a + b) * c;
                if (answer > 100) return generateLevel4();
                return createQuestion("(" + a + " + " + b + ") x " + c, answer);
            }
            case 1: {
                int a = MathUtils.random(5, 30);
                int b = MathUtils.random(1, a - 1);
                int c = MathUtils.random(2, 5);
                int answer = (a - b) * c;
                if (answer > 100) return generateLevel4();
                return createQuestion("(" + a + " - " + b + ") x " + c, answer);
            }
            case 2: {
                int a = MathUtils.random(2, 10);
                int b = MathUtils.random(2, 10);
                int c = MathUtils.random(1, 20);
                int answer = a * b + c;
                if (answer > 100) return generateLevel4();
                return createQuestion(a + " x " + b + " + " + c, answer);
            }
            default: {
                int c = MathUtils.random(2, 5);
                int inner = MathUtils.random(2, 20);
                int a = inner * c;
                int b = MathUtils.random(1, 10);
                int answer = inner + b;
                if (answer > 100 || a > 100) return generateLevel4();
                return createQuestion(a + " / " + c + " + " + b, answer);
            }
        }
    }

    private MathQuestion createQuestion(String text, int correctAnswer) {
        int wrong1 = generateWrongAnswer(correctAnswer);
        int wrong2 = generateWrongAnswer(correctAnswer);
        while (wrong2 == wrong1) {
            wrong2 = generateWrongAnswer(correctAnswer);
        }
        return new MathQuestion(text + " = ?", correctAnswer, wrong1, wrong2);
    }

    private int generateWrongAnswer(int correct) {
        int offset = MathUtils.random(1, 10);
        boolean add = MathUtils.randomBoolean();
        int wrong = add ? correct + offset : correct - offset;
        if (wrong < 0) wrong = correct + offset;
        if (wrong == correct) wrong = correct + 1;
        return wrong;
    }
}
