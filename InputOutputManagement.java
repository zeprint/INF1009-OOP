package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InputOutputManagement {
    private final InputHandler input;
    private final AudioManager audio;

    public InputOutputManagement() {
        input = new InputHandler();
        audio = new AudioManager();
    }

    // LibGDX polls automatically; keep for UML consistency
    public void updateInput() {}

    public boolean isKeyDown(String key) {
        return input.isKeyDown(key);
    }

    public boolean isKeyJustPressed(String key) {
        return input.isKeyJustPressed(key);
    }

    public float getMouseX() { return input.getMouseX(); }
    public float getMouseY() { return input.getMouseY(); }

    public AudioManager getAudio() { return audio; }

    // Helper draw wrapper (optional)
    public void begin(SpriteBatch batch) { batch.begin(); }
    public void end(SpriteBatch batch) { batch.end(); }

    public void dispose() {
        audio.dispose();
    }
}
