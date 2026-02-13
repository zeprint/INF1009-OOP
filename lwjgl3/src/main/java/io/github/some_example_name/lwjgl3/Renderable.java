package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renderable - Contract for any object that can draw itself (ISP).
 *
 * Only entities that actually render something implement this.
 * SpriteBatch and ShapeRenderer cannot both be active simultaneously;
 * implementors should only use the renderer that is currently drawing.
 *
 * Implementing classes: TextureObject, Shapes, RotatingShape
 */
public interface Renderable {

    /* Draw using a SpriteBatch (texture-based rendering). */
    void draw(SpriteBatch batch);

    /* Draw using a ShapeRenderer (primitive shape rendering). */
    void draw(ShapeRenderer shape);
}
