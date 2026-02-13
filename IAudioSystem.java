package io.github.some_example_name.lwjgl3;

public interface IAudioSystem {
    void loadSound(String name, String assetPath);

    void playSound(String name);

    void setMuted(boolean muted);

    boolean isMuted();

    void setVolume(float v);

    void dispose();
}
