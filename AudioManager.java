package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioManager {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private float volume = 1.0f;
    private boolean muted = false;

    public void loadSound(String name, String assetPath) {
        Sound old = sounds.get(name);
        if (old != null) old.dispose();

        Sound s = Gdx.audio.newSound(Gdx.files.internal(assetPath));
        sounds.put(name, s);
    }

    public void playSound(String name) {
        if (muted) return;

        Sound s = sounds.get(name);
        if (s == null) return;

        s.play(volume);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        volume = v;
    }

    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
