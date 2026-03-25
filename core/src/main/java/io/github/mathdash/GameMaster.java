package io.github.mathdash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.mathdash.AbstractEngine.inputouput.InputAction;
import io.github.mathdash.AbstractEngine.inputouput.InputBindings;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.logic.scene.DeathScene;
import io.github.mathdash.logic.scene.GameScene;
import io.github.mathdash.logic.scene.MainMenuScene;
import io.github.mathdash.logic.scene.PauseScene;

/**
 * GameMaster - Main application entry point.
 * Wires all scenes together via SceneManager.
 */
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
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

            // Build game input bindings — Input.Keys stays in GameMaster.
            InputBindings gameBindings = new InputBindings();
            gameBindings.bindAction(InputAction.JUMP, Input.Keys.UP);
            gameBindings.bindAction(InputAction.JUMP, Input.Keys.W);
            gameBindings.bindAction(InputAction.CONFIRM, Input.Keys.DOWN);
            gameBindings.bindAction(InputAction.CONFIRM, Input.Keys.S);
            gameBindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.ESCAPE);
            gameBindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.P);

            GameScene gameScene = new GameScene(sceneManager, level, gameBindings);

            // Build pause input bindings.
            InputBindings pauseBindings = new InputBindings();
            pauseBindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.ESCAPE);
            pauseBindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.P);

            PauseScene pauseScene = new PauseScene(sceneManager, this::returnToMainMenu, pauseBindings);

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
        if (sceneManager.hasScene("game"))  {
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
    }
}