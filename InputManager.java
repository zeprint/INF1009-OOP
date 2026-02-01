package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Single InputManager:
 * - polls input (update)
 * - exposes axes + actions + mouse
 * - owns AudioManager (I/O)
 *
 * IMPORTANT:
 * - NO key bindings are hardcoded here.
 * - Bindings are provided by GameMaster (test harness).
 */
public class InputManager {

    private final InputBindings bindings;

    private final ObjectMap<InputAxis, Float> axisValues = new ObjectMap<>();
    private final ObjectMap<InputAction, Boolean> actionTriggered = new ObjectMap<>();

    private final AudioManager audio = new AudioManager();

    // optional state flags (not tied to any keys here)
    private boolean mouseModeEnabled = false;
    private boolean muted = false;

    public InputManager(InputBindings bindings) {
        this.bindings = bindings;

        for (InputAxis a : InputAxis.values()) axisValues.put(a, 0f);
        for (InputAction ac : InputAction.values()) actionTriggered.put(ac, false);
    }

    /** Poll input once per frame */
    public void update() {
        // Axes
        for (InputAxis axis : InputAxis.values()) {
            float v = 0f;

            // ✅ FIXED: iterate KeyPair, not int[]
            for (InputBindings.KeyPair pair : bindings.getAxisPairs(axis)) {
                int neg = pair.negativeKey;
                int pos = pair.positiveKey;

                if (Gdx.input.isKeyPressed(neg)) v -= 1f;
                if (Gdx.input.isKeyPressed(pos)) v += 1f;
            }

            if (v > 1f) v = 1f;
            if (v < -1f) v = -1f;

            axisValues.put(axis, v);
        }

        // Actions (just pressed)
        for (InputAction action : InputAction.values()) {
            Integer key = bindings.getActionKey(action);
            boolean pressed = (key != null) && Gdx.input.isKeyJustPressed(key);
            actionTriggered.put(action, pressed);
        }
    }

    public float getAxis(InputAxis axis) {
        Float v = axisValues.get(axis);
        return v == null ? 0f : v;
    }

    public boolean isActionTriggered(InputAction action) {
        Boolean v = actionTriggered.get(action);
        return v != null && v;
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    public AudioManager getAudio() {
        return audio;
    }

    public boolean isMouseModeEnabled() {
        return mouseModeEnabled;
    }

    public void setMouseModeEnabled(boolean enabled) {
        this.mouseModeEnabled = enabled;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void dispose() {
        audio.dispose();
    }
}
