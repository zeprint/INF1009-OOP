package io.github.mathdash.logic.entity;

import io.github.mathdash.AbstractEngine.entity.Entity;

/**
 * GenericFactory - Interface for creating pre-configured entities.
 * Design Pattern: Factory Method.
 *
 * @param <T> the concrete Entity type this factory produces
 */
public interface GenericFactory<T extends Entity> {
    T create(float x, float y);
}
