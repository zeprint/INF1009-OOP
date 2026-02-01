package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private final Map<String, Sound> sounds = new HashMap<>();
    private float volume = 1.0f;
    private boolean muted = false;

    public void loadSound(String name, String pathInAssets) {
        if (sounds.containsKey(name)) return; // avoid reloading / leaks
        Sound s = Gdx.audio.newSound(Gdx.files.internal(pathInAssets));
        sounds.put(name, s);
    }

    public void playSound(String name) {
        if (muted) return;
        Sound s = sounds.get(name);
        if (s == null) return;
        s.play(volume);
    }

    public void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        volume = v;
    }

    public float getVolume() {
        return volume;
    }

    public void setMuted(boolean m) {
        muted = m;
    }

    public boolean isMuted() {
        return muted;
    }

    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
