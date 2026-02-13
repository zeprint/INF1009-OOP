package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * SceneManager - Registers, switches, and forwards updates/renders to scenes (SRP).
 *
 * Implements ISceneSystem so callers depend on the abstraction (DIP).
 */
public class SceneManager implements ISceneSystem {

    private final ObjectMap<String, Scene>   scenes;
    private final ObjectMap<String, Boolean> createdScenes;
    private Scene currentScene;

    public SceneManager() {
        scenes        = new ObjectMap<>();
        createdScenes = new ObjectMap<>();
        currentScene  = null;
    }

    // --- Registration ---

    @Override
    public boolean addScene(String name, Scene scene) {
        if (name == null || scene == null || scenes.containsKey(name)) return false;
        scene.setName(name);
        scenes.put(name, scene);
        return true;
    }

    /** Unregister and dispose a scene. Clears currentScene if it matches. */
    public boolean removeScene(String name) {
        if (!scenes.containsKey(name)) return false;
        Scene removed = scenes.get(name);
        if (removed == currentScene) currentScene = null;
        removed.dispose();
        scenes.remove(name);
        createdScenes.remove(name);
        return true;
    }

    // --- Scene switching ---

    @Override
    public boolean loadScene(String name) {
        if (!scenes.containsKey(name)) return false;

        if (currentScene != null) currentScene.pause();

        currentScene = scenes.get(name);
        if (currentScene != null) {
            // Only call create() the first time a scene is loaded
            if (!createdScenes.containsKey(name)) {
                currentScene.create();
                createdScenes.put(name, true);
            }
            currentScene.resume();
            return true;
        }
        return false;
    }

    // --- Per-frame delegation ---

    @Override
    public boolean update(float deltaTime) {
        if (currentScene != null && !currentScene.isPaused()) {
            return currentScene.update(deltaTime);
        }
        return false;
    }

    @Override
    public boolean render() {
        if (currentScene != null) return currentScene.render();
        return false;
    }

    // --- Shutdown ---

    @Override
    public void dispose() {
        for (Scene scene : scenes.values()) {
            if (scene != null) scene.dispose();
        }
        scenes.clear();
        createdScenes.clear();
        currentScene = null;
    }

    // --- Query ---

    @Override
    public Scene   getCurrentScene()       { return currentScene; }
    public boolean hasScene(String name)   { return name != null && scenes.containsKey(name); }
}
