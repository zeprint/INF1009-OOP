package io.github.some_example_name.lwjgl3.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.some_example_name.lwjgl3.GameMaster;
import io.github.some_example_name.lwjgl3.IInputSystem;
import io.github.some_example_name.lwjgl3.ISceneManager;
import io.github.some_example_name.lwjgl3.InputAction;
import io.github.some_example_name.lwjgl3.Scene;

/**
 * PauseScene - Semi-transparent overlay with resume/menu prompts.
 * P → resume game, ESC → back to main menu.
 * Uses its own camera to handle window resize correctly.
 */
public class PauseScene extends Scene {

    private static final String TAG = "PauseScene";
    private final IInputSystem inputSystem;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont promptFont;
    private GlyphLayout layout;

    public PauseScene(ISceneManager sceneManager, IInputSystem inputSystem) {
        super(sceneManager);
        this.inputSystem = inputSystem;
    }

    @Override
    public boolean create() {
        try {
            camera = new OrthographicCamera();
            batch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();
            titleFont = new BitmapFont();
            titleFont.setColor(Color.WHITE);
            titleFont.getData().setScale(3f);
            promptFont = new BitmapFont();
            promptFont.setColor(Color.LIGHT_GRAY);
            promptFont.getData().setScale(1.4f);
            layout = new GlyphLayout();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "create() failed", e);
            return false;
        }
    }

    @Override
    public boolean update(float dt) {
        if (inputSystem.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.loadScene(GameMaster.SCENE_GAME);
        }
        if (inputSystem.isActionTriggered(InputAction.BACK_TO_MENU)) {
            sceneManager.loadScene(GameMaster.SCENE_MAIN_MENU);
        }
        return true;
    }

    @Override
    public boolean render() {
        try {
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();

            // Update camera projection to current window size
            camera.setToOrtho(false, screenW, screenH);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            shapeRenderer.setProjectionMatrix(camera.combined);

            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.65f);
            shapeRenderer.rect(0, 0, screenW, screenH);
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

            batch.begin();
            layout.setText(titleFont, "PAUSED");
            titleFont.draw(batch, "PAUSED", (screenW - layout.width) / 2f, screenH * 0.6f);

            promptFont.setColor(Color.LIGHT_GRAY);
            String p1 = "Press P to Resume";
            layout.setText(promptFont, p1);
            promptFont.draw(batch, p1, (screenW - layout.width) / 2f, screenH * 0.38f);

            String p2 = "Press ESC for Main Menu";
            layout.setText(promptFont, p2);
            promptFont.draw(batch, p2, (screenW - layout.width) / 2f, screenH * 0.30f);
            batch.end();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "render() error", e);
            try { if (shapeRenderer.isDrawing()) shapeRenderer.end(); } catch (Exception ex) {}
            try { if (batch.isDrawing()) batch.end(); } catch (Exception ex) {}
            return false;
        }
    }

    @Override
    public boolean dispose() {
        try {
            if (batch != null) batch.dispose();
            if (shapeRenderer != null) shapeRenderer.dispose();
            if (titleFont != null) titleFont.dispose();
            if (promptFont != null) promptFont.dispose();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "dispose() error", e);
            return false;
        }
    }
}
