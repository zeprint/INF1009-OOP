package io.github.mathdash.engine.inputoutput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * AudioManager - Concrete implementation of IAudioSystem.
 * Handles both short sound effects (Sound) and background music (Music).
 */
public class AudioManager implements IAudioSystem {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private final ObjectMap<String, Music> musics = new ObjectMap<>();

    private float volume = 1.0f;
    private boolean muted = false;

    // --- Sound Effects ---

    @Override
    public void loadSound(String name, String assetPath) {
        Sound old = sounds.get(name);
        if (old != null) {
            old.dispose();
        }
        sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(assetPath)));
    }

    @Override
    public void playSound(String name) {
        if (muted) {
            return;
        }
        Sound s = sounds.get(name);
        if (s != null) {
            s.play(volume);
        }
    }

    @Override
    public void stopSound(String name) {
        Sound s = sounds.get(name);
        if (s != null) {
            s.stop();
        }
    }

    // --- Background Music ---

    @Override
    public void loadMusic(String name, String assetPath) {
        Music old = musics.get(name);
        if (old != null) {
            old.dispose();
        }
        musics.put(name, Gdx.audio.newMusic(Gdx.files.internal(assetPath)));
    }

    @Override
    public void playMusic(String name, boolean loop) {
        if (muted) {
            return;
        }
        Music m = musics.get(name);
        if (m != null) {
            m.setLooping(loop);
            m.setVolume(volume);
            m.play();
        }
    }

    @Override
    public void stopMusic(String name) {
        Music m = musics.get(name);
        if (m != null) {
            m.stop();
        }
    }

    @Override
    public void pauseMusic(String name) {
        Music m = musics.get(name);
        if (m != null) {
            m.pause();
        }
    }

    @Override
    public void resumeMusic(String name) {
        if (muted) {
            return;
        }
        Music m = musics.get(name);
        if (m != null){
            m.setVolume(volume);m.play();
        }
    }

    // --- Volume & Mute ---

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
        // Pause all music when muted
        if (muted) {
            for (Music m : musics.values()) {
                if (m.isPlaying()) {
                    m.pause();
                }
            }
        }
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));
        // Update all playing music volume
        for (Music m : musics.values()) {
            m.setVolume(volume);
        }
    }

    @Override
    public float getVolume() {
        return volume;
    }

    // --- Dispose ---

    @Override
    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
        for (Music m : musics.values()) {
            m.dispose();
        }
        musics.clear();
    }
}
