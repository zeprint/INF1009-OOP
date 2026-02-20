package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * GameMaster - Core engine coordinator (non-contextual).
 * 
 * Owns and initialises all engine managers, delegates the game loop,
 * and manages shared rendering resources.
 * All managers are stored and exposed as interfaces so the
 * concrete implementations can be swapped without changing this class.
 */
public class GameMaster extends ApplicationAdapter {

    // Engine managers (DIP: stored as interfaces)
    private ISceneSystem     sceneSystem;
    private IEntitySystem    entitySystem;
    private IMovementSystem  movementSystem;
    private ICollisionSystem collisionSystem;
    private IInputSystem     inputSystem;
    private IAudioSystem     audioSystem;

    // Shared rendering resources
    private SpriteBatch   spriteBatch;
    private ShapeRenderer shapeRenderer;

    // Scene management: Logic Engine injection point
    private Scene logicEngineScene;  // Injected by Logic Engine if present (null = fallback mode)
    private SampleLogicScreen fallbackScreen;  // Error handling fallback when no Logic Engine

    // --- ApplicationAdapter lifecycle ---

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

        inputSystem.update();

        // Check if Logic Engine is present
        if (logicEngineScene != null) {
            // Logic Engine is loaded: use its scene
            logicEngineScene.update(dt);
            logicEngineScene.render();
        } else {
            // Logic Engine NOT loaded: use fallback error handling screen
            fallbackScreen.update(dt);
            fallbackScreen.render();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        sceneSystem.dispose();
        audioSystem.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    // --- Initialisation helpers ---

    private void initRendering() {
        spriteBatch   = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private void initManagers() {
        entitySystem    = new EntityManager();
        movementSystem  = new MovementManager();
        collisionSystem = new CollisionManager();
        audioSystem     = new AudioManager();
        sceneSystem     = new SceneManager();
    }

    private void initInput() {
        InputBindings bindings = new InputBindings();

        // Movement: A/D or Left/Right arrows
        bindings.bindAxis(InputAxis.MOVE_X,
                com.badlogic.gdx.Input.Keys.A,
                com.badlogic.gdx.Input.Keys.D);
        bindings.bindAxis(InputAxis.MOVE_X,
                com.badlogic.gdx.Input.Keys.LEFT,
                com.badlogic.gdx.Input.Keys.RIGHT);

        bindings.bindAxis(InputAxis.MOVE_Y,
                com.badlogic.gdx.Input.Keys.S,
                com.badlogic.gdx.Input.Keys.W);

        // Actions
        bindings.bindAction(InputAction.TOGGLE_MUTE,  com.badlogic.gdx.Input.Keys.M);
        bindings.bindAction(InputAction.TOGGLE_DEBUG,  com.badlogic.gdx.Input.Keys.F1);

        inputSystem = new InputManager(bindings);
    }

    private void loadAssets() {
        try {
            audioSystem.loadSound("click", "click.wav");
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Asset load failed: " + e.getMessage());
        }
    }

    private void initScenes() {
        // Initialize fallback/error handling screen (displayed when no Logic Engine is loaded)
        fallbackScreen = new SampleLogicScreen(spriteBatch, shapeRenderer);
        fallbackScreen.create();
        
        // Logic Engine injection point: remains null until Logic Engine loads
        logicEngineScene = null;
        
        Gdx.app.log("GameMaster", "Engine initialized. Waiting for Logic Engine...");
    }

    // --- Public accessors for Logic Engine (DIP: return interfaces) ---

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

    public void setLogicEngineScene(Scene scene) {
        if (scene != null) {
            logicEngineScene = scene;
            logicEngineScene.create();
            Gdx.app.log("GameMaster", "Logic Engine scene LOADED. Fallback screen disabled.");
        } else {
            logicEngineScene = null;
            Gdx.app.log("GameMaster", "Logic Engine scene UNLOADED. Fallback screen ACTIVE.");
        }
    }

    public boolean isLogicEngineLoaded() {
        return logicEngineScene != null;
    }}