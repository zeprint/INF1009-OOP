package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * GameMaster - Core game coordinator.
 * All specific game logic (droplets, bucket, etc.) has been moved to
 * SimulationScene.java to prevent double-rendering and adhere to
 * the Abstract Engine architecture.
 */
public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;

    @Override
    public void create() {
        // 1. Initialize the Engine's Scene System
        sceneManager = new SceneManager();

        // 2. Load the Simulation Logic (Where the droplets/bucket now live)
        sceneManager.addScene("simulation", new SimulationScene());
        sceneManager.addScene("pause", new PauseScene());

        // 3. Start the Simulation
        sceneManager.loadScene("simulation");
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Delegate drawing to the active Scene (SimulationScene)
        if (sceneManager != null) {
            float dt = Gdx.graphics.getDeltaTime();
            sceneManager.update(dt);
            sceneManager.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (sceneManager != null && sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (sceneManager != null) {
            sceneManager.dispose();
        }
    }
}
