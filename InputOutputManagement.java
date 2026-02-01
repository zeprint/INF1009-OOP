package io.github.some_example_name.lwjgl3;

public class InputOutputManagement {

    private final InputProvider input;
    private final AudioManager audio;

    public InputOutputManagement(InputProvider input) {
        this.input = input;
        this.audio = new AudioManager();
    }

    public void update() {
        input.update();
    }

    public float getAxis(InputAxis axis) {
        return input.getAxis(axis);
    }

    public boolean isActionTriggered(InputAction action) {
        return input.isActionTriggered(action);
    }

    public boolean isActionDown(InputAction action) {
        return input.isActionDown(action);
    }

    public float getMouseX() {
        return input.getMouseX();
    }

    public float getMouseY() {
        return input.getMouseY();
    }

    public AudioManager getAudio() {
        return audio;
    }

    public void dispose() {
        audio.dispose();
    }
}
