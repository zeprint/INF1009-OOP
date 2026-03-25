package io.github.mathdash.logic.entity;

import com.badlogic.gdx.graphics.Texture;

/**
 * AnswerBlockFactory - Creates AnswerBlock entities.
 * Design Pattern: Factory Method.
 * Implements GenericFactory for polymorphic entity creation.
 */
public class AnswerBlockFactory implements GenericFactory<AnswerBlock> {

    private final Texture blockTexture;
    private float scrollSpeed;

    public AnswerBlockFactory(Texture blockTexture, float scrollSpeed) {
        this.blockTexture = blockTexture;
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * Creates a default AnswerBlock with value 0 and incorrect.
     * Satisfies the GenericFactory contract for uniform entity creation.
     */
    @Override
    public AnswerBlock create(float x, float y) {
        return new AnswerBlock(blockTexture, x, y, scrollSpeed, 0, false);
    }

    /**
     * Creates an AnswerBlock with a specific answer value and correctness.
     */
    public AnswerBlock create(float x, float y, int answerValue, boolean correct) {
        return new AnswerBlock(blockTexture, x, y, scrollSpeed, answerValue, correct);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
