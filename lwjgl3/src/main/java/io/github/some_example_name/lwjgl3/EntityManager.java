package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * EntityManager - Creates, manages, updates, and disposes all entities (SRP).
 *
 * Rendering contract: SpriteBatch and ShapeRenderer cannot both be active
 * simultaneously. Callers begin/end renderers; EntityManager checks isDrawing().
 */
public class EntityManager implements IEntitySystem {

    private final Array<Entity> entityList;

    public EntityManager() {
        entityList = new Array<Entity>();
    }

    // --- Lifecycle ---

    /* Register an entity. Null entities are silently ignored. */
    @Override
    public void addEntity(Entity entity) {
        if (entity != null) {
            entityList.add(entity);
        }
    }

    /* Remove an entity. identity=true uses reference equality. */
    @Override
    public void removeEntity(Entity entity, boolean identity) {
        entityList.removeValue(entity, identity);
    }

    /* Direct reference to the entity list. */
    @Override
    public Array<Entity> getEntityList() {
        return entityList;
    }

    // --- Per-frame update ---

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entityList) {
            entity.update(deltaTime);
        }
    }

    // --- Rendering ---

    /* Draw all entities using whichever renderer is currently active. */
    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity entity : entityList) {
            if (batch != null && batch.isDrawing()) {
                entity.draw(batch);
            }
            if (shape != null && shape.isDrawing()) {
                entity.draw(shape);
            }
        }
    }

    // --- Shutdown ---

    /* Dispose every entity then clear the list. */
    @Override
    public void dispose() {
        for (Entity entity : entityList) {
            entity.dispose();
        }
        entityList.clear();
    }
}
