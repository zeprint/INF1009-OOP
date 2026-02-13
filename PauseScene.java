package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PauseScene extends Scene {

    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;

    @Override
    public void create() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();
    }

    @Override
    public void update(float dt) {
        // Press P to Resume
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            Gdx.app.log("PauseScene", "Resume key pressed - switching to simulation");
            if (sceneManager != null) {
                sceneManager.loadScene("simulation");
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();

        // 1. Draw Semi-Transparent Black Background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f);
        shapeRenderer.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 2. Draw Centered Text
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        String text1 = "PAUSED";
        layout.setText(font, text1);
        float w1 = layout.width;
        float h1 = layout.height;

        String text2 = "(Press P to Resume)";
        layout.setText(font, text2);
        float w2 = layout.width; // Updated: P key only

        font.draw(batch, text1, (viewport.getWorldWidth() - w1) / 2, (viewport.getWorldHeight() / 2) + h1 + 20);
        font.draw(batch, text2, (viewport.getWorldWidth() - w2) / 2, (viewport.getWorldHeight() / 2) - 20);

        batch.end();
    }

    @Override
    public void dispose() {
        if (font != null)
            font.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
