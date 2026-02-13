package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * PauseScene - Semi-transparent overlay with a centred "PAUSED" label.
 *
 * Uses GlyphLayout for accurate text width measurement (not the font atlas width).
 */
public class PauseScene extends Scene {

    private SpriteBatch   batch;
    private BitmapFont    font;
    private GlyphLayout   layout;
    private ShapeRenderer shapeRenderer;

    @Override
    public boolean create() {
        batch         = new SpriteBatch();
        font          = new BitmapFont();
        layout        = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        font.setColor(Color.WHITE);
        return true;
    }

    @Override
    public boolean update(float dt) {
        // Passive scene; input handling belongs in the Logic Engine layer
        return true;
    }

    @Override
    public boolean render() {
        // Semi-transparent dark overlay
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // Centred "PAUSED" text
        final String text = "PAUSED";
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth()  - layout.width)  / 2f;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2f;

        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();

        return true;
    }

    @Override
    public boolean dispose() {
        if (batch         != null) batch.dispose();
        if (font          != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        return true;
    }
}
