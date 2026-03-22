package io.github.some_example_name.lwjgl3.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.GameMaster;
import io.github.some_example_name.lwjgl3.IAudioSystem;
import io.github.some_example_name.lwjgl3.IInputSystem;
import io.github.some_example_name.lwjgl3.ISceneManager;
import io.github.some_example_name.lwjgl3.InputAction;
import io.github.some_example_name.lwjgl3.Scene;

/**
 * EndScene - Displayed when the player loses all health.
 *
 * Shows "GAME OVER", the final score, and a prompt to restart.
 * Input logic: checks InputAction.CONFIRM (ENTER) to return to MainMenu.
 */
public class EndScene extends Scene {

    private static final String TAG = "EndScene";

    private final SpriteBatch spriteBatch;
    private final IInputSystem inputSystem;
    private final IAudioSystem audioSystem;

    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont promptFont;
    private GlyphLayout layout;

    // Score to display (set when scene resumes)
    private int finalScore;

    /**
     * @param sceneManager for scene transitions (DIP)
     * @param spriteBatch  shared rendering resource from GameMaster
     * @param inputSystem  for polling ENTER to restart
     * @param audioSystem  for sound effects
     */
    public EndScene(ISceneManager sceneManager,
                    SpriteBatch spriteBatch,
                    IInputSystem inputSystem,
                    IAudioSystem audioSystem) {
        super(sceneManager);
        this.spriteBatch = spriteBatch;
        this.inputSystem = inputSystem;
        this.audioSystem = audioSystem;
        this.finalScore = 0;
    }

    @Override
    public boolean create() {
        try {
            titleFont = new BitmapFont();
            titleFont.setColor(Color.RED);
            titleFont.getData().setScale(3f);

            scoreFont = new BitmapFont();
            scoreFont.setColor(Color.CYAN);
            scoreFont.getData().setScale(2f);

            promptFont = new BitmapFont();
            promptFont.setColor(Color.WHITE);
            promptFont.getData().setScale(1.5f);

            layout = new GlyphLayout();

            Gdx.app.log(TAG, "EndScene created");
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "create() failed", e);
            return false;
        }
    }

    @Override
    public boolean resume() {
        super.resume();
        // Retrieve the score from the GameScene when we become active
        Scene gameScene = sceneManager.getCurrentScene();
        // Note: getCurrentScene() returns *this* after loadScene, so we
        // need to check if the previous scene was a GameScene.
        // For now, the score is passed via the setter below.
        return true;
    }

    @Override
    public boolean update(float dt) {
        if (isPaused) return false;

        // ENTER pressed → return to main menu
        if (inputSystem.isActionTriggered(InputAction.CONFIRM)) {
            audioSystem.playSound("click");
            sceneManager.loadScene(GameMaster.SCENE_MAIN_MENU);
        }

        return true;
    }

    @Override
    public boolean render() {
        try {
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();

            spriteBatch.begin();

            // Title
            String title = "GAME OVER";
            layout.setText(titleFont, title);
            titleFont.draw(spriteBatch, title,
                    (screenW - layout.width) / 2f,
                    screenH * 0.7f);

            // Score
            String scoreText = "Score: " + finalScore;
            layout.setText(scoreFont, scoreText);
            scoreFont.draw(spriteBatch, scoreText,
                    (screenW - layout.width) / 2f,
                    screenH * 0.5f);

            // Prompt
            String prompt = "Press ENTER to Return to Menu";
            layout.setText(promptFont, prompt);
            promptFont.draw(spriteBatch, prompt,
                    (screenW - layout.width) / 2f,
                    screenH * 0.3f);

            spriteBatch.end();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "render() error", e);
            try { if (spriteBatch.isDrawing()) spriteBatch.end(); } catch (Exception ex) { /* swallow */ }
            return false;
        }
    }

    @Override
    public boolean dispose() {
        try {
            if (titleFont != null) titleFont.dispose();
            if (scoreFont != null) scoreFont.dispose();
            if (promptFont != null) promptFont.dispose();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "dispose() error", e);
            return false;
        }
    }

    // ---- Score injection (called by GameScene before transition) ----

    public void setFinalScore(int score) {
        this.finalScore = score;
    }
}
