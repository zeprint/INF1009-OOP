package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * InputManager - Processes keyboard/mouse input each frame via configurable bindings (SRP).
 *
 * Implements IInputSystem so callers depend on the abstraction (DIP).
 * Axes produce a float in [-1, 1]; actions produce a boolean (just-pressed).
 *
 * Error handling (defensive):
 * - Reject null InputBindings at construction (programmer/config error).
 * - Safe fallbacks for null axis/action queries.
 * - Safe handling if bindings return null/empty arrays.
 * - Guards against invalid keycodes (negative).
 */
public class InputManager implements IInputSystem {

    private final InputBindings bindings;

    private final ObjectMap<InputAxis, Float> axisValues = new ObjectMap<>();
    private final ObjectMap<InputAction, Boolean> actionTriggered = new ObjectMap<>();

    private boolean mouseMode = false;

    // Set true if you want console logs for misconfiguration (optional)
    private static final boolean DEBUG_INPUT = false;

    public InputManager(InputBindings bindings) {
        if (bindings == null) {
            throw new IllegalArgumentException("InputBindings cannot be null");
        }
        this.bindings = bindings;

        // Initialise maps with defaults
        for (InputAxis axis : InputAxis.values()) {
            axisValues.put(axis, 0f);
        }
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, false);
        }
    }

    @Override
    public void update() {
        // Update axes
        for (InputAxis axis : InputAxis.values()) {
            axisValues.put(axis, computeAxis(axis));
        }

        // Update actions
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, computeActionJustPressed(action));
        }

        // Mouse mode toggle handled internally
        if (isActionTriggered(InputAction.TOGGLE_MOUSE_MODE)) {
            mouseMode = !mouseMode;
        }
    }

    // --- Queries ---

    @Override
    public float getAxis(InputAxis axis) {
        if (axis == null) return 0f; // defensive
        Float v = axisValues.get(axis);
        return (v != null) ? v : 0f;
    }

    @Override
    public boolean isActionTriggered(InputAction action) {
        if (action == null) return false; // defensive
        Boolean b = actionTriggered.get(action);
        return b != null && b;
    }

    public boolean isMouseMode() {
        return mouseMode;
    }

    /* Mouse X in screen coordinates. */
    public float getMouseX() {
        return Gdx.input.getX();
    }

    /* Mouse Y in world-style Y (0 at bottom). */
    public float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    /* Convenience: raw key check. */
    public boolean isKeyDown(int keycode) {
        if (keycode < 0) return false; // invalid keycode guard
        return Gdx.input.isKeyPressed(keycode);
    }

    @Override
    public void dispose() {
        /* no native resources */
    }

    // --- Internal ---

    private float computeAxis(InputAxis axis) {
        if (axis == null) return 0f;

        Array<InputBindings.AxisPair> pairs = bindings.getAxisPairs(axis);
        if (pairs == null || pairs.size == 0) {
            // Not bound -> default 0
            return 0f;
        }

        float v = 0f;

        for (int i = 0; i < pairs.size; i++) {
            InputBindings.AxisPair pair = pairs.get(i);
            if (pair == null) continue;

            // Keycode sanity (LibGDX uses non-negative keycodes)
            if (pair.negativeKey >= 0 && Gdx.input.isKeyPressed(pair.negativeKey)) {
                v -= 1f;
            } else if (pair.negativeKey < 0 && DEBUG_INPUT) {
    Gdx.app.log("InputManager", "Invalid negativeKey for axis " + axis + ": " + pair.negativeKey);
}

if (pair.positiveKey >= 0 && Gdx.input.isKeyPressed(pair.positiveKey)) {
    v += 1f;
} else if (pair.positiveKey < 0 && DEBUG_INPUT) {
    Gdx.app.log("InputManager", "Invalid positiveKey for axis " + axis + ": " + pair.positiveKey);
}

        }

        // Clamp to [-1, 1]
        if (v > 1f) v = 1f;
        if (v < -1f) v = -1f;
        return v;
    }

    private boolean computeActionJustPressed(InputAction action) {
        if (action == null) return false;

        Array<Integer> keys = bindings.getActionKeys(action);
        if (keys == null || keys.size == 0) {
            // Not bound -> default false
            return false;
        }

        for (int i = 0; i < keys.size; i++) {
            Integer key = keys.get(i);
            if (key == null) continue;

            int keycode = key;
            if (keycode < 0) {
                if (DEBUG_INPUT) {
                    Gdx.app.log("InputManager", "Invalid keycode for action " + action + ": " + keycode);
                }
                continue;
            }

            if (Gdx.input.isKeyJustPressed(keycode)) return true;
        }
        return false;
    }
}
