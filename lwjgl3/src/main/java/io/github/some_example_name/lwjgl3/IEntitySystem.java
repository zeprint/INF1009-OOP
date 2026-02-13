package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * IEntitySystem - Contract for entity management (DIP).
 *
 * Callers depend on this interface rather than EntityManager directly,
 * allowing the entity system to be swapped (e.g. spatial-partitioned
 * entity manager) without touching call sites.
 *
 * Implementing class: EntityManager
 */
public interface IEntitySystem {

    /** Register an entity for management. */
    void addEntity(Entity entity);

    /** Remove an entity. */
    void removeEntity(Entity entity, boolean identity);

    /** @return direct reference to the entity list. */
    Array<Entity> getEntityList();

    /** Update all managed entities. */
    void update(float deltaTime);

    /** Draw all entities using whichever renderer is currently active. */
    void draw(SpriteBatch batch, ShapeRenderer shape);

    /** Dispose every entity then clear the list. */
    void dispose();
}
