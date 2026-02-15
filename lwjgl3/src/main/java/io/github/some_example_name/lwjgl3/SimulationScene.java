package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
* SimulationScene - The main running scene of the simulation.
*
* Per-frame pipeline: movement â†’ entity update â†’ collision â†’ render
*
* DIP: Depends on interfaces (IMovementSystem, ICollisionSystem, IInputSystem,
* IAudioSystem) instead of concrete managers, allowing any subsystem to be swapped.
*
* Note: Input polling is handled by GameMaster so it works even during pause.
* This scene reads input state but does not call inputSystem.update().
*/
public class SimulationScene extends Scene {

    private static final String TAG = "SimulationScene";

    private final IMovementSystem movementSystem;
    private final ICollisionSystem collisionSystem;
    private final IInputSystem inputSystem;
    private final IAudioSystem audioSystem;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;

    /**
    * All manager references are injected by GameMaster.
    * No game-specific entities are created here (Logic Engine territory).
    */
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

    // Scene lifecycle

    @Override
    public boolean create() {
        return true;  // Logic Engine subclasses override to populate entities
    }

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

        // Input is already polled by GameMaster â€” subclasses read state directly
        try {
            if (movementSystem != null) {
                movementSystem.update(dt);
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error updating movementSystem!", e);
            allUpdateSucceeded = false;
        }

        try {
            if (entitySystem != null) {
                if (!entitySystem.update(dt)) {
                    allUpdateSucceeded = false;
                }
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error updating entitySystem!", e);
            allUpdateSucceeded = false;
        }

        try {
            if (collisionSystem != null) {
                collisionSystem.checkCollisions();
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error in collisionSystem!", e);
            allUpdateSucceeded = false;
        }

        return allUpdateSucceeded;
    }

    @Override
    public boolean render() {
        if (entitySystem == null) {
            Gdx.app.error(TAG, "render() rejected null entitySystem");
            return false;
        }
        boolean allRenderSucceeded = true;

        // Pass 1: textures
        try {
            spriteBatch.begin();
            if (!entitySystem.draw(spriteBatch, null)) {
                allRenderSucceeded = false;
            }
            spriteBatch.end();
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error during SpriteBatch rendering!", e);
            try {
                if (spriteBatch.isDrawing()) spriteBatch.end();
            } catch (Exception endEx) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        // Pass 2: shapes
        try {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (!entitySystem.draw(null, shapeRenderer)) {
                allRenderSucceeded = false;
            }
            shapeRenderer.end();
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error during ShapeRenderer rendering!", e);
            try {
                if (shapeRenderer.isDrawing()) shapeRenderer.end();
            } 
            catch (Exception endEx) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        return allRenderSucceeded;
    }

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

    // Accessors for Logic Engine subclasses

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
