package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
* Manage scene registration, switching and caching
* Implement ISceneSystem to satisfy DIP and allows for dependency injection
* Optimise game performance through caching and enforces SRP by delegating logic
* and rendering to individual scenes
* Prevents engine crashes with error handling.
*/
public class SceneManager implements ISceneSystem {

    private static final String TAG = "SceneManager"; // for logging

    private final ObjectMap<String, Scene> scenes;
    private final ObjectMap<String, Boolean> createdScenes;
    private Scene currentScene;

    // constructor initialise the scene manager with empty object maps and null current scene
    public SceneManager() {
        scenes = new ObjectMap<>();
        createdScenes = new ObjectMap<>();
        currentScene = null;
    }

    // ensure that scene names and scene objects are valid and prevent dups

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

    // scene switching with caching to prevent redundant amt of create() calls and improve performance
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

    // update and render forward to current scene if it exist and is not paused, error handling to prevent engine crash
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

    // two-pass rendering to ensure clean state transition between SpriteBatch and ShapeRenderer
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

    // dispose all scenes and clear caches

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

    // GameMaster get current scene and check if a scene exist by name for scene switching
    @Override
    public Scene getCurrentScene() {
        return currentScene;
    }

    public boolean hasScene(String name) {
        return name != null && scenes.containsKey(name);
    }
}
