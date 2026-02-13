package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * AudioManager - Concrete implementation of IAudioSystem (DIP).
 *
 * Uses libGDX's Sound API for short one-shot effects.
 * Callers should depend on the IAudioSystem interface.
 */
public class AudioManager implements IAudioSystem {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private float   volume = 1.0f;
    private boolean muted  = false;

    @Override
    public void loadSound(String name, String assetPath) {
        Sound old = sounds.get(name);
        if (old != null) old.dispose();
        sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(assetPath)));
    }

    @Override
    public void playSound(String name) {
        if (muted) return;
        Sound s = sounds.get(name);
        if (s != null) s.play(volume);
    }

    @Override public void    setMuted(boolean muted) { this.muted = muted; }
    @Override public boolean isMuted()               { return muted;       }

    @Override
    public void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));
    }

    @Override
    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }

    /** @return current master volume in [0.0, 1.0]. */
    public float getVolume() { return volume; }
}
