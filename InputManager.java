package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class InputManager implements InputSystem {

    private final InputBindings bindings;

    private final ObjectMap<InputAxis, Float> axisValues = new ObjectMap<>();
    private final ObjectMap<InputAction, Boolean> actionTriggered = new ObjectMap<>();

    private boolean mouseMode = false;

    public InputManager(InputBindings bindings) {
        this.bindings = bindings;

        for (InputAxis axis : InputAxis.values()) axisValues.put(axis, 0f);
        for (InputAction a : InputAction.values()) actionTriggered.put(a, false);
    }

    /** Call once per frame */
    @Override
    public void update() {
        // axes
        for (InputAxis axis : InputAxis.values()) {
            axisValues.put(axis, computeAxis(axis));
        }

        // actions (just pressed)
        for (InputAction action : InputAction.values()) {
            actionTriggered.put(action, computeActionJustPressed(action));
        }

        // internal state toggles
        if (isActionTriggered(InputAction.TOGGLE_MOUSE_MODE)) {
            mouseMode = !mouseMode;
        }
    }

    private float computeAxis(InputAxis axis) {
        float v = 0f;

        for (InputBindings.AxisPair pair : bindings.getAxisPairs(axis)) {
            if (pair == null) continue;

            if (Gdx.input.isKeyPressed(pair.negativeKey)) v -= 1f;
            if (Gdx.input.isKeyPressed(pair.positiveKey)) v += 1f;
        }

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

    @Override
    public float getAxis(InputAxis axis) {
        Float v = axisValues.get(axis);
        return (v == null) ? 0f : v;
    }

    /** True only on the frame the key was pressed */
    @Override
    public boolean isActionTriggered(InputAction action) {
        Boolean b = actionTriggered.get(action);
        return b != null && b;
    }

    @Override
    public boolean isMouseMode() {
        return mouseMode;
    }

    /** screen coords (0,0 at bottom-left like world-style Y) */
    @Override
    public float getMouseX() {
        return Gdx.input.getX();
    }

    @Override
    public float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    @Override
    public void dispose() {
        // nothing
    }
}
