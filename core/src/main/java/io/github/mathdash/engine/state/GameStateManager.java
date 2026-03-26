package io.github.mathdash.engine.state;

import java.util.HashMap;
import java.util.Map;

/**
 * GameStateManager - Manages registration and transitions between GameStates.
 */
public class GameStateManager {

    private final Map<String, GameState> states;
    private GameState currentState;

    /** Creates a new, empty GameStateManager. */
    public GameStateManager() {
        this.states = new HashMap<>();
        this.currentState = null;
    }

    public void addState(GameState state) {
        if (state == null) {
            throw new IllegalArgumentException("Cannot add a null GameState.");
        }
        if (states.containsKey(state.getName())) {
            throw new IllegalArgumentException(
                "A state with name '" + state.getName() + "' is already registered."
            );
        }
        states.put(state.getName(), state);
    }

    public void setState(String name) {
        GameState next = states.get(name);
        if (next == null) {
            throw new IllegalArgumentException("No GameState registered with name '" + name + "'.");
        }

        if (currentState != null) {
            currentState.exit();
        }

        currentState = next;
        currentState.enter();
    }

    /** Delegates the per-frame update to the current state. */
    public void update(float deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
        }
    }

    /** Returns the currently active state, or null if none. */
    public GameState getCurrentState() {
        return currentState;
    }

    /** Returns the name of the current state, or null if none. */
    public String getCurrentStateName() {
        return currentState != null ? currentState.getName() : null;
    }

    /** Returns whether a state with the given name is registered. */
    public boolean hasState(String name) {
        return states.containsKey(name);
    }
}
