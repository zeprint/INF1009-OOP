package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//SimulationScene - Main game simulation scene
//Contains the entity manager and handles game logic
public class SimulationScene extends Scene {
    private EntityManager entityManager;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Constructor for SimulationScene
    public SimulationScene() {
        super();
    }

    // Initialise the simulation scene
    // Sets up entity manager and creates initial entities
    // @return true if successful
    @Override
    public boolean create() {
        // Initialise managers
        entityManager = new EntityManager();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Example: Create some initial entities
        // add your game-specific entities here
        // For example:
        // Entity entity = new SomeEntityType(x, y);
        // entityManager.addEntity(entity);

        return true;
    }

    // Update the simulation
    // @param dt Delta time in seconds
    // @return true if successful
    @Override
    public boolean update(float dt) {
        if (isPaused) {
            return false;
        }

        // Update all entities
        if (entityManager != null) {
            entityManager.update(dt);
        }

        return true;
    }

    // Render the simulation
    // @return true if successful
    @Override
    public boolean render() {
        // Begin batch rendering for textures
        if (batch != null) {
            batch.begin();
        }

        // Begin shape rendering
        if (shapeRenderer != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }

        // Draw all entities
        if (entityManager != null) {
            entityManager.draw(batch, shapeRenderer);
        }

        // End rendering
        if (shapeRenderer != null) {
            shapeRenderer.end();
        }

        if (batch != null) {
            batch.end();
        }

        return true;
    }

    // Dispose of simulation resources
    // @return true if successful
    @Override
    public boolean dispose() {
        if (entityManager != null) {
            entityManager.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        return true;
    }

    // Get the entity manager
    // @return Entity manager instance
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
