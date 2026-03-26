package io.github.mathdash.logic.math;

/**
 * MathQuestion - Data class representing a math question with its answer.
 */
public class MathQuestion {

    private final String questionText;
    private final int correctAnswer;
    private final int wrongAnswer1;
    private final int wrongAnswer2;

    public MathQuestion(String questionText, int correctAnswer, int wrongAnswer1, int wrongAnswer2) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getQuestionText() { 
        return questionText; 
    }
    public int getCorrectAnswer() { 
        return correctAnswer; 
    }
    public int getWrongAnswer1() { 
        return wrongAnswer1; 
    }
    public int getWrongAnswer2() { 
        return wrongAnswer2; 
    }
}
