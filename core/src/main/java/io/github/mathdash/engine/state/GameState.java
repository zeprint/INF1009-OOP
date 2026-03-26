package io.github.mathdash.engine.state;

/**
 * GameState - Abstract interface for the State design pattern.
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
