package io.github.mathdash.logic.state;

import io.github.mathdash.engine.state.GameState;

/**
 * GameOverState - State entered when the player has lost all lives.
 *
 * Halts all gameplay updates and triggers the scene transition to
 * the death screen via a callback.
 */
public class GameOverState implements GameState {

    /** Callback so the state can trigger the death transition. */
    public interface GameOverCallback {
        void onGameOver();
    }

    private final GameOverCallback callback;
    private boolean fired = false;

    public GameOverState(GameOverCallback callback) {
        this.callback = callback;
    }

    @Override
    public void enter() {
        if (!fired && callback != null) {
            fired = true;
            callback.onGameOver();
        }
    }

    @Override
    public void update(float deltaTime) {
        // No updates in game-over state - waiting for scene transition
    }

    @Override
    public void exit() {
        fired = false;
    }

    @Override
    public String getName() {
        return "gameover";
    }
}
