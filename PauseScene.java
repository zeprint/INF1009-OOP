package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// PauseScene - Scene displayed when game is paused
// Shows pause menu and handles pause state
public class PauseScene extends Scene {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    // Constructor for PauseScene
    public PauseScene() {
        super();
    }

    // Initialize the pause scene
    // Sets up rendering components
    // @return true if successful
    @Override
    public boolean create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        
        // Set font color to white for visibility
        font.setColor(Color.WHITE);
        
        return true;
    }

    // Update the pause scene
    // @param dt Delta time in seconds
    // @return true if successful
    @Override
    public boolean update(float dt) {
        // Pause scene typically doesn't need updates
        // Can add input handling here for resume/quit buttons
        return true;
    }

    // Render the pause scene
    // Displays pause menu overlay
    // @return true if successful
    @Override
    public boolean render() {
        // Draw semi-transparent overlay
        if (shapeRenderer != null) {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f); // Semi-transparent black
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }

        // Draw pause text
        if (batch != null && font != null) {
            batch.begin();
            
            String pauseText = "PAUSED";
            float textWidth = font.getRegion().getRegionWidth() * pauseText.length();
            float x = (Gdx.graphics.getWidth() - textWidth) / 2;
            float y = Gdx.graphics.getHeight() / 2;
            
            font.draw(batch, pauseText, x, y);
            
            batch.end();
        }

        return true;
    }

    // Dispose of pause scene resources
    // @return true if successful
    @Override
    public boolean dispose() {
        if (batch != null) {
            batch.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        return true;
    }
}