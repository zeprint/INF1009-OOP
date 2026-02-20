package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * SampleLogicScreen - A simple demo screen that displays the libgdx logo.
 * 
 * This is a sample implementation of a game screen that can be extended
 * by the Logic Engine layer to add custom game logic.
 */
public class SampleLogicScreen extends Scene {

    private static final String TAG = "SampleLogicScreen";
    
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Texture libgdxLogo;
    private float logoX;
    private float logoY;

    public SampleLogicScreen(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        super();
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
    }

    @Override
    public boolean create() {
        try {
            // Load the libgdx logo from assets
            libgdxLogo = new Texture(Gdx.files.internal("libgdx128.png"));
            
            // Center the logo on screen
            logoX = (Gdx.graphics.getWidth() - libgdxLogo.getWidth()) / 2f;
            logoY = (Gdx.graphics.getHeight() - libgdxLogo.getHeight()) / 2f;
            
            Gdx.app.log(TAG, "SampleLogicScreen created successfully");
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to create SampleLogicScreen: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(float dt) {
        if (!Float.isFinite(dt) || dt < 0f) {
            Gdx.app.error(TAG, "update() rejected invalid deltaTime: " + dt);
            return false;
        }
        
        if (isPaused) {
            return false;
        }

        return true;
    }

    @Override
    public boolean render() {
        try {
            spriteBatch.begin();
            spriteBatch.draw(libgdxLogo, logoX, logoY);
            spriteBatch.end();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error rendering: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean dispose() {
        try {
            if (libgdxLogo != null) {
                libgdxLogo.dispose();
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error disposing: " + e.getMessage());
            return false;
        }
    }
}
