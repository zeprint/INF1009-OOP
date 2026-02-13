package io.github.some_example_name.lwjgl3;

/**
 * IAudioSystem - Contract for the engine's audio playback system (DIP).
 *
 * Callers depend on this interface rather than AudioManager directly,
 * allowing the audio back-end to be swapped (e.g. null implementation
 * for headless tests) without touching call sites.
 *
 * Implementing class: AudioManager
 */
public interface IAudioSystem {

    /** Load a named sound from the given internal asset path. */
    void loadSound(String name, String assetPath);

    /** Play a previously loaded sound at the current volume. */
    void playSound(String name);

    /** Mute or unmute all audio output. */
    void setMuted(boolean muted);

    /** @return true if audio is currently muted. */
    boolean isMuted();

    /** Set the global playback volume, clamped to [0, 1]. */
    void setVolume(float volume);

    /** Release all loaded sound resources. */
    void dispose();
}
