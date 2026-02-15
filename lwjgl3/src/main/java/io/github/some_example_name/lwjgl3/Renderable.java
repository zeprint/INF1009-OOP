package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renderable - Contract for any object that can draw itself.
 */
public interface Renderable {

    boolean draw(SpriteBatch batch);

    boolean draw(ShapeRenderer shape);
}
