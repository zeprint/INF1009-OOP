package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
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

    private static final String TAG = "EntityManager";

    private final Array<Entity> entityList;

    public EntityManager() {
        entityList = new Array<Entity>();
    }

    // --- Lifecycle ---

    @Override
    public boolean addEntity(Entity entity) {
        if (entity == null) {
            Gdx.app.error(TAG, "addEntity rejected null entity");
            return false;
        }
        if (entityList.contains(entity, true)) {
            Gdx.app.error(TAG, "addEntity rejected duplicate entity: " + entity);
            return false;
        }
        entityList.add(entity);
        return true;
    }

    @Override
    public boolean removeEntity(Entity entity, boolean identity) {
        if (entity == null) {
            Gdx.app.error(TAG, "removeEntity rejected null entity");
            return false;
        }
        boolean removed = entityList.removeValue(entity, identity);
        if (!removed) {
            Gdx.app.error(TAG, "removeEntity could not find entity: " + entity);
        }
        return removed;
    }

    @Override
    public Array<Entity> getEntityList() {
        return entityList;
    }

    // --- Per-frame update ---

    @Override
    public boolean update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            Gdx.app.error(TAG, "update rejected invalid deltaTime: " + deltaTime);
            return false;
        }

        boolean allSucceeded = true;
        for (Entity entity : entityList) {
            try {
                if (!entity.update(deltaTime)) {
                    allSucceeded = false;
                }
            } catch (Exception e) {
                Gdx.app.error(TAG, "Exception updating entity: " + entity, e);
                allSucceeded = false;
            }
        }
        return allSucceeded;
    }

    // --- Rendering ---

    @Override
    public boolean draw(SpriteBatch batch, ShapeRenderer shape) {
        boolean allSucceeded = true;
        for (Entity entity : entityList) {
            try {
                if (batch != null && batch.isDrawing()) {
                    if (!entity.draw(batch)) {
                        allSucceeded = false;
                    }
                }
                if (shape != null && shape.isDrawing()) {
                    if (!entity.draw(shape)) {
                        allSucceeded = false;
                    }
                }
            } catch (Exception e) {
                Gdx.app.error(TAG, "Exception drawing entity: " + entity, e);
                allSucceeded = false;
            }
        }
        return allSucceeded;
    }

    // --- Shutdown ---

    @Override
    public boolean dispose() {
        boolean allSucceeded = true;
        for (Entity entity : entityList) {
            try {
                if (!entity.dispose()) {
                    allSucceeded = false;
                }
            } catch (Exception e) {
                Gdx.app.error(TAG, "Exception disposing entity: " + entity, e);
                allSucceeded = false;
            }
        }
        entityList.clear();
        return allSucceeded;
    }
}