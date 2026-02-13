package io.github.some_example_name.lwjgl3;

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

    private final IMovementSystem  movementSystem;
    private final ICollisionSystem collisionSystem;
    private final IInputSystem     inputSystem;
    private final IAudioSystem     audioSystem;

    private final SpriteBatch   spriteBatch;
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

    // --- Scene lifecycle ---

    @Override
    public boolean create() {
        return true;  // Logic Engine subclasses override to populate entities
    }

    @Override
    public boolean update(float dt) {
        if (isPaused) return false;

        // Input is already polled by GameMaster â€” subclasses read state directly
        if (movementSystem != null) {
            movementSystem.update(dt);
        }

        if (entitySystem   != null) {
            entitySystem.update(dt);
        }

        if (collisionSystem != null) {
            collisionSystem.checkCollisions();
        }

        return true;
    }

    @Override
    public boolean render() {
        if (entitySystem == null) return false;

        // Pass 1: textures
        spriteBatch.begin();
        entitySystem.draw(spriteBatch, null);
        spriteBatch.end();

        // Pass 2: shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entitySystem.draw(null, shapeRenderer);
        shapeRenderer.end();

        return true;
    }

    @Override
    public boolean dispose() {
        if (entitySystem != null) entitySystem.dispose();
        return true;
    }

    // --- Accessors for Logic Engine subclasses ---

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
