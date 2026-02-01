package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class InputBindings {

    // Small helper class: one axis pair (neg, pos)
    public static class AxisPair {
        public int negativeKey;
        public int positiveKey;

        public AxisPair(int negativeKey, int positiveKey) {
            this.negativeKey = negativeKey;
            this.positiveKey = positiveKey;
        }
    }

    // Axis -> list of pairs (so you can bind A/D AND Left/Right)
    private final ObjectMap<InputAxis, Array<AxisPair>> axisPairs = new ObjectMap<>();

    // Action -> list of keys (so you can bind multiple keys to one action)
    private final ObjectMap<InputAction, Array<Integer>> actionKeys = new ObjectMap<>();

    public InputBindings() {}

    // Adds another pair to the axis
    public void bindAxis(InputAxis axis, int negativeKey, int positiveKey) {
        Array<AxisPair> pairs = axisPairs.get(axis);
        if (pairs == null) {
            pairs = new Array<>();
            axisPairs.put(axis, pairs);
        }
        pairs.add(new AxisPair(negativeKey, positiveKey));
    }

    // Adds another key to the action
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
        return (pairs == null) ? new Array<AxisPair>() : pairs;
    }

    public Array<Integer> getActionKeys(InputAction action) {
        Array<Integer> keys = actionKeys.get(action);
        return (keys == null) ? new Array<Integer>() : keys;
    }
}
