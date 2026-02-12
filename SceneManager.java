package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SceneManager {
    private Scene currentScene;
    private Scene backgroundScene; // Holds the game scene when paused
    private Map<String, Scene> scenes;
    public SpriteBatch batch;

    public SceneManager() {
        this.scenes = new HashMap<>();
        this.currentScene = null;
        this.backgroundScene = null;
        this.batch = new SpriteBatch();
    }

    public void addScene(String name, Scene scene) {
        if (scenes.containsKey(name))
            return;
        scene.setSceneManager(this);
        scene.create(); // Initialize the scene immediately upon adding
        scenes.put(name, scene);
    }

    public void loadScene(String name) {
        Scene nextScene = scenes.get(name);
        if (nextScene == null) {
            Gdx.app.error("SceneManager", "Scene not found: " + name);
            return;
        }

        // 1. If switching TO pause, keep the current simulation visible in background
        if (name.equals("pause") && currentScene != null) {
            backgroundScene = currentScene;
            backgroundScene.pause();
        }
        // 2. If returning TO simulation (or any other scene), clear background
        else {
            backgroundScene = null;
        }

        currentScene = nextScene;
        currentScene.resume();

        // Ensure viewport is correct after switch
        currentScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void update(float dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public void render() {
        // 1. Draw Background Scene (e.g., Frozen Simulation)
        if (backgroundScene != null) {
            backgroundScene.render(batch);
        }

        // 2. Draw Current Scene (e.g., Pause Menu)
        if (currentScene != null) {
            currentScene.render(batch);
        }
    }

    public void dispose() {
        for (Scene scene : scenes.values()) {
            scene.dispose();
        }
        scenes.clear();
        if (batch != null)
            batch.dispose();
    }
}
