package io.github.mathdash.AbstractEngine.inputouput;

/**
 * IAudioSystem - Contract for the engine's audio playback system.
 * Supports both short sound effects (Sound) and background music (Music).
 */
public interface IAudioSystem {

    // --- Sound Effects (short clips) ---
    void loadSound(String name, String assetPath);
    void playSound(String name);
    void stopSound(String name);

    // --- Background Music (long streaming audio) ---
    void loadMusic(String name, String assetPath);
    void playMusic(String name, boolean loop);
    void stopMusic(String name);
    void pauseMusic(String name);
    void resumeMusic(String name);

    // --- Volume & Mute ---
    void setMuted(boolean muted);
    boolean isMuted();
    void setVolume(float volume);
    float getVolume();

    void dispose();
}
