package io.github.some_example_name.lwjgl3;

/**
 * IAudioSystem - Contract for the engine's audio playback system.
 */
public interface IAudioSystem {

    void loadSound(String name, String assetPath);

    void playSound(String name);

    void setMuted(boolean muted);

    boolean isMuted();

    void setVolume(float volume);

    void dispose();
}
