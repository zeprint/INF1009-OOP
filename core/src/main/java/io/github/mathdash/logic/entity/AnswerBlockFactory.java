package io.github.mathdash.logic.entity;

import com.badlogic.gdx.graphics.Texture;

/**
 * AnswerBlockFactory - Creates AnswerBlock entities.
 * Design Pattern: Factory Method.
 */
public class AnswerBlockFactory {

    private final Texture blockTexture;
    private float scrollSpeed;

    public AnswerBlockFactory(Texture blockTexture, float scrollSpeed) {
        this.blockTexture = blockTexture;
        this.scrollSpeed = scrollSpeed;
    }

    public AnswerBlock create(float x, float y, int answerValue, boolean correct) {
        return new AnswerBlock(blockTexture, x, y, scrollSpeed, answerValue, correct);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
