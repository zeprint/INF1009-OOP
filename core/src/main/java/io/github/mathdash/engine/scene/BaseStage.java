package io.github.mathdash.engine.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * BaseStage - Abstract wrapper around a libGDX Stage.
 * Provides a lifecycle for stage setup, updating, and disposal.
 * Each Scene can use one or more stages (e.g. a game-world stage and a UI stage).
 */

public abstract class BaseStage {

    private Stage stage;
    private boolean active;

    // Creates a BaseStage with the given viewport.
    public BaseStage(Viewport viewport) {
        if (viewport == null) {
            throw new IllegalArgumentException("Viewport cannot be null.");
        }
        this.stage = new Stage(viewport);
        this.active = true;
        initialize();
    }

    // Called once after the stage is created. Subclasses add actors and configure the stage here.
    protected abstract void initialize();

    // Called every frame to update stage logic.
    public void update(float deltaTime) {
        if (!active) return;
        stage.act(deltaTime);
    }

    // Draws the stage and all its actors.
    public void render() {
        if (!active) return;
        stage.draw();
    }

    // Called when the viewport is resized. Updates the stage's viewport.
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Disposes the underlying libGDX Stage and releases resources.
    public void dispose() {
        stage.dispose();
    }

    // ---- Getters and Setters ----

    // Returns the underlying libGDX Stage for direct access when needed.
    public Stage getStage() {
        return stage;
    }

    // Returns whether this stage is active.
    public boolean isActive() {
        return active;
    }

    // Sets whether this stage is active. Inactive stages are not updated or rendered.
    public void setActive(boolean active) {
        this.active = active;
    }
}
