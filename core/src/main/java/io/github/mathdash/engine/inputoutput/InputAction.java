package io.github.mathdash.engine.inputoutput;

/**
 * InputAction - Abstract action identifiers for the input system.
 * Scenes query InputManager for these actions to implement their logic.
 */
public enum InputAction {
    TOGGLE_MOUSE_MODE,
    TOGGLE_DEBUG,
    TOGGLE_MUTE,
    TOGGLE_PAUSE,
    JUMP,
    CONFIRM
}
