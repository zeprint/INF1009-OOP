package io.github.mathdash.AbstractEngine.entity;

/**
 * GenericFactory - Abstract interface for creating pre-configured entities.
 * Design Pattern: Factory Method.
 *
 * Placed in the engine layer so it can be reused across any game built
 * on this engine, without coupling to game-specific entity types.
 *
 * @param <T> the concrete Entity type this factory produces
 */
public interface GenericFactory<T extends Entity> {
    T create(float x, float y);
}
