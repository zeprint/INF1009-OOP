package io.github.mathdash.logic.entity;

import com.badlogic.gdx.graphics.Texture;

/**
 * AnswerBlockFactory - Creates AnswerBlock entities.
 * Design Pattern: Factory Method.
 */
public class AnswerBlockFactory {

    private final Texture correctTexture;
    private final Texture wrongTexture;
    private float scrollSpeed;

    public AnswerBlockFactory(Texture correctTexture, Texture wrongTexture, float scrollSpeed) {
        this.correctTexture = correctTexture;
        this.wrongTexture = wrongTexture;
        this.scrollSpeed = scrollSpeed;
    }

    public AnswerBlock create(float x, float y, int answerValue, boolean correct) {
        Texture tex = correct ? correctTexture : wrongTexture;
        return new AnswerBlock(tex, x, y, scrollSpeed, answerValue, correct);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
