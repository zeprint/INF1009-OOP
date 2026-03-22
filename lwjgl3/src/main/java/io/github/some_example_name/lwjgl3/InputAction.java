package io.github.some_example_name.lwjgl3;

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
    CONFIRM,
    SHIELD,
    BACK_TO_MENU
}
