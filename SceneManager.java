package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

//SceneManager - Manages all scenes in the game
//Handles scene switching, updating, and rendering
public class SceneManager {
    private Scene currentScene;
    private Map<String, Scene> scenes;

    // Constructor for SceneManager
    public SceneManager() {
        this.scenes = new HashMap<String, Scene>();
        this.currentScene = null;
    }

    // Add a scene to the manager
    // @param name Name identifier for the scene
    // @param scene The scene to add
    // @return true if successfully added, false if name already exists
    public boolean addScene(String name, Scene scene) {
        if (scenes.containsKey(name)) {
            return false;
        }
        scene.setName(name);
        scenes.put(name, scene);
        return true;
    }

    // Load and switch to a scene
    // @param name Name of the scene to load
    // @return true if successfully loaded, false if scene doesn't exist
    public boolean loadScene(String name) {
        if (!scenes.containsKey(name)) {
            return false;
        }

        // Pause current scene if it exists
        if (currentScene != null) {
            currentScene.pause();
        }

        // Switch to new scene
        currentScene = scenes.get(name);

        // Create the scene if it hasn't been created yet
        if (currentScene != null) {
            currentScene.create();
            currentScene.resume();
            return true;
        }

        return false;
    }

    // Get the current active scene
    // @return Current scene, or null if no scene is loaded
    public Scene getCurrentScene() {
        return currentScene;
    }

    // Update the current scene
    // @param deltaTime Delta time in seconds
    // @return true if successful, false otherwise
    public boolean update(float deltaTime) {
        if (currentScene != null && !currentScene.isPaused()) {
            return currentScene.update(deltaTime);
        }
        return false;
    }

    // Render the current scene
    // @return true if successful, false otherwise
    public boolean render() {
        if (currentScene != null) {
            return currentScene.render();
        }
        return false;
    }

    // Dispose of all scenes and cleanup resources
    public void dispose() {
        for (Scene scene : scenes.values()) {
            if (scene != null) {
                scene.dispose();
            }
        }
        scenes.clear();
        currentScene = null;
    }
}
