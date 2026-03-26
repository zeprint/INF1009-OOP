package io.github.mathdash.engine.entity;

/**
 * GenericFactory - Abstract interface for creating pre-configured entities.
 */
public interface GenericFactory<T extends Entity> {
    T create(float x, float y);
}
