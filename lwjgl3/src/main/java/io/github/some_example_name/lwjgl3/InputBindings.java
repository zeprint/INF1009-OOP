package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * InputBindings - Maps abstract axes and actions to physical key codes.
 *
 * Supports multiple key bindings per axis/action (e.g. A/D and Left/Right).
 *
 * Defensive error handling:
 * - Ignore null axis/action.
 * - Ignore invalid (negative) keycodes.
 * - Optional warning logs for misconfiguration.
 */
public class InputBindings {

    /** A negative/positive key pair for one axis binding. */
    public static class AxisPair {
        public int negativeKey;
        public int positiveKey;

        public AxisPair(int negativeKey, int positiveKey) {
            this.negativeKey = negativeKey;
            this.positiveKey = positiveKey;
        }
    }

    private final ObjectMap<InputAxis, Array<AxisPair>> axisPairs = new ObjectMap<>();
    private final ObjectMap<InputAction, Array<Integer>> actionKeys = new ObjectMap<>();

    /** Toggle warnings for bad bindings (optional). */
    private boolean warningsEnabled = true;

    /** Enable/disable warning logs (does not affect behaviour). */
    public void setWarningsEnabled(boolean enabled) {
        this.warningsEnabled = enabled;
    }

    /** Bind an additional negative/positive key pair to an axis. */
    public void bindAxis(InputAxis axis, int negativeKey, int positiveKey) {
        if (axis == null) {
            warn("bindAxis called with null axis");
            return;
        }

        // LibGDX keycodes are non-negative (Input.Keys.*). Negative usually means
        // invalid.
        if (negativeKey < 0 || positiveKey < 0) {
            warn("bindAxis(" + axis + ") invalid keycode(s): neg=" + negativeKey + ", pos=" + positiveKey);
            return;
        }

        Array<AxisPair> pairs = axisPairs.get(axis);
        if (pairs == null) {
            pairs = new Array<>();
            axisPairs.put(axis, pairs);
        }

        pairs.add(new AxisPair(negativeKey, positiveKey));
    }

    /** Bind an additional key to an action. */
    public void bindAction(InputAction action, int key) {
        if (action == null) {
            warn("bindAction called with null action");
            return;
        }

        if (key < 0) {
            warn("bindAction(" + action + ") invalid keycode: " + key);
            return;
        }

        Array<Integer> keys = actionKeys.get(action);
        if (keys == null) {
            keys = new Array<>();
            actionKeys.put(action, keys);
        }

        
        for (int i = 0; i < keys.size; i++) {
            Integer existing = keys.get(i);
            if (existing != null && existing == key) {
                warn("bindAction(" + action + ") duplicate key ignored: " + key);
                return;
            }
        }

        keys.add(key);
    }

    public Array<AxisPair> getAxisPairs(InputAxis axis) {
        if (axis == null)
            return new Array<>();
        Array<AxisPair> pairs = axisPairs.get(axis);
        return (pairs != null) ? pairs : new Array<>();
    }

    public Array<Integer> getActionKeys(InputAction action) {
        if (action == null)
            return new Array<>();
        Array<Integer> keys = actionKeys.get(action);
        return (keys != null) ? keys : new Array<>();
    }

    private void warn(String msg) {
        if (!warningsEnabled)
            return;

        // Safe logging: won't crash even if Gdx.app isn't ready
        try {
            if (Gdx.app != null) {
                Gdx.app.log("InputBindings", "WARN: " + msg);
            } else {
                System.out.println("InputBindings WARN: " + msg);
            }
        } catch (Exception ignored) {
            // do nothing
        }
    }
}
