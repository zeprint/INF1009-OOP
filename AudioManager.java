package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private float volume = 1.0f;

    public void loadSound(String name, String pathInAssets) {
        Sound s = Gdx.audio.newSound(Gdx.files.internal(pathInAssets));
        sounds.put(name, s);
    }

    public void playSound(String name) {
        Sound s = sounds.get(name);
        if (s == null) return;
        s.play(volume);
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
