package io.github.mathdash.logic.entity;

import io.github.mathdash.engine.entity.GenericFactory;
import io.github.mathdash.engine.entity.Renderable;

/**
 * AnswerBlockFactory - Creates AnswerBlock entities with pre-built Renderable components.
 */
public class AnswerBlockFactory implements GenericFactory<AnswerBlock> {

    private final Renderable blockRenderable;
    private float scrollSpeed;

    public AnswerBlockFactory(Renderable blockRenderable, float scrollSpeed) {
        this.blockRenderable = blockRenderable;
        this.scrollSpeed = scrollSpeed;
    }

    /**
     * Creates a default AnswerBlock with value 0 and incorrect.
     * Satisfies the GenericFactory contract for uniform entity creation.
     */
    @Override
    public AnswerBlock create(float x, float y) {
        return new AnswerBlock(blockRenderable, x, y, scrollSpeed, 0, false);
    }

    /**
     * Creates an AnswerBlock with a specific answer value and correctness.
     */
    public AnswerBlock create(float x, float y, int answerValue, boolean correct) {
        return new AnswerBlock(blockRenderable, x, y, scrollSpeed, answerValue, correct);
    }

    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
}
