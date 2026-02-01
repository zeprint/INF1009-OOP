package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class InputManager {

    private final InputBindings bindings;

    // Stores computed values after update()
    private final ObjectMap<InputAxis, Float> axisValues = new ObjectMap<>();
    private final ObjectMap<InputAction, Boolean> actionTriggered = new ObjectMap<>();

    // Optional state flags (not specifying keys here, only toggling states)
    private boolean mouseMode = false;

    public InputManager(InputBindings bindings) {
        this.bindings = bindings;

        // init defaults
        for (InputAxis axis : InputAxis.values()) axisValues.put(axis, 0f);
        for (InputAction a : InputAction.values()) actionTriggered.put(a, false);
    }

    /** Call once per frame */
    public void update() {
        // 1) compute axes
        for (InputAxis axis : InputAxis.values()) {
            axisValues.put(axis, computeAxis(axis));
        }

        // 2) compute actions (just pressed)
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, computeActionJustPressed(action));
        }

        // 3) optional: flip internal state based on actions (still not binding keys here)
        if (isActionTriggered(InputAction.TOGGLE_MOUSE_MODE)) {
            mouseMode = !mouseMode;
        }
    }

    private float computeAxis(InputAxis axis) {
        float v = 0f;

        // supports multiple pairs (A/D + LEFT/RIGHT)
        for (InputBindings.AxisPair pair : bindings.getAxisPairs(axis)) {
            if (pair == null) continue;

            if (Gdx.input.isKeyPressed(pair.negativeKey)) v -= 1f;
            if (Gdx.input.isKeyPressed(pair.positiveKey)) v += 1f;
        }

        // clamp [-1, 1]
        if (v > 1f) v = 1f;
        if (v < -1f) v = -1f;
        return v;
    }

    private boolean computeActionJustPressed(InputAction action) {
        for (Integer key : bindings.getActionKeys(action)) {
            if (key == null) continue;
            if (Gdx.input.isKeyJustPressed(key)) return true;
        }
        return false;
    }

    public float getAxis(InputAxis axis) {
        Float v = axisValues.get(axis);
        return (v == null) ? 0f : v;
    }

    /** True only on the frame the key was pressed */
    public boolean isActionTriggered(InputAction action) {
        Boolean b = actionTriggered.get(action);
        return b != null && b;
    }

    public boolean isMouseMode() {
        return mouseMode;
    }

    /** Mouse position in world-style Y (0 at bottom) */
    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    /** Optional convenience */
    public boolean isKeyDown(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    public void dispose() {
        // nothing to dispose (Input has no resources)
    }
}
