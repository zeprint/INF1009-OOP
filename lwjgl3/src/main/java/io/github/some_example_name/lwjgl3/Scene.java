package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;

/**
 * Scene - Abstract base class for all engine and game scenes.
 *
 * Defines the strict lifecycle contract using the Template Method pattern.
 * Ensures consistent behaviour across all scene implementations.
 *
 * Each concrete scene receives an ISceneManager reference so it can
 * trigger scene transitions (e.g. GameScene loads PauseScene on ESC).
 * This satisfies the Dependency Inversion Principle: concrete scenes
 * depend on the ISceneManager abstraction, not the SceneManager class.
 */
public abstract class Scene {

    private static final String TAG = "Scene";

    protected String name;
    protected boolean isPaused;

    /** Scene manager reference for scene transitions (DIP). */
    protected ISceneManager sceneManager;

    /**
     * No-arg constructor for scenes that do not need scene switching
     * (e.g. engine-internal test screens).
     */
    public Scene() {
        this.name = "";
        this.isPaused = false;
        this.sceneManager = null;
    }

    /**
     * Primary constructor. All game-layer scenes should use this
     * so they can trigger scene transitions.
     *
     * @param sceneManager the engine's scene management system
     */
    public Scene(ISceneManager sceneManager) {
        this.name = "";
        this.isPaused = false;
        this.sceneManager = sceneManager;
    }

    // ---- Lifecycle (Template Method pattern) ----

    /** Initialise scene resources. Called once when the scene is first loaded. */
    public abstract boolean create();

    /** Update scene logic per frame. */
    public abstract boolean update(float dt);

    /** Render the scene visually per frame. */
    public abstract boolean render();

    /** Clean up scene resources. Called once when the scene is removed. */
    public abstract boolean dispose();

    // ---- Pause / Resume ----

    /** Pause scene updates; rendering may continue. */
    public boolean pause() {
        isPaused = true;
        return true;
    }

    /** Resume scene updates after pause. */
    public boolean resume() {
        isPaused = false;
        return true;
    }

    // ---- Accessors ----

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            Gdx.app.error(TAG, "setName rejected null name");
            return;
        }
        this.name = name;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public ISceneManager getSceneManager() {
        return sceneManager;
    }
}
