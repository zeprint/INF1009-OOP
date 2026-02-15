package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
* GameMaster - Core engine coordinator
* Owns and initialises all engine managers, delegates the game loop,
* and manages shared rendering resources.
*
* All managers are stored and exposed as interfaces so the
* concrete implementations can be swapped without changing this class.
*
* SCENES manages 2 scenes, GameScene (play) and PauseScene (pause).
* The game scene is always rendered when paused,
* the PauseScene overlay is drawn on top with a semi-transparent background.
*/
public class GameMaster extends ApplicationAdapter {

    // Engine managers, stored as interfaces for DIP compliance
    private ISceneSystem sceneSystem;
    private IEntitySystem entitySystem;
    private IMovementSystem movementSystem;
    private ICollisionSystem collisionSystem;
    private IInputSystem inputSystem;
    private IAudioSystem audioSystem;

    // Shared rendering resources
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    // Scene references for overlay rendering
    private GameScene gameScene;
    private PauseScene pauseScene;
    private boolean paused = false;

    // ApplicationAdapter lifecycle
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

        float dt = Gdx.graphics.getDeltaTime();

        // Always poll input (even during pause, to detect unpause)
        inputSystem.update();

        // Handle pause toggle (P key)
        if (inputSystem.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            paused = !paused;
        }

        // Update game logic only when not paused
        if (!paused) {
            gameScene.update(dt);
        }

        // Always render the game scene (frozen when paused)
        gameScene.render();

        // Render pause overlay on top when paused
        if (paused) {
            pauseScene.render();
        }
    }
    // Forward to scenes if they need to react
    @Override
    public void resize(int width, int height) {
        // scene has full control over how to handle resize
    }
    // dispose() is called when application is destoryed, dispose all resources to prevent memory leaks issue
    @Override
    public void dispose() {
        sceneSystem.dispose();
        audioSystem.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
    // called in create() to set up the engine before the game loop starts
    private void initRendering() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }
    // called in create() to set up the engine before the game loop starts, create all managers and systems
    private void initManagers() {
        entitySystem = new EntityManager();
        movementSystem = new MovementManager();
        collisionSystem = new CollisionManager();
        audioSystem = new AudioManager();
        sceneSystem = new SceneManager();
    }
    // called in create() to set up the engine before the game loop starts, set up input bindings and input manager
    private void initInput() {
        InputBindings bindings = new InputBindings();

        // Movement: A/D or Left/Right arrows keys
        bindings.bindAxis(InputAxis.MOVE_X, com.badlogic.gdx.Input.Keys.A, com.badlogic.gdx.Input.Keys.D);
        bindings.bindAxis(InputAxis.MOVE_X, com.badlogic.gdx.Input.Keys.LEFT, com.badlogic.gdx.Input.Keys.RIGHT);

        bindings.bindAxis(InputAxis.MOVE_Y, com.badlogic.gdx.Input.Keys.S, com.badlogic.gdx.Input.Keys.W);

        // Actions
        bindings.bindAction(InputAction.TOGGLE_MUTE, com.badlogic.gdx.Input.Keys.M);
        bindings.bindAction(InputAction.TOGGLE_DEBUG, com.badlogic.gdx.Input.Keys.F1);
        bindings.bindAction(InputAction.TOGGLE_PAUSE, com.badlogic.gdx.Input.Keys.P);
        bindings.bindAction(InputAction.TOGGLE_PAUSE, com.badlogic.gdx.Input.Keys.ESCAPE);

        inputSystem = new InputManager(bindings);
    }

    private void loadAssets() {
        try {
            audioSystem.loadSound("click", "click.wav");
        } 
        catch (Exception e) {
            Gdx.app.error("GameMaster", "Asset load failed: " + e.getMessage());
        }
    }

    private void initScenes() {
        // Scene 1, main game scene with everything needed for gameplay
        gameScene = new GameScene(
                entitySystem,
                movementSystem,
                collisionSystem,
                inputSystem,
                audioSystem,
                spriteBatch,
                shapeRenderer);

        // Scene 2, to display pause overlay
        pauseScene = new PauseScene();

        // Register both with SceneManager for lifecycle management
        sceneSystem.addScene("simulation", gameScene);
        sceneSystem.addScene("pause", pauseScene);

        // Initial load of both scenes, creates resources
        sceneSystem.loadScene("simulation");
        pauseScene.create();
    }

    // Public accessors for Logic Engine, DIP: return interfaces
    public ISceneSystem getSceneSystem() {
        return sceneSystem;
    }

    public IEntitySystem getEntitySystem() {
        return entitySystem;
    }

    public IMovementSystem getMovementSystem() {
        return movementSystem;
    }

    public ICollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public IInputSystem getInputSystem() {
        return inputSystem;
    }

    public IAudioSystem getAudioSystem() {
        return audioSystem;
    }
}
