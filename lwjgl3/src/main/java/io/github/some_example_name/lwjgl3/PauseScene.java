package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
* PauseScene, a 50% transparent overlay with "PAUSED" at the center of the game in frozen state.
* Run as a passive scene with no update logic to adhere to SRP
* Achieve fast transition for consideration of user experience
* Isolates resource disposal to prevent memory leaks issues
*/
public class PauseScene extends Scene {

    private static final String TAG = "PauseScene";
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private ShapeRenderer shapeRenderer;

    // implmentation of Scene lifecycle methods
    @Override
    public boolean create() {
        try {
            batch = new SpriteBatch();
            font = new BitmapFont();
            layout = new GlyphLayout();
            shapeRenderer = new ShapeRenderer();
            font.setColor(Color.WHITE);
            return true;
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Exception during create()!", e);
            return false;
        }
    }

    // no input handling or game logic update, just a static overlay
    @Override
    public boolean update(float dt) {
        if (!Float.isFinite(dt) || dt < 0f) {
            Gdx.app.error(TAG, "update rejected invalid deltaTime: " + dt);
            return false;
        }
        // means passive scene, input handling belongs in the Logic Engine layer
        return true;
    }

    // render the half-transparent overlay and the centered PAUSED text
    @Override
    public boolean render() {
        
        boolean allRenderSucceeded = true;
        
        // half-transparent dark overlay
        try {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error rendering overlay!", e);
            try {
                if (shapeRenderer.isDrawing()) {
                    shapeRenderer.end();
                }
                Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
            } 
            catch (Exception endEx) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        // centered "PAUSED" text
        try {
            final String text = "PAUSED";
            layout.setText(font, text);
            float x = (Gdx.graphics.getWidth()  - layout.width)  / 2f;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2f;

            batch.begin();
            font.draw(batch, text, x, y);
            batch.end();
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error rendering text!", e);
            try {
                if (batch.isDrawing()) {
                    batch.end();
                }
            } 
            catch (Exception endException) {
                // do nothing, ignore
            }
            allRenderSucceeded = false;
        }

        return allRenderSucceeded;
    }

// dispose resources, log down any exceptions and try to dispose as many resources as possible to prevent memory leaks issue
@Override
    public boolean dispose() {
        boolean allDisposeSucceeded = true;
        
        try {
            if (batch != null) {
                batch.dispose();
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing batch", e);
            allDisposeSucceeded = false;
        }

        try {
            if (font != null) {
                font.dispose();
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing font", e);
            allDisposeSucceeded = false;
        }
        
        try {
            if (shapeRenderer != null) {
                shapeRenderer.dispose();
            }
        } 
        catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing shapeRenderer", e);
            allDisposeSucceeded = false;
        }

        return allDisposeSucceeded;
    }
}
