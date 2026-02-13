package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioManager implements IAudioSystem {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private float volume = 1.0f;
    private boolean muted = false;

    @Override
    public void loadSound(String name, String assetPath) {
        Sound old = sounds.get(name);
        if (old != null) old.dispose();

        Sound s = Gdx.audio.newSound(Gdx.files.internal(assetPath));
        sounds.put(name, s);
    }

    @Override
    public void playSound(String name) {
        if (muted) return;

        Sound s = sounds.get(name);
        if (s == null) return;

        s.play(volume);
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        volume = v;
    }

    @Override
    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
