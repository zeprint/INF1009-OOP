package io.github.mathdash.engine;

import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.engine.inputoutput.IInputSystem;

/**
 * ServiceLocator - Provides global access to shared engine services
 * without tight coupling to concrete implementations.
 */
public final class ServiceLocator {

    private static IAudioSystem audioSystem;
    private static IInputSystem inputSystem;

    private ServiceLocator() {
        // Non-instantiable utility class
    }

    // ---- Audio ----

    /** Registers the application-wide audio system. */
    public static void provide(IAudioSystem audio) {
        ServiceLocator.audioSystem = audio;
    }

    /** Returns the registered audio system, or null if none has been provided. */
    public static IAudioSystem getAudio() {
        return audioSystem;
    }

    // ---- Input ----

    /** Registers the application-wide input system. */
    public static void provide(IInputSystem input) {
        ServiceLocator.inputSystem = input;
    }

    /** Returns the registered input system, or null if none has been provided. */
    public static IInputSystem getInput() {
        return inputSystem;
    }

    // ---- Cleanup ----

    /** Clears all registered services. Call during application shutdown. */
    public static void reset() {
        audioSystem = null;
        inputSystem = null;
    }
}
