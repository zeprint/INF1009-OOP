package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
/**
 * GenericFactory - Interface for creating pre-configured entities.
 *
 * Implementations assemble a specific entity type (character, obstacle,
 * floor, etc.) with all required components already attached.  This keeps
 * entity-construction logic in one place and lets the game scene spawn
 * objects without knowing their internal wiring.
 *
 * @param <T> the concrete Entity type this factory produces
 */
public interface GenericFactory<T extends Entity> {

    /**
     * Creates and returns a fully initialised entity positioned at (x,&nbsp;y).
     *
     * @param x spawn x-coordinate
     * @param y spawn y-coordinate
     * @return a new, ready-to-use entity instance
     */
    T create(float x, float y);
}
