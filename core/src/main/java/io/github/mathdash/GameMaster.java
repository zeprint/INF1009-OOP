package io.github.mathdash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.mathdash.engine.ServiceLocator;
import io.github.mathdash.engine.inputoutput.AudioManager;
import io.github.mathdash.engine.scene.SceneManager;
import io.github.mathdash.logic.scene.DeathScene;
import io.github.mathdash.logic.scene.GameScene;
import io.github.mathdash.logic.scene.MainMenuScene;
import io.github.mathdash.logic.scene.PauseScene;

/**
 * GameMaster - Main application entry point.
 *
 * Bootstraps the ServiceLocator with shared engine services (AudioManager),
 * then wires all scenes together via SceneManager.
 */
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private AudioManager audioManager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Bootstrap shared services via ServiceLocator (Singleton / Service Locator pattern)
        audioManager = new AudioManager();
        audioManager.loadSound("select", "kenney_new-platformer-pack-1.1/Sounds/sfx_select.ogg");
        audioManager.loadSound("jump", "kenney_new-platformer-pack-1.1/Sounds/sfx_jump.ogg");
        audioManager.loadSound("hurt", "kenney_new-platformer-pack-1.1/Sounds/sfx_hurt.ogg");
        audioManager.loadSound("correct", "kenney_new-platformer-pack-1.1/Sounds/sfx_coin.ogg");
        audioManager.loadSound("wrong", "kenney_new-platformer-pack-1.1/Sounds/sfx_bump.ogg");
        audioManager.loadSound("death", "kenney_new-platformer-pack-1.1/Sounds/sfx_disappear.ogg");
        ServiceLocator.provide(audioManager);

        sceneManager = new SceneManager();

        try {
            MainMenuScene mainMenu = new MainMenuScene(sceneManager, this::startGame);
            sceneManager.addScene(mainMenu);
            sceneManager.setScene("mainmenu");
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Failed to initialize scenes", e);
        }
    }

    private void startGame(int level) {
        try {
            cleanupGameScenes();

            GameScene gameScene = new GameScene(sceneManager, level);

            PauseScene pauseScene = new PauseScene(sceneManager, this::returnToMainMenu);

            DeathScene deathScene = new DeathScene(sceneManager,
                () -> startGame(level),
                this::returnToMainMenu
            );
            deathScene.setFinalScore(0);
            deathScene.setLevel(level);

            sceneManager.addScene(gameScene);
            sceneManager.addScene(pauseScene);
            sceneManager.addScene(deathScene);

            sceneManager.setScene("game");
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Failed to start game level " + level, e);
        }
    }

    private void returnToMainMenu() {
        cleanupGameScenes();
        sceneManager.setScene("mainmenu");
    }

    private void cleanupGameScenes() {
        if (sceneManager.hasScene("game")) {
            sceneManager.removeScene("game");
        }
        if (sceneManager.hasScene("pause")) {
            sceneManager.removeScene("pause");
        }
        if (sceneManager.hasScene("death")) {
            sceneManager.removeScene("death");
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();
        sceneManager.update(deltaTime);
        sceneManager.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
    }

    @Override
    public void dispose() {
        if (sceneManager != null) sceneManager.dispose();
        if (batch != null) batch.dispose();
        if (audioManager != null) audioManager.dispose();
        ServiceLocator.reset();
    }
}
