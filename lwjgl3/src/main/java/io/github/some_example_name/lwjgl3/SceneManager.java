package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * SceneManager - Manages scene registration, switching, and caching.
 *
 * Implements ISceneManager so concrete scenes depend on the interface,
 * not this class (DIP).
 *
 * Responsibilities (SRP):
 *   - Register and unregister named Scene instances
 *   - Switch between scenes with lazy initialisation (create-on-first-load)
 *   - Forward update() and render() calls to the current active scene
 *   - Dispose all scenes on shutdown
 *
 * Optimises game performance through caching and prevents engine crashes
 * with comprehensive error handling on every public entry point.
 */
public class SceneManager implements ISceneManager {

    private static final String TAG = "SceneManager";

    private final ObjectMap<String, Scene> scenes;
    private final ObjectMap<String, Boolean> createdScenes;
    private Scene currentScene;

    public SceneManager() {
        scenes = new ObjectMap<>();
        createdScenes = new ObjectMap<>();
        currentScene = null;
    }

    // ---- ISceneManager implementation ----

    @Override
    public boolean addScene(String name, Scene scene) {
        if (name == null) {
            Gdx.app.error(TAG, "addScene rejected null name!");
            return false;
        }
        if (scene == null) {
            Gdx.app.error(TAG, "addScene rejected null scene!");
            return false;
        }
        if (scenes.containsKey(name)) {
            Gdx.app.error(TAG, "addScene rejected duplicate scene: " + name);
            return false;
        }
        scene.setName(name);
        scenes.put(name, scene);
        return true;
    }

    @Override
    public boolean removeScene(String name) {
        if (name == null) {
            Gdx.app.error(TAG, "removeScene rejected null name!");
            return false;
        }
        if (!scenes.containsKey(name)) {
            Gdx.app.error(TAG, "removeScene could not find scene: " + name);
            return false;
        }

        Scene removed = scenes.get(name);

        if (removed == currentScene) {
            currentScene = null;
        }
        try {
            removed.dispose();
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing scene: " + name, e);
        }

        scenes.remove(name);
        createdScenes.remove(name);
        return true;
    }

    @Override
    public boolean loadScene(String name) {
        if (name == null) {
            Gdx.app.error(TAG, "loadScene rejected null name!");
            return false;
        }
        if (!scenes.containsKey(name)) {
            Gdx.app.error(TAG, "loadScene could not find scene: " + name);
            return false;
        }

        if (currentScene != null) {
            currentScene.pause();
        }
        currentScene = scenes.get(name);

        if (currentScene != null) {
            // Only call create() the first time a scene is loaded (caching)
            if (!createdScenes.containsKey(name)) {
                try {
                    if (!currentScene.create()) {
                        Gdx.app.error(TAG, "Scene create() failed for: " + name);
                        currentScene = null;
                        return false;
                    }
                } catch (Exception e) {
                    Gdx.app.error(TAG, "Exception creating scene: " + name, e);
                    currentScene = null;
                    return false;
                }
                createdScenes.put(name, true);
            }
            currentScene.resume();
            return true;
        }
        return false;
    }

    @Override
    public boolean update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            Gdx.app.error(TAG, "update rejected invalid deltaTime: " + deltaTime);
            return false;
        }

        if (currentScene != null && !currentScene.isPaused()) {
            try {
                return currentScene.update(deltaTime);
            } catch (Exception e) {
                Gdx.app.error(TAG, "Error updating scene!", e);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean render() {
        if (currentScene != null) {
            try {
                return currentScene.render();
            } catch (Exception e) {
                Gdx.app.error(TAG, "Error rendering scene!", e);
                return false;
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        for (Scene scene : scenes.values()) {
            try {
                if (scene != null) {
                    scene.dispose();
                }
            } catch (Exception e) {
                Gdx.app.error(TAG, "Exception disposing scene: " + scene, e);
            }
        }
        scenes.clear();
        createdScenes.clear();
        currentScene = null;
    }

    @Override
    public Scene getCurrentScene() {
        return currentScene;
    }

    @Override
    public Scene getScene(String name) {
        if (name == null) return null;
        return scenes.get(name);
    }

    @Override
    public boolean hasScene(String name) {
        return name != null && scenes.containsKey(name);
    }
}
