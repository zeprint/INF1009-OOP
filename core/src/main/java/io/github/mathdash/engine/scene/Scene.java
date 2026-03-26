package io.github.mathdash.engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Scene - Represents a single scene in the simulation.
 * A scene encapsulates a distinct state such as a menu, gameplay, or settings.
 * Each scene has its own lifecycle: load, update, render, unload.
 */

public abstract class Scene {

    private final String name;
    private boolean loaded;

    // Creates a new Scene with the given name.
    public Scene(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Scene name cannot be null or empty.");
        }
        this.name = name;
        this.loaded = false;
    }

    // Called once when the scene is first loaded. Use for resource allocation and setup.
    public void load() {
        if (loaded) {
            return;
        }
        onLoad();
        loaded = true;
    }

    // Called once when the scene is being unloaded. Use for resource cleanup.
    public void unload() {
        if (!loaded) {
            return;
        }
        onUnload();
        loaded = false;
    }

    // Called when this scene becomes the active scene (e.g. after a transition).
    public void show() {
        onShow();
    }

    // Called when this scene is no longer the active scene (e.g. before a transition).
    public void hide() {
        onHide();
    }

    // Called when the window is resized.
    public void resize(int width, int height) {
        onResize(width, height);
    }

    // ---- Abstract Lifecycle Methods ----

    // Subclasses implement this to load resources and initialize scene state.
    protected abstract void onLoad();

    // Subclasses implement this to release resources when the scene is unloaded.
    protected abstract void onUnload();

    // Subclasses implement this to handle becoming the active scene.
    protected abstract void onShow();

    // Subclasses implement this to handle no longer being the active scene.
    protected abstract void onHide();

    // Called every frame to update the scene's state.
    public abstract void update(float deltaTime);

    // Called every frame to render the scene.
    public abstract void render(SpriteBatch batch);

    // Called when the window is resized. Override for custom resize handling.
    protected void onResize(int width, int height) {
        // Default: do nothing. Subclasses can override.
    }

    // ---- Getters ----

    // Returns the name of this scene.
    public String getName() {
        return name;
    }

    // Returns whether this scene has been loaded.
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + ", loaded=" + loaded + "]";
    }
}
