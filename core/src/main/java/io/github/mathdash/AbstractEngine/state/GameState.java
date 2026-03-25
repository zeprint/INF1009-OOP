package io.github.mathdash.AbstractEngine.state;

/**
 * GameState - Abstract interface for the State design pattern.
 *
 * Each concrete state encapsulates behaviour for one distinct phase
 * of a scene (e.g. Playing, Paused, GameOver). The owning scene
 * delegates its update() call to the current GameState, and a
 * GameStateManager handles transitions between states.
 *
 * Design Pattern: State
 */
public interface GameState {

    /** Called once when this state becomes the current state. */
    void enter();

    /** Called every frame while this state is active. */
    void update(float deltaTime);

    /** Called once when transitioning away from this state. */
    void exit();

    /** Returns the unique name of this state (used as a lookup key). */
    String getName();
}
