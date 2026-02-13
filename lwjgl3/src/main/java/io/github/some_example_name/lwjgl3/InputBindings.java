package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * InputBindings - Maps abstract axes and actions to physical key codes.
 *
 * Supports multiple key bindings per axis/action (e.g. A/D and Left/Right).
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

    /* Bind an additional negative/positive key pair to an axis. */
    public void bindAxis(InputAxis axis, int negativeKey, int positiveKey) {
        Array<AxisPair> pairs = axisPairs.get(axis);
        if (pairs == null) {
            pairs = new Array<>();
            axisPairs.put(axis, pairs);
        }
        pairs.add(new AxisPair(negativeKey, positiveKey));
    }

    /** Bind an additional key to an action. */
    public void bindAction(InputAction action, int key) {
        Array<Integer> keys = actionKeys.get(action);
        if (keys == null) {
            keys = new Array<>();
            actionKeys.put(action, keys);
        }
        keys.add(key);
    }

    public Array<AxisPair> getAxisPairs(InputAxis axis) {
        Array<AxisPair> pairs = axisPairs.get(axis);
        return (pairs != null) ? pairs : new Array<AxisPair>();
    }

    public Array<Integer> getActionKeys(InputAction action) {
        Array<Integer> keys = actionKeys.get(action);
        return (keys != null) ? keys : new Array<Integer>();
    }
}
