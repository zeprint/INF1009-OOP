package io.github.some_example_name.lwjgl3.logic.scene;

/**
 * WordChallenge - Pure data class holding one language learning question.
 *
 * Stores the sentence with a blank, the correct answer, and two wrong answers.
 * Used by GameScene to display questions and spawn word option entities.
 *
 * SRP: only stores question data, no logic.
 */
public class WordChallenge {

    private final String sentence;
    private final String correctWord;
    private final String wrongWord1;
    private final String wrongWord2;

    public WordChallenge(String sentence, String correctWord, String wrongWord1, String wrongWord2) {
        this.sentence    = sentence;
        this.correctWord = correctWord;
        this.wrongWord1  = wrongWord1;
        this.wrongWord2  = wrongWord2;
    }

    public String getSentence()    { return sentence; }
    public String getCorrectWord() { return correctWord; }
    public String getWrongWord1()  { return wrongWord1; }
    public String getWrongWord2()  { return wrongWord2; }
}
