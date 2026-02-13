package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * InputManager - Processes keyboard/mouse input each frame via configurable bindings (SRP).
 *
 * Implements IInputSystem so callers depend on the abstraction (DIP).
 * Axes produce a float in [-1, 1]; actions produce a boolean (just-pressed).
 */
public class InputManager implements IInputSystem {

    private final InputBindings bindings;
    private final ObjectMap<InputAxis, Float> axisValues = new ObjectMap<>();
    private final ObjectMap<InputAction, Boolean>  actionTriggered = new ObjectMap<>();
    private boolean mouseMode = false;

    public InputManager(InputBindings bindings) {
        this.bindings = bindings;
        for (InputAxis axis   : InputAxis.values()) {
            axisValues.put(axis, 0f);
        }
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, false);
        }
    }

    @Override
    public void update() {
        for (InputAxis axis : InputAxis.values()) {
            axisValues.put(axis, computeAxis(axis));
        }
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, computeActionJustPressed(action));
        }
        if (isActionTriggered(InputAction.TOGGLE_MOUSE_MODE)) {
            mouseMode = !mouseMode;
        }
    }

    // --- Queries ---

    @Override
    public float getAxis(InputAxis axis) {
        Float v = axisValues.get(axis);
        return (v != null) ? v : 0f;
    }

    @Override
    public boolean isActionTriggered(InputAction action) {
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
        return Gdx.input.isKeyPressed(keycode);
    }

    @Override
    public void dispose() {
        /* no native resources */
    }

    // --- Internal ---

    private float computeAxis(InputAxis axis) {
        float v = 0f;
        for (InputBindings.AxisPair pair : bindings.getAxisPairs(axis)) {
            if (pair == null) {
                continue;
            }
            if (Gdx.input.isKeyPressed(pair.negativeKey)) {
                v -= 1f;
            }
            if (Gdx.input.isKeyPressed(pair.positiveKey)) {
                v += 1f;
            }
        }
        return Math.max(-1f, Math.min(1f, v));
    }

    private boolean computeActionJustPressed(InputAction action) {
        for (Integer key : bindings.getActionKeys(action)) {
            if (key != null && Gdx.input.isKeyJustPressed(key)) return true;
        }
        return false;
    }
}
