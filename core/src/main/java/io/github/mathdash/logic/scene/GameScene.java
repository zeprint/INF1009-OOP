package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.engine.ServiceLocator;
import io.github.mathdash.engine.collision.CollisionManager;
import io.github.mathdash.engine.difficulty.DifficultyAdapter;
import io.github.mathdash.engine.entity.EntityManager;
import io.github.mathdash.engine.entity.Renderable;
import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.engine.inputoutput.IInputSystem;
import io.github.mathdash.engine.inputoutput.InputAction;
import io.github.mathdash.engine.inputoutput.InputBindings;
import io.github.mathdash.engine.inputoutput.InputManager;
import io.github.mathdash.engine.movement.MovementManager;
import io.github.mathdash.engine.scene.Scene;
import io.github.mathdash.engine.scene.SceneManager;
import io.github.mathdash.engine.state.GameStateManager;
import io.github.mathdash.logic.collision.CollisionDispatcher;
import io.github.mathdash.logic.component.SurgeComponent;
import io.github.mathdash.logic.difficulty.MathDashDifficulty;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.AnswerBlockFactory;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.entity.ObstacleFactory;
import io.github.mathdash.logic.entity.Player;
import io.github.mathdash.logic.entity.PlayerFactory;
import io.github.mathdash.logic.math.MathQuestion;
import io.github.mathdash.logic.math.MathQuestionGenerator;
import io.github.mathdash.logic.render.BackgroundRenderer;
import io.github.mathdash.logic.render.EntityCleaner;
import io.github.mathdash.logic.render.EntitySpawner;
import io.github.mathdash.logic.render.HudRenderer;
import io.github.mathdash.logic.state.GameOverState;
import io.github.mathdash.logic.state.PlayingState;
import io.github.mathdash.logic.util.FontGenerator;
/**
 * GameScene - The main gameplay scene for MathDash.
 *
 * Design Patterns used here:
 *   - State (GameStateManager with PlayingState / GameOverState)
 *   - Factory Method (PlayerFactory, ObstacleFactory, AnswerBlockFactory)
 *   - Observer (CollisionDispatcher / CollisionHandler)
 *   - Strategy (MathQuestionGenerator delegates to Level1-4 strategies)
 *   - Service Locator (ServiceLocator for audio)
 *   - Template Method (DifficultyAdapter -> MathDashDifficulty)
 *
 * Delegates rendering and entity management to:
 *   - BackgroundRenderer: parallax sky, lane bands, decorations
 *   - HudRenderer: hearts, score, question, surge bar
 *   - EntitySpawner: obstacle and answer block spawning
 *   - EntityCleaner: off-screen entity removal
 */
public class GameScene extends Scene
    implements CollisionDispatcher.GameEventListener, PlayingState.PlayingCallback {

    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;

    private static final String ASSET_BASE = "kenney_new-platformer-pack-1.1/";
    private static final float BASE_SCROLL_SPEED = 200f;

    private final SceneManager sceneManager;
    private final int level;

    // Engine managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private IInputSystem inputManager;
    private GameStateManager gameStateManager;

    // Camera
    private OrthographicCamera camera;
    private Viewport viewport;

    // Textures
    private Texture bgTexture, grassBgTexture, dirtBgTexture;
    private Texture heartTexture, heartEmptyTexture;
    private Texture obstacleTexSaw, obstacleTexSpike, obstacleTexSlime;
    private Texture answerBlockTex;
    private Texture playerWalkA, playerWalkB, playerIdle, playerHit;
    private Texture decoGrassTex, decoBushTex;
    private Texture surgeBarBgTex, surgeBarFillTex, surgeTintTex;

    // Fonts
    private BitmapFont font, hudFont, questionFont, streakFont;
    private GlyphLayout glyphLayout;

    // Factories
    private PlayerFactory playerFactory;
    private ObstacleFactory obstacleFactory;
    private AnswerBlockFactory answerBlockFactory;

    // Game state
    private Player player;
    private CollisionDispatcher collisionDispatcher;
    private MathQuestionGenerator questionGenerator;
    private MathQuestion currentQuestion;
    private DifficultyAdapter difficulty;
    private SurgeComponent surgeComponent;
    private FontGenerator fontGenerator;
    private float scrollSpeed;
    private int score = 0;

    // Track active entities
    private Array<AnswerBlock> activeAnswers = new Array<>();
    private Array<Obstacle> activeObstacles = new Array<>();

    // Track surge state for updates
    private boolean previousSurgingState = false;

    // Extracted helpers
    private BackgroundRenderer backgroundRenderer;
    private HudRenderer hudRenderer;
    private EntitySpawner entitySpawner;
    private EntityCleaner entityCleaner;

    public GameScene(SceneManager sceneManager, int level) {
        super("game");
        this.sceneManager = sceneManager;
        this.level = level;
        this.scrollSpeed = BASE_SCROLL_SPEED;
    }

    @Override
    protected void onLoad() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        loadTextures();
        setupManagers();
        setupInput();
        setupFactories();
        setupHelpers();
        setupStates();
        spawnPlayer();
        generateNewQuestion();
    }

    private Texture loadTex(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return tex;
    }

    private void loadTextures() {
        fontGenerator = new FontGenerator();
        bgTexture = loadTex(ASSET_BASE + "Sprites/Backgrounds/Default/background_color_trees.png");
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        grassBgTexture = loadTex(ASSET_BASE + "Sprites/Backgrounds/Default/background_solid_grass.png");
        grassBgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        dirtBgTexture = loadTex(ASSET_BASE + "Sprites/Backgrounds/Default/cobble.png");
        dirtBgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        heartTexture = loadTex(ASSET_BASE + "Sprites/Tiles/Default/hud_heart.png");
        heartEmptyTexture = loadTex(ASSET_BASE + "Sprites/Tiles/Default/hud_heart_empty.png");

        obstacleTexSaw = loadTex(ASSET_BASE + "Sprites/Enemies/Default/saw_a.png");
        obstacleTexSpike = loadTex(ASSET_BASE + "Sprites/Tiles/Default/block_spikes.png");
        obstacleTexSlime = loadTex(ASSET_BASE + "Sprites/Enemies/Default/slime_spike_rest.png");

        answerBlockTex = loadTex(ASSET_BASE + "Sprites/Tiles/Default/block_green.png");

        playerWalkA = loadTex(ASSET_BASE + "Sprites/Characters/Default/character_green_walk_a.png");
        playerWalkB = loadTex(ASSET_BASE + "Sprites/Characters/Default/character_green_walk_b.png");
        playerIdle = loadTex(ASSET_BASE + "Sprites/Characters/Default/character_green_idle.png");
        playerHit = loadTex(ASSET_BASE + "Sprites/Characters/Default/character_green_hit.png");

        decoGrassTex = loadTex(ASSET_BASE + "Sprites/Tiles/Default/grass.png");
        decoBushTex = loadTex(ASSET_BASE + "Sprites/Tiles/Default/bush.png");

        font = fontGenerator.create(24, Color.WHITE);
        hudFont = fontGenerator.create(24, Color.BLACK);
        questionFont = fontGenerator.create(32, Color.YELLOW, Color.DARK_GRAY, 1f);
        streakFont = fontGenerator.create(20, Color.ORANGE, Color.DARK_GRAY, 1f);
        glyphLayout = new GlyphLayout();

        surgeBarBgTex = createPixmapTexture(0.2f, 0.2f, 0.2f, 0.7f);
        surgeBarFillTex = createPixmapTexture(0.2f, 0.9f, 0.3f, 0.9f);
        surgeTintTex = createPixmapTexture(1f, 0.85f, 0f, 0.12f);
    }

    private Texture createPixmapTexture(float r, float g, float b, float a) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(r, g, b, a);
        pm.fill();
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    private void setupManagers() {
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        movementManager = new MovementManager();
        difficulty = new MathDashDifficulty();
        surgeComponent = new SurgeComponent();
    }

    private void setupInput() {
        InputBindings bindings = new InputBindings();
        bindings.bindAction(InputAction.JUMP, Input.Keys.UP);
        bindings.bindAction(InputAction.JUMP, Input.Keys.W);
        bindings.bindAction(InputAction.CONFIRM, Input.Keys.DOWN);
        bindings.bindAction(InputAction.CONFIRM, Input.Keys.S);
        bindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.ESCAPE);
        bindings.bindAction(InputAction.TOGGLE_PAUSE, Input.Keys.P);
        bindings.bindAction(InputAction.TOGGLE_MUTE, Input.Keys.M);
        inputManager = new InputManager(bindings);
    }

    private void setupFactories() {
        Renderable renderWalkA = new Renderable(new TextureRegion(playerWalkA), 48, 48);
        Renderable renderWalkB = new Renderable(new TextureRegion(playerWalkB), 48, 48);
        Renderable renderIdle  = new Renderable(new TextureRegion(playerIdle),  48, 48);
        Renderable renderHit   = new Renderable(new TextureRegion(playerHit),   48, 48);
        Renderable renderSaw   = new Renderable(new TextureRegion(obstacleTexSaw),   48, 48);
        Renderable renderSpike = new Renderable(new TextureRegion(obstacleTexSpike), 48, 48);
        Renderable renderSlime = new Renderable(new TextureRegion(obstacleTexSlime), 48, 48);
        Renderable renderAnswerBlock = new Renderable(new TextureRegion(answerBlockTex), 48, 48);

        playerFactory = new PlayerFactory(renderWalkA, renderWalkB, renderIdle, renderHit);
        obstacleFactory = new ObstacleFactory(scrollSpeed, renderSaw, renderSpike, renderSlime);
        answerBlockFactory = new AnswerBlockFactory(renderAnswerBlock, scrollSpeed);
    }

    private void setupHelpers() {
        backgroundRenderer = new BackgroundRenderer(WORLD_WIDTH, WORLD_HEIGHT, bgTexture, grassBgTexture, dirtBgTexture,
            decoGrassTex, decoBushTex);
        hudRenderer = new HudRenderer(heartTexture, heartEmptyTexture,
            surgeBarBgTex, surgeBarFillTex, surgeTintTex,
            font, hudFont, questionFont, streakFont, glyphLayout);
    }

    private void setupStates() {
        gameStateManager = new GameStateManager();
        gameStateManager.addState(new PlayingState(this));
        gameStateManager.addState(new GameOverState(() -> handleGameOver()));
        gameStateManager.setState("playing");
    }

    private void spawnPlayer() {
        IAudioSystem audio = ServiceLocator.getAudio();
        player = playerFactory.create(120f, Player.LANE_Y[0]);

        // Attach SurgeComponent to Player entity for ECS consistency
        player.addComponent(surgeComponent);

        collisionDispatcher = new CollisionDispatcher(audio, this);
        player.setCollisionHandler(collisionDispatcher);

        entityManager.addEntity(player);
        collisionManager.addObject(player);

        // Create spawner and cleaner after player (needs collisionDispatcher)
        entitySpawner = new EntitySpawner(WORLD_WIDTH, obstacleFactory, answerBlockFactory,
            entityManager, collisionManager, movementManager, collisionDispatcher,
            activeAnswers, activeObstacles);
        entityCleaner = new EntityCleaner(collisionManager, entityManager, movementManager);
    }

    private void generateNewQuestion() {
        questionGenerator = new MathQuestionGenerator(level);
        currentQuestion = questionGenerator.generate();
        entitySpawner.resetAnswerTimer();
    }

    // ---- Scene lifecycle ----

    @Override
    public void update(float deltaTime) {
        gameStateManager.update(deltaTime);
    }

    @Override
    public void onPlayingUpdate(float deltaTime) {
        IAudioSystem audio = ServiceLocator.getAudio();
        inputManager.update();

        // Pause
        if (inputManager.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.setScene("pause");
            return;
        }

        // Mute toggle via InputManager
        if (inputManager.isActionTriggered(InputAction.TOGGLE_MUTE)) {
            if (audio != null) {
                audio.setMuted(!audio.isMuted());
            }
        }

        // Player lane switching
        if (inputManager.isActionTriggered(InputAction.JUMP)) {
            player.switchLane(1);
            if (audio != null) audio.playSound("jump");
        }
        if (inputManager.isActionTriggered(InputAction.CONFIRM)) {
            player.switchLane(-1);
            if (audio != null) audio.playSound("jump");
        }

        // Update game systems
        collisionDispatcher.update(deltaTime);
        entityManager.update(deltaTime);
        collisionManager.checkCollisions();

        // Apply adaptive difficulty + surge speed
        scrollSpeed = BASE_SCROLL_SPEED * difficulty.getSpeedMultiplier()
            * surgeComponent.getSpeedBonus();

        // Detect surge state changes and update entity speeds
        boolean currentSurgingState = surgeComponent.isSurging();
        if (currentSurgingState != previousSurgingState) {
            previousSurgingState = currentSurgingState;
            updateScrollSpeeds();
        }

        // Delegate to helpers
        backgroundRenderer.update(deltaTime, scrollSpeed);
        entitySpawner.update(deltaTime, scrollSpeed, currentQuestion);

        boolean allAnswersGone = entityCleaner.cleanup(activeObstacles, activeAnswers);
        if (entitySpawner.isAnswersOnScreen() && allAnswersGone) {
            generateNewQuestion();
        }
    }

    // ---- Rendering ----

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        backgroundRenderer.render(batch);
        entityManager.render(batch);
        hudRenderer.render(batch, player, surgeComponent, difficulty,
            currentQuestion, score, level, activeAnswers);

        batch.end();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    // ---- CollisionDispatcher.GameEventListener callbacks ----

    @Override
    public void onHealthChanged(int newHealth) { }

    private void updateScrollSpeeds() {
        float newSpeed = BASE_SCROLL_SPEED * difficulty.getSpeedMultiplier()
            * surgeComponent.getSpeedBonus();
        
        // Update factories for future spawns
        obstacleFactory.setScrollSpeed(newSpeed);
        answerBlockFactory.setScrollSpeed(newSpeed);
        
        // Update all existing entities on screen
        for (Obstacle obs : activeObstacles) {
            obs.setScrollSpeed(newSpeed);
        }
        for (AnswerBlock block : activeAnswers) {
            block.setScrollSpeed(newSpeed);
        }
    }

    @Override
    public void onObstacleHit() {
        difficulty.onObstacleHit();
        surgeComponent.resetSurge();
        updateScrollSpeeds();
    }

    @Override
    public void onPlayerDeath() {
        gameStateManager.setState("gameover");
    }

    @Override
    public void onCorrectAnswer() {
        score++;
        difficulty.onCorrect();
        surgeComponent.addSurge();

        updateScrollSpeeds();
        entitySpawner.clearAnswerBlocks();
        generateNewQuestion();
    }

    @Override
    public void onWrongAnswer() {
        difficulty.onWrong();
        surgeComponent.resetSurge();

        updateScrollSpeeds();
        entitySpawner.clearAnswerBlocks();
        generateNewQuestion();
    }

    private void handleGameOver() {
        saveHighScore();
        Scene deathSceneRef = sceneManager.getScene("death");
        if (deathSceneRef instanceof DeathScene) {
            ((DeathScene) deathSceneRef).setFinalScore(score);
            ((DeathScene) deathSceneRef).setLevel(level);
        }
        sceneManager.setScene("death");
    }

    private void saveHighScore() {
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("MathDash");
        String key = "highscore_level_" + level;
        int current = prefs.getInteger(key, 0);
        if (score > current) {
            prefs.putInteger(key, score);
            prefs.flush();
        }
    }

    public int getScore() { return score; }
    public int getLevel() { return level; }

    // ---- Lifecycle ----

    @Override
    protected void onUnload() {
        if (entityManager != null) entityManager.dispose();
        if (collisionManager != null) collisionManager.clear();
        if (inputManager != null) inputManager.dispose();

        disposeTexture(bgTexture);
        disposeTexture(grassBgTexture);
        disposeTexture(dirtBgTexture);
        disposeTexture(decoGrassTex);
        disposeTexture(decoBushTex);
        disposeTexture(heartTexture);
        disposeTexture(heartEmptyTexture);
        disposeTexture(obstacleTexSaw);
        disposeTexture(obstacleTexSpike);
        disposeTexture(obstacleTexSlime);
        disposeTexture(answerBlockTex);
        disposeTexture(playerWalkA);
        disposeTexture(playerWalkB);
        disposeTexture(playerIdle);
        disposeTexture(playerHit);
        if (font != null) font.dispose();
        if (hudFont != null) hudFont.dispose();
        if (questionFont != null) questionFont.dispose();
        if (streakFont != null) streakFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        disposeTexture(surgeBarBgTex);
        disposeTexture(surgeBarFillTex);
        disposeTexture(surgeTintTex);
    }

    private void disposeTexture(Texture tex) {
        if (tex != null) tex.dispose();
    }

    @Override
    protected void onShow() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if (movementManager != null) movementManager.unfreezeAll();
        if (gameStateManager != null && !"playing".equals(gameStateManager.getCurrentStateName())) {
            if (!"gameover".equals(gameStateManager.getCurrentStateName())) {
                gameStateManager.setState("playing");
            }
        }
    }

    @Override
    protected void onHide() {
        if (movementManager != null) movementManager.freezeAll();
    }
}
