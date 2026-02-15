package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * IEntitySystem - Contract for entity management.
 */
public interface IEntitySystem {

    boolean addEntity(Entity entity);

    boolean removeEntity(Entity entity, boolean identity);

    Array<Entity> getEntityList();

    boolean update(float deltaTime);

    boolean draw(SpriteBatch batch, ShapeRenderer shape);

    boolean dispose();
}
