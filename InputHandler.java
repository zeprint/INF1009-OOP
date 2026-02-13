package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputHandler {

    public boolean isKeyDown(String key) {
        int code = toKeyCode(key);
        return code != -1 && Gdx.input.isKeyPressed(code);
    }

    public boolean isKeyJustPressed(String key) {
        int code = toKeyCode(key);
        return code != -1 && Gdx.input.isKeyJustPressed(code);
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    private int toKeyCode(String key) {
        if (key == null) return -1;
        key = key.toUpperCase();

        switch (key) {
            case "A": return Input.Keys.A;
            case "D": return Input.Keys.D;
            case "LEFT": return Input.Keys.LEFT;
            case "RIGHT": return Input.Keys.RIGHT;
            case "SPACE": return Input.Keys.SPACE;
            case "M": return Input.Keys.M;
            case "N": return Input.Keys.N;
            case "P": return Input.Keys.P;              // ✅ Resume key
            case "F2": return Input.Keys.F2;            // ✅ use F2
            case "ESC":
            case "ESCAPE": return Input.Keys.ESCAPE;    // ✅ Pause key
            default: return -1;
        }
    }
}
