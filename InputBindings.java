package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class InputBindings {

    // Axis -> list of key pairs (neg, pos)
    private final ObjectMap<InputAxis, Array<KeyPair>> axisPairs = new ObjectMap<>();

    // Action -> keycode
    private final ObjectMap<InputAction, Integer> actionKeys = new ObjectMap<>();

    public static class KeyPair {
        public int negativeKey;
        public int positiveKey;

        public KeyPair(int negativeKey, int positiveKey) {
            this.negativeKey = negativeKey;
            this.positiveKey = positiveKey;
        }
    }

    /** Allow multiple bindAxis calls for same axis (adds another pair). */
    public void bindAxis(InputAxis axis, int negativeKey, int positiveKey) {
        Array<KeyPair> list = axisPairs.get(axis);
        if (list == null) {
            list = new Array<>();
            axisPairs.put(axis, list);
        }
        list.add(new KeyPair(negativeKey, positiveKey));
    }

    /** Get all key pairs bound to this axis (may be empty). */
    public Array<KeyPair> getAxisPairs(InputAxis axis) {
        Array<KeyPair> list = axisPairs.get(axis);
        if (list == null) return new Array<>();
        return list;
    }

    /** Bind a single key to an action. */
    public void bindAction(InputAction action, int keycode) {
        actionKeys.put(action, keycode);
    }

    /** Returns the key for the action, or null if unbound. */
    public Integer getActionKey(InputAction action) {
        return actionKeys.get(action);
    }
}
