package io.github.some_example_name.lwjgl3.logic.movement;

// Toggleable state - used by Dodge Movement to turn dodge state on/off while the key is held

public interface StateTarget {

    void setStateActive(boolean active);
}
