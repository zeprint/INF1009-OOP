package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class Scene implements ISceneSystem {

    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected SceneManager sceneManager;
    protected boolean isPaused;

    public Scene() {
        camera = new OrthographicCamera();
        // Standard virtual resolution 800x600
        viewport = new FitViewport(800, 600, camera);
        viewport.apply();

        // Center camera
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        isPaused = false;
    }

    public void setSceneManager(SceneManager sm) {
        this.sceneManager = sm;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    // Default empty implementation for create/dispose if not overridden
    @Override
    public void create() {
    }

    @Override
    public void dispose() {
    }
}
