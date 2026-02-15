package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
* SceneManager - Registers, switches and forwards updates to scenes (SRP).
*
* Implements ISceneSystem so callers depend on the abstraction (DIP).
*/
public class SceneManager implements ISceneSystem {

    private static final String TAG = "SceneManager";

    private final ObjectMap<String, Scene> scenes;
    private final ObjectMap<String, Boolean> createdScenes;
    private Scene currentScene;

    public SceneManager() {
        scenes = new ObjectMap<>();
        createdScenes = new ObjectMap<>();
        currentScene = null;
    }

    // Registration

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

    // Unregister and dispose a scene. Clears currentScene if it matches
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
        }
        catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing scene: " + name, e);
        }

        scenes.remove(name);
        createdScenes.remove(name);
        return true;
    }

    // Scene switching

    @Override
    public boolean loadScene(String name) {
        //if (!scenes.containsKey(name)) return false;
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
            // Only call create() the first time a scene is loaded
            if (!createdScenes.containsKey(name)) {
                try {
                    if (!currentScene.create()) {
                        Gdx.app.error(TAG, "Scene create() failed for: " + name);
                        currentScene = null;
                        return false;
                    }
                } 
                catch (Exception e) {
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

    // Per-frame delegation

    @Override
    public boolean update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            Gdx.app.error(TAG, "update rejected invalid deltaTime: " + deltaTime);
            return false;
        }
        
        if (currentScene != null && !currentScene.isPaused()) {
            try {
                return currentScene.update(deltaTime);
            } 
            catch (Exception e) {
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
            } 
            catch (Exception e) {
                Gdx.app.error(TAG, "Error rendering scene!", e);
                return false;
            }
        }
        return false;
    }

    // Shutdown

    @Override
    public void dispose() {
        boolean allDisposeSucceeded = true; 
        for (Scene scene : scenes.values()) {
            try {
                if (scene != null) {
                    if (!scene.dispose()) {
                        allDisposeSucceeded = false;
                    }
                }
            } catch (Exception e) {
                Gdx.app.error(TAG, "Exception disposing scene: " + scene, e);
                allDisposeSucceeded = false;
            }
        }
        scenes.clear();
        createdScenes.clear();
        currentScene = null;
    }

    // Query

    @Override
    public Scene getCurrentScene() {
        return currentScene;
    }

    public boolean hasScene(String name) {
        return name != null && scenes.containsKey(name);
    }
}
