package io.github.mathdash.logic.state;

import io.github.mathdash.engine.state.GameState;

/**
 * PlayingState - Active gameplay state.
 * Design Pattern: State (concrete state).
 *
 * Delegates per-frame logic to the GameScene's gameplay methods.
 */
public class PlayingState implements GameState {

    /** Callback so the state can trigger scene-level gameplay logic. */
    public interface PlayingCallback {
        void onPlayingUpdate(float deltaTime);
    }

    private final PlayingCallback callback;

    public PlayingState(PlayingCallback callback) {
        this.callback = callback;
    }

    @Override
    public void enter() {
        // Nothing special on enter - gameplay resumes
    }

    @Override
    public void update(float deltaTime) {
        if (callback != null) {
            callback.onPlayingUpdate(deltaTime);
        }
    }

    @Override
    public void exit() {
        // Nothing special on exit
    }

    @Override
    public String getName() {
        return "playing";
    }
}
