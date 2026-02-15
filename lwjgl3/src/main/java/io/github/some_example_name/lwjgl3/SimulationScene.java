package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
* Coordinates the update pipeline to prevent physics or gravity bugs and logic errors
* SimulationScene acts as the middle layer injecting dependencies for DIP compliance
* Uses two-pass rendering to ensure clean state transitions using SpriteBatch and ShapeRenderer
* Manages subsystem complexity while adhering to LSP
*/
public class SimulationScene extends Scene {

    private static final String TAG = "SimulationScene"; // for logging

    private final IMovementSystem movementSystem;
    private final ICollisionSystem collisionSystem;
    private final IInputSystem inputSystem;
    private final IAudioSystem audioSystem;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;

    
    // all manager references are injected by GameMaster.
    public SimulationScene(
            IEntitySystem entitySystem,
            IMovementSystem movementSystem,
            ICollisionSystem collisionSystem,
            IInputSystem inputSystem,
            IAudioSystem audioSystem,
            SpriteBatch spriteBatch,
            ShapeRenderer shapeRenderer) {

        super();
        this.entitySystem = entitySystem;
        this.movementSystem = movementSystem;
        this.collisionSystem = collisionSystem;
        this.inputSystem = inputSystem;
        this.audioSystem = audioSystem;
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
    }

    // purpose to allow subclasses to override create() and add entities to entitySystem before update() and render() are called
    @Override
    public boolean create() {
        return true;  // Logic Engine subclasses override to populate entities
    }

    // update() is called by GameMaster after create() and before render()
    @Override
    public boolean update(float dt) {
        if (!Float.isFinite(dt) || dt < 0f) {
            Gdx.app.error(TAG, "update() rejected invalid deltaTime: " + dt);
            return false;
        }

        if (isPaused) {
            return false;
        }

        boolean allUpdateSucceeded = true;

        // if movementSystem is null, still update entitySystem and collisionSystem to allow static entities and collision checks to function
        try {
            if (movementSystem != null) {
                movementSystem.update(dt);
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error updating movementSystem!", e);
            allUpdateSucceeded = false;
        }

        try {
            if (entitySystem != null) {
                if (!entitySystem.update(dt)) {
                    allUpdateSucceeded = false;
                }
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error updating entitySystem!", e);
            allUpdateSucceeded = false;
        }

        try {
            if (collisionSystem != null) {
                collisionSystem.checkCollisions();
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error in collisionSystem!", e);
            allUpdateSucceeded = false;
        }

        return allUpdateSucceeded;
    }

    // render() is called by GameMaster after update()
    @Override
    public boolean render() {
        if (entitySystem == null) {
            Gdx.app.error(TAG, "render() rejected null entitySystem");
            return false;
        }
        boolean allRenderSucceeded = true;

        // textures to be drawn with SpriteBatch
        try {
            spriteBatch.begin();
            if (!entitySystem.draw(spriteBatch, null)) {
                allRenderSucceeded = false;
            }
            spriteBatch.end();
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error during SpriteBatch rendering!", e);
            try {
                if (spriteBatch.isDrawing()){
                    spriteBatch.end();
                } 
            } 
            catch (Exception endEx) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        // shapes to be drawn with ShapeRenderer.
        try {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (!entitySystem.draw(null, shapeRenderer)) {
                allRenderSucceeded = false;
            }
            shapeRenderer.end();
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error during ShapeRenderer rendering!", e);
            try {
                if (shapeRenderer.isDrawing()) {
                    shapeRenderer.end();
                }
            } 
            catch (Exception endEx) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        return allRenderSucceeded;
    }

    // dispose() is called by GameMasteer when switching scenes or exit engine.
    @Override
    public boolean dispose() {
        if (entitySystem != null) {
            try {
                return entitySystem.dispose();
            } 
            catch (Exception e) {
                Gdx.app.error(TAG, "Exception disposing entitySystem", e);
                return false;
            }
        }
        return true;
    }

    // Get accessors for engine subclasses

    public IMovementSystem getMovementSystem() {
        return movementSystem;
    }

    public ICollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public IInputSystem getInputSystem() {
        return inputSystem;
    }

    public IAudioSystem getAudioSystem() {
        return audioSystem;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }
}
