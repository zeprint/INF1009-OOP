package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.AbstractEngine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.logic.scene.EndScene;
import io.github.some_example_name.lwjgl3.logic.scene.GameScene;
import io.github.some_example_name.lwjgl3.logic.scene.MainMenuScene;
import io.github.some_example_name.lwjgl3.logic.scene.PauseScene;

/**
 * GameMaster - Core engine coordinator (non-contextual).
 *
 * Owns and initialises all engine managers, delegates the game loop
 * to the SceneManager, and manages shared rendering resources.
 *
 * Uses a ScreenViewport to handle window resize / maximise correctly.
 * The viewport updates the OrthographicCamera projection on resize,
 * which is applied to SpriteBatch and ShapeRenderer every frame.
 */
public class GameMaster extends ApplicationAdapter {

    private ISceneManager    sceneManager;
    private EntityManager    entityManager;
    private MovementManager  movementManager;
    private CollisionManager collisionManager;
    private IInputSystem     inputSystem;
    private IAudioSystem     audioSystem;

    private SpriteBatch   spriteBatch;
    private ShapeRenderer shapeRenderer;

    // Camera and viewport for resize support
    private OrthographicCamera camera;
    private Viewport viewport;

    // Scene name constants
    public static final String SCENE_MAIN_MENU = "MainMenu";
    public static final String SCENE_GAME      = "GameScene";
    public static final String SCENE_PAUSE     = "PauseScene";
    public static final String SCENE_END       = "EndScene";

    @Override
    public void create() {
        initRendering();
        initManagers();
        initInput();
        loadAssets();
        initScenes();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera and apply to both renderers every frame
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float dt = Gdx.graphics.getDeltaTime();
        inputSystem.update();
        sceneManager.update(dt);
        sceneManager.render();
    }

    /**
     * Handles window resize and maximise. Updates the viewport so
     * the camera projection matches the new window dimensions.
     * Without this, SpriteBatch/ShapeRenderer project to the old
     * window size and nothing renders on the new screen area.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        audioSystem.dispose();
        inputSystem.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    private void initRendering() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        viewport.update(w, h, true);
        camera.position.set(w / 2f, h / 2f, 0);
        camera.update();

        spriteBatch   = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void initManagers() {
        entityManager    = new EntityManager();
        movementManager  = new MovementManager();
        collisionManager = new CollisionManager();
        audioSystem      = new AudioManager();
        sceneManager     = new SceneManager();
    }

    private void initInput() {
        InputBindings bindings = new InputBindings();

        bindings.bindAxis(InputAxis.MOVE_X,
                com.badlogic.gdx.Input.Keys.A,
                com.badlogic.gdx.Input.Keys.D);
        bindings.bindAxis(InputAxis.MOVE_X,
                com.badlogic.gdx.Input.Keys.LEFT,
                com.badlogic.gdx.Input.Keys.RIGHT);

        bindings.bindAction(InputAction.JUMP, com.badlogic.gdx.Input.Keys.W);
        bindings.bindAction(InputAction.JUMP, com.badlogic.gdx.Input.Keys.UP);
        bindings.bindAction(InputAction.JUMP, com.badlogic.gdx.Input.Keys.SPACE);

        bindings.bindAction(InputAction.SHIELD, com.badlogic.gdx.Input.Keys.S);
        bindings.bindAction(InputAction.SHIELD, com.badlogic.gdx.Input.Keys.DOWN);

        bindings.bindAction(InputAction.TOGGLE_PAUSE, com.badlogic.gdx.Input.Keys.P);
        bindings.bindAction(InputAction.BACK_TO_MENU, com.badlogic.gdx.Input.Keys.ESCAPE);
        bindings.bindAction(InputAction.CONFIRM, com.badlogic.gdx.Input.Keys.ENTER);
        bindings.bindAction(InputAction.TOGGLE_MUTE, com.badlogic.gdx.Input.Keys.M);
        bindings.bindAction(InputAction.TOGGLE_DEBUG, com.badlogic.gdx.Input.Keys.F1);

        inputSystem = new InputManager(bindings);
    }

    private void loadAssets() {
        try {
            audioSystem.loadSound("click",        "click.wav");
            audioSystem.loadSound("correct",      "correct.wav");
            audioSystem.loadSound("wrong",        "wrong.wav");
            audioSystem.loadSound("jump",         "jump.wav");
            audioSystem.loadSound("death",        "death.wav");
            audioSystem.loadSound("hurt",         "wrong.wav");
            audioSystem.loadSound("obstacle_hit", "wrong.wav");
            audioSystem.loadMusic("bgm",          "bgm.mp3");
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Asset load failed: " + e.getMessage());
        }
    }

    private void initScenes() {
        MainMenuScene mainMenu = new MainMenuScene(sceneManager, spriteBatch, inputSystem, audioSystem);
        GameScene     game     = new GameScene(sceneManager, spriteBatch, shapeRenderer,
                                               inputSystem, audioSystem,
                                               entityManager, movementManager, collisionManager);
        PauseScene    pause    = new PauseScene(sceneManager, inputSystem);
        EndScene      end      = new EndScene(sceneManager, spriteBatch, inputSystem, audioSystem);

        sceneManager.addScene(SCENE_MAIN_MENU, mainMenu);
        sceneManager.addScene(SCENE_GAME,      game);
        sceneManager.addScene(SCENE_PAUSE,     pause);
        sceneManager.addScene(SCENE_END,       end);

        sceneManager.loadScene(SCENE_MAIN_MENU);
        Gdx.app.log("GameMaster", "All scenes registered. MainMenu loaded.");
    }

    public ISceneManager getSceneManager()       { return sceneManager; }
    public EntityManager getEntityManager()      { return entityManager; }
    public MovementManager getMovementManager()  { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IInputSystem getInputSystem()         { return inputSystem; }
    public IAudioSystem getAudioSystem()         { return audioSystem; }
    public SpriteBatch getSpriteBatch()          { return spriteBatch; }
    public ShapeRenderer getShapeRenderer()      { return shapeRenderer; }
}
