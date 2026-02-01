package io.github.some_example_name.lwjgl3;

public class InputSystem {

    private final InputOutputManagement io;

    public InputSystem(InputOutputManagement io) {
        this.io = io;
    }

    public void update() {
        io.update();
    }

    public float readMoveX() {
        return io.getAxis(InputAxis.MOVE_X);
    }

    public float readMoveY() {
        return io.getAxis(InputAxis.MOVE_Y);
    }

    public boolean toggleMouseTriggered() {
        return io.isActionTriggered(InputAction.TOGGLE_MOUSE_MODE);
    }

    public boolean toggleDebugTriggered() {
        return io.isActionTriggered(InputAction.TOGGLE_DEBUG);
    }

    public float pointerX() {
        return io.getMouseX();
    }

    public float pointerY() {
        return io.getMouseY();
    }

    public AudioManager audio() {
        return io.getAudio();
    }
}
