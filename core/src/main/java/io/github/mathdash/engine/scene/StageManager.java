package io.github.mathdash.engine.scene;

import com.badlogic.gdx.utils.Array;

/**
 * StageManager - Manages multiple BaseStage instances within a scene.
 * Handles updating, rendering, resizing, and disposing all registered stages.
 * Stages are rendered in the order they are added (back to front).
 */

public class StageManager {

    private final Array<BaseStage> stages;

    // Creates a new, empty StageManager.
    public StageManager() {
        this.stages = new Array<>();
    }

    // Adds a stage to the manager. Stages added later render on top of earlier ones.
    public void addStage(BaseStage stage) {
        if (stage == null) {
            throw new IllegalArgumentException("Cannot add a null stage.");
        }
        stages.add(stage);
    }

    // Removes a stage from the manager.
    public void removeStage(BaseStage stage) {
        if (stage != null) {
            stages.removeValue(stage, true);
        }
    }

    // Updates all active stages.
    public void update(float deltaTime) {
        for (int i = 0; i < stages.size; i++) {
            BaseStage stage = stages.get(i);
            if (stage.isActive()) {
                stage.update(deltaTime);
            }
        }
    }

    // Renders all active stages in order (back to front).
    public void render() {
        for (int i = 0; i < stages.size; i++) {
            BaseStage stage = stages.get(i);
            if (stage.isActive()) {
                stage.render();
            }
        }
    }

    // Forwards a resize event to all stages.
    public void resize(int width, int height) {
        for (int i = 0; i < stages.size; i++) {
            stages.get(i).resize(width, height);
        }
    }

    // Disposes all stages and clears the manager.
    public void dispose() {
        for (int i = 0; i < stages.size; i++) {
            stages.get(i).dispose();
        }
        stages.clear();
    }

    // Returns all registered stages.
    public Array<BaseStage> getStages() {
        return new Array<>(stages);
    }

    // Returns the number of registered stages.
    public int getStageCount() {
        return stages.size;
    }
}
