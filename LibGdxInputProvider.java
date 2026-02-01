package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class LibGdxInputProvider implements InputProvider {

    private final InputBindings bindings;

    public LibGdxInputProvider(InputBindings bindings) {
        this.bindings = bindings;
    }

    @Override
    public void update() {
        // LibGDX input is polled; nothing required here.
    }

    @Override
    public float getAxis(InputAxis axis) {
        int[] keys = bindings.getAxisKeys(axis);
        if (keys == null) return 0f;

        int neg = keys[0];
        int pos = keys[1];

        float v = 0f;
        if (neg != Input.Keys.UNKNOWN && Gdx.input.isKeyPressed(neg)) v -= 1f;
        if (pos != Input.Keys.UNKNOWN && Gdx.input.isKeyPressed(pos)) v += 1f;

        return v;
    }

    @Override
    public boolean isActionTriggered(InputAction action) {
        Integer key = bindings.getActionKey(action);
        if (key == null) return false;
        return Gdx.input.isKeyJustPressed(key);
    }

    @Override
    public boolean isActionDown(InputAction action) {
        Integer key = bindings.getActionKey(action);
        if (key == null) return false;
        return Gdx.input.isKeyPressed(key);
    }

    @Override
    public float getMouseX() {
        return Gdx.input.getX();
    }

    @Override
    public float getMouseY() {
        // Convert LibGDX screen coords to world coords (0 at bottom)
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }
}
