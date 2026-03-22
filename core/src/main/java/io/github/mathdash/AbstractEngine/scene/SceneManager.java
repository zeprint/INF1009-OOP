package io.github.mathdash.AbstractEngine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * SceneManager - Manages loading, unloading, and transitioning between scenes.
 * Only one scene is active at a time. Scenes are registered by name for lookup.
 */

public class SceneManager {

    private final ObjectMap<String, Scene> scenes;
    private Scene currentScene;

    // Creates a new, empty SceneManager.
    public SceneManager() {
        this.scenes = new ObjectMap<>();
        this.currentScene = null;
    }

    // Registers a scene with the manager. The scene's name is used as the key.
    public void addScene(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Cannot add a null scene.");
        }
        if (scenes.containsKey(scene.getName())) {
            throw new IllegalArgumentException(
                "A scene with name '" + scene.getName() + "' is already registered."
            );
        }
        scenes.put(scene.getName(), scene);
    }

    // Removes a scene from the manager. If it is the current scene, it is hidden and unloaded first.
    public void removeScene(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) return;

        if (scene == currentScene) {
            currentScene.hide();
            currentScene.unload();
            currentScene = null;
        }

        if (scene.isLoaded()) {
            scene.unload();
        }
        scenes.remove(name);
    }

    // Transitions to the scene with the given name.
    // The current scene is hidden, the target scene is loaded (if not already) and shown.
    public void setScene(String name) {
        Scene next = scenes.get(name);
        if (next == null) {
            throw new IllegalArgumentException("No scene registered with name '" + name + "'.");
        }

        // Hide and (optionally) unload the current scene
        if (currentScene != null) {
            currentScene.hide();
        }

        // Load and show the next scene
        if (!next.isLoaded()) {
            next.load();
        }
        next.show();
        currentScene = next;
    }

    // Updates the current scene.
    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }

    // Renders the current scene.
    public void render(SpriteBatch batch) {
        if (currentScene != null) {
            currentScene.render(batch);
        }
    }

    // Forwards a resize event to the current scene.
    public void resize(int width, int height) {
        if (currentScene != null) {
            currentScene.resize(width, height);
        }
    }

    // Disposes all scenes and clears the manager.
    public void dispose() {
        for (Scene scene : scenes.values()) {
            if (scene.isLoaded()) {
                scene.unload();
            }
        }
        scenes.clear();
        currentScene = null;
    }

    // Returns the currently active scene, or null if none.
    public Scene getCurrentScene() {
        return currentScene;
    }

    // Returns a registered scene by name, or null if not found.
    public Scene getScene(String name) {
        return scenes.get(name);
    }

    // Returns whether a scene with the given name is registered.
    public boolean hasScene(String name) {
        return scenes.containsKey(name);
    }
}
