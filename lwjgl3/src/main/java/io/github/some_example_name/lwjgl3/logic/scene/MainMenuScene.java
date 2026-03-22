package io.github.some_example_name.lwjgl3.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
 * MainMenuScene - Entry point. Title, controls, mute toggle, and start prompt.
 *
 * ENTER → start game, M → toggle mute.
 */
public class MainMenuScene extends Scene {

    private static final String TAG = "MainMenuScene";
    private final SpriteBatch spriteBatch;
    private final IInputSystem inputSystem;
    private final IAudioSystem audioSystem;

    private BitmapFont titleFont;
    private BitmapFont promptFont;
    private BitmapFont smallFont;
    private GlyphLayout layout;
    private Texture muteTexture;
    private Texture unmuteTexture;

    public MainMenuScene(ISceneManager sceneManager, SpriteBatch spriteBatch,
                         IInputSystem inputSystem, IAudioSystem audioSystem) {
        super(sceneManager);
        this.spriteBatch = spriteBatch;
        this.inputSystem = inputSystem;
        this.audioSystem = audioSystem;
    }

    @Override
    public boolean create() {
        try {
            titleFont = new BitmapFont();
            titleFont.setColor(Color.CYAN);
            titleFont.getData().setScale(3f);

            promptFont = new BitmapFont();
            promptFont.setColor(Color.WHITE);
            promptFont.getData().setScale(1.5f);

            smallFont = new BitmapFont();
            smallFont.setColor(Color.GRAY);
            smallFont.getData().setScale(1.1f);

            layout = new GlyphLayout();

            muteTexture = new Texture(Gdx.files.internal("mute.png"));
            unmuteTexture = new Texture(Gdx.files.internal("unmute.png"));

            Gdx.app.log(TAG, "MainMenuScene created");
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "create() failed", e);
            return false;
        }
    }

    @Override
    public boolean update(float dt) {
        if (isPaused) return false;

        if (inputSystem.isActionTriggered(InputAction.CONFIRM)) {
            audioSystem.playSound("click");
            sceneManager.loadScene(GameMaster.SCENE_GAME);
        }

        if (inputSystem.isActionTriggered(InputAction.TOGGLE_MUTE)) {
            boolean newMute = !audioSystem.isMuted();
            audioSystem.setMuted(newMute);
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
            String title = "LANGUAGE RUNNER";
            layout.setText(titleFont, title);
            titleFont.draw(spriteBatch, title, (screenW - layout.width) / 2f, screenH * 0.72f);

            // Subtitle
            promptFont.setColor(Color.WHITE);
            String subtitle = "Learn words. Dodge wrong answers. Survive.";
            layout.setText(promptFont, subtitle);
            promptFont.draw(spriteBatch, subtitle, (screenW - layout.width) / 2f, screenH * 0.58f);

            // Start prompt
            promptFont.setColor(Color.YELLOW);
            String start = ">>> Press ENTER to Start <<<";
            layout.setText(promptFont, start);
            promptFont.draw(spriteBatch, start, (screenW - layout.width) / 2f, screenH * 0.42f);

            // Controls
            smallFont.setColor(Color.LIGHT_GRAY);
            String[] controls = {
                "A/D or Left/Right : Move Lanes",
                "W/Up/Space : Jump",
                "S/Down : Shield (3 uses)",
                "P : Pause  |  ESC : Main Menu  |  M : Mute"
            };
            float cy = screenH * 0.30f;
            for (String line : controls) {
                layout.setText(smallFont, line);
                smallFont.draw(spriteBatch, line, (screenW - layout.width) / 2f, cy);
                cy -= 22f;
            }

            // Mute icon (bottom-right corner)
            Texture soundIcon = audioSystem.isMuted() ? muteTexture : unmuteTexture;
            spriteBatch.draw(soundIcon, screenW - 50f, 15f, 32f, 32f);

            smallFont.setColor(Color.GRAY);
            String muteLabel = audioSystem.isMuted() ? "Muted (M)" : "Sound ON (M)";
            layout.setText(smallFont, muteLabel);
            smallFont.draw(spriteBatch, muteLabel, screenW - 55f - layout.width, 30f);

            spriteBatch.end();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "render() error", e);
            try { if (spriteBatch.isDrawing()) spriteBatch.end(); } catch (Exception ex) {}
            return false;
        }
    }

    @Override
    public boolean dispose() {
        try {
            if (titleFont != null) titleFont.dispose();
            if (promptFont != null) promptFont.dispose();
            if (smallFont != null) smallFont.dispose();
            if (muteTexture != null) muteTexture.dispose();
            if (unmuteTexture != null) unmuteTexture.dispose();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "dispose() error", e);
            return false;
        }
    }
}
