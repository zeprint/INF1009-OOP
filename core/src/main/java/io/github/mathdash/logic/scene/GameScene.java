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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.AbstractEngine.ServiceLocator;
import io.github.mathdash.AbstractEngine.collision.CollisionManager;
import io.github.mathdash.AbstractEngine.entity.EntityManager;
import io.github.mathdash.AbstractEngine.entity.Renderable;
import io.github.mathdash.AbstractEngine.entity.Transform;
import io.github.mathdash.AbstractEngine.inputouput.IAudioSystem;
import io.github.mathdash.AbstractEngine.inputouput.InputAction;
import io.github.mathdash.AbstractEngine.inputouput.InputBindings;
import io.github.mathdash.AbstractEngine.inputouput.InputManager;
import io.github.mathdash.AbstractEngine.movement.MovementManager;
import io.github.mathdash.AbstractEngine.scene.Scene;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.AbstractEngine.state.GameStateManager;
import io.github.mathdash.logic.Collision.CollisionDispatcher;
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
import io.github.mathdash.logic.movement.ScrollMovement;
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
 * Gameplay Features:
 *   - Adaptive Difficulty: speed adjusts based on player performance
 *   - Surge Meter: correct answers fill a meter; when full, triggers invincibility + speed burst
 *
 * Uses engine managers: EntityManager, CollisionManager, MovementManager, InputManager.
 */
public class GameScene extends Scene
    implements CollisionDispatcher.GameEventListener, PlayingState.PlayingCallback {

    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;

    private static final String ASSET_BASE = "kenney_new-platformer-pack-1.1/";
    private static final float BASE_SCROLL_SPEED = 200f;
    private static final float SPAWN_INTERVAL_BASE = 3.0f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2.0f;

    // Lane background band definitions [yStart, height]
    private static final float[][] DIRT_BANDS = {
        {50f, 100f},   // Lane 1 (center=100)
        {200f, 100f},  // Lane 2 (center=250)
        {350f, 100f},  // Lane 3 (center=400)
    };
    private static final float[][] GRASS_BANDS = {
        {0f, 50f},
        {150f, 50f},
        {300f, 50f},
        {450f, 30f},
    };
    private static final float SKY_Y = 480f;

    private final SceneManager sceneManager;
    private final int level;

    // Engine managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private InputManager inputManager;

    // State pattern
    private GameStateManager gameStateManager;

    // Camera
    private OrthographicCamera camera;
    private Viewport viewport;

    // Textures (rendering resources - loaded in scene, which is allowed to use LibGDX)
    private Texture bgTexture;
    private Texture grassBgTexture;
    private Texture dirtBgTexture;
    private Texture heartTexture;
    private Texture heartEmptyTexture;
    private Texture obstacleTexSaw;
    private Texture obstacleTexSpike;
    private Texture obstacleTexSlime;
    private Texture answerBlockTex;
    private Texture playerWalkA, playerWalkB, playerIdle, playerHit;
    private Texture decoGrassTex;
    private Texture decoBushTex;

    // Renderable templates (passed to logic-layer factories)
    private Renderable renderWalkA, renderWalkB, renderIdle, renderHit;
    private Renderable renderSaw, renderSpike, renderSlime;
    private Renderable renderAnswerBlock;

    // Fonts
    private BitmapFont font;
    private BitmapFont hudFont;
    private BitmapFont questionFont;
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

    private float scrollSpeed;
    private float bgScrollX = 0f;
    private float floorScrollX = 0f;
    private float obstacleSpawnTimer = 0f;
    private float answerSpawnTimer = 0f;
    private boolean answersOnScreen = false;
    private int score = 0;

    // Adaptive difficulty + Surge meter
    private MathDashDifficulty difficulty;
    private SurgeComponent surgeComponent;
    private Texture surgeBarBgTex;   // grey background bar
    private Texture surgeBarFillTex; // fill (green -> gold)
    private Texture surgeTintTex;    // golden screen tint during surge
    private BitmapFont streakFont;

    // Track active answer blocks and obstacles
    private Array<AnswerBlock> activeAnswers = new Array<>();
    private Array<Obstacle> activeObstacles = new Array<>();

    // Decorations
    private Array<float[]> decorations = new Array<>();
    private static final float DECO_SPAWN_INTERVAL = 80f;
    private float decoSpawnAccum = 0f;

    private static final float ANSWER_SAFE_DISTANCE = 100f;

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
        createRenderables();
        setupManagers();
        setupInput();
        setupFactories();
        setupStates();
        spawnPlayer();
        generateNewQuestion();

        // Pre-populate decorations across the screen
        for (float x = 0; x < WORLD_WIDTH; x += DECO_SPAWN_INTERVAL) {
            float[] band = GRASS_BANDS[MathUtils.random(GRASS_BANDS.length - 1)];
            float y = band[0] + MathUtils.random(0f, Math.max(0f, band[1] - 48f));
            float texIdx = MathUtils.randomBoolean() ? 0f : 1f;
            decorations.add(new float[]{x + MathUtils.random(-20f, 20f), y, texIdx});
        }
    }

    private Texture loadTex(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return tex;
    }

    private void loadTextures() {
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

        font = FontGenerator.create(24, Color.WHITE);
        hudFont = FontGenerator.create(24, Color.BLACK);
        questionFont = FontGenerator.create(32, Color.YELLOW, Color.DARK_GRAY, 1f);
        streakFont = FontGenerator.create(20, Color.ORANGE, Color.DARK_GRAY, 1f);
        glyphLayout = new GlyphLayout();

        // Surge bar textures (created procedurally)
        Pixmap barBg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        barBg.setColor(0.2f, 0.2f, 0.2f, 0.7f);
        barBg.fill();
        surgeBarBgTex = new Texture(barBg);
        barBg.dispose();

        Pixmap barFill = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        barFill.setColor(0.2f, 0.9f, 0.3f, 0.9f);
        barFill.fill();
        surgeBarFillTex = new Texture(barFill);
        barFill.dispose();

        Pixmap tint = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        tint.setColor(1f, 0.85f, 0f, 0.12f);
        tint.fill();
        surgeTintTex = new Texture(tint);
        tint.dispose();
    }

    /** Creates Renderable templates from loaded textures for the logic-layer factories. */
    private void createRenderables() {
        renderWalkA = new Renderable(new TextureRegion(playerWalkA), 48, 48);
        renderWalkB = new Renderable(new TextureRegion(playerWalkB), 48, 48);
        renderIdle  = new Renderable(new TextureRegion(playerIdle),  48, 48);
        renderHit   = new Renderable(new TextureRegion(playerHit),   48, 48);

        renderSaw   = new Renderable(new TextureRegion(obstacleTexSaw),   48, 48);
        renderSpike = new Renderable(new TextureRegion(obstacleTexSpike), 48, 48);
        renderSlime = new Renderable(new TextureRegion(obstacleTexSlime), 48, 48);

        renderAnswerBlock = new Renderable(new TextureRegion(answerBlockTex), 48, 48);
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
        inputManager = new InputManager(bindings);
    }

    private void setupFactories() {
        playerFactory = new PlayerFactory(renderWalkA, renderWalkB, renderIdle, renderHit);
        obstacleFactory = new ObstacleFactory(scrollSpeed, renderSaw, renderSpike, renderSlime);
        answerBlockFactory = new AnswerBlockFactory(renderAnswerBlock, scrollSpeed);
    }

    /** Sets up the State pattern: PlayingState and GameOverState. */
    private void setupStates() {
        gameStateManager = new GameStateManager();

        PlayingState playingState = new PlayingState(this);
        GameOverState gameOverState = new GameOverState(() -> handleGameOver());

        gameStateManager.addState(playingState);
        gameStateManager.addState(gameOverState);

        gameStateManager.setState("playing");
    }

    private void spawnPlayer() {
        IAudioSystem audio = ServiceLocator.getAudio();
        player = playerFactory.create(120f, Player.LANE_Y[0]);

        collisionDispatcher = new CollisionDispatcher(audio, this);
        player.setCollisionHandler(collisionDispatcher);

        entityManager.addEntity(player);
        collisionManager.addObject(player);
    }

    private void generateNewQuestion() {
        questionGenerator = new MathQuestionGenerator(level);
        currentQuestion = questionGenerator.generate();
        answersOnScreen = false;
        answerSpawnTimer = 0f;
    }

    // ---- Scene lifecycle ----

    @Override
    public void update(float deltaTime) {
        // Delegate to the current state (Playing or GameOver)
        gameStateManager.update(deltaTime);
    }

    /**
     * Called by PlayingState via the PlayingCallback interface.
     * Contains all active gameplay logic.
     */
    @Override
    public void onPlayingUpdate(float deltaTime) {
        IAudioSystem audio = ServiceLocator.getAudio();

        inputManager.update();

        // Pause
        if (inputManager.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.setScene("pause");
            return;
        }

        // M key: toggle mute mid-game
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
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

        collisionDispatcher.setSurging(surgeComponent.isSurging());
        collisionDispatcher.update(deltaTime);
        surgeComponent.update(deltaTime);
        entityManager.update(deltaTime);
        collisionManager.checkCollisions();

        // Apply adaptive difficulty + surge speed to scrollSpeed
        float effectiveSpeed = BASE_SCROLL_SPEED * difficulty.getSpeedMultiplier()
            * surgeComponent.getSpeedBonus();
        scrollSpeed = effectiveSpeed;

        // Scroll background
        bgScrollX += scrollSpeed * 0.3f * deltaTime;
        floorScrollX += scrollSpeed * deltaTime;

        // Update decorations
        float decoMove = scrollSpeed * deltaTime;
        for (int i = decorations.size - 1; i >= 0; i--) {
            float[] d = decorations.get(i);
            d[0] -= decoMove;
            if (d[0] < -50f) {
                decorations.removeIndex(i);
            }
        }
        decoSpawnAccum += decoMove;
        while (decoSpawnAccum >= DECO_SPAWN_INTERVAL) {
            decoSpawnAccum -= DECO_SPAWN_INTERVAL;
            float[] band = GRASS_BANDS[MathUtils.random(GRASS_BANDS.length - 1)];
            float y = band[0] + MathUtils.random(0f, Math.max(0f, band[1] - 48f));
            float texIdx = MathUtils.randomBoolean() ? 0f : 1f;
            decorations.add(new float[]{WORLD_WIDTH + MathUtils.random(0f, 40f), y, texIdx});
        }

        // Spawn obstacles
        obstacleSpawnTimer += deltaTime;
        float obstacleInterval = OBSTACLE_SPAWN_INTERVAL * (BASE_SCROLL_SPEED / scrollSpeed);
        if (obstacleSpawnTimer >= obstacleInterval) {
            obstacleSpawnTimer = 0f;
            spawnObstacle();
        }

        // Spawn answer blocks
        if (!answersOnScreen) {
            answerSpawnTimer += deltaTime;
            float answerInterval = SPAWN_INTERVAL_BASE * (BASE_SCROLL_SPEED / scrollSpeed);
            if (answerSpawnTimer >= answerInterval) {
                spawnAnswerBlocks();
                answersOnScreen = true;
            }
        }

        cleanupEntities();
    }

    // ---- Spawning ----

    private void spawnObstacle() {
        float spawnX = WORLD_WIDTH + 50f;

        // Don't spawn if any active answer block is within safe distance
        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform bt = block.getComponent(Transform.class);
            if (bt != null && Math.abs(bt.getX() - spawnX) < ANSWER_SAFE_DISTANCE) {
                return;
            }
        }

        // Don't spawn if answer blocks are about to spawn soon
        if (!answersOnScreen) {
            float answerInterval = SPAWN_INTERVAL_BASE * (BASE_SCROLL_SPEED / scrollSpeed);
            float timeUntilAnswers = answerInterval - answerSpawnTimer;
            float safeTime = ANSWER_SAFE_DISTANCE / scrollSpeed;
            if (timeUntilAnswers <= safeTime) {
                return;
            }
        }

        int lane = MathUtils.random(0, 2);
        float y = Player.LANE_Y[lane];

        Obstacle obs = obstacleFactory.create(spawnX, y);
        movementManager.add(obs.getComponent(ScrollMovement.class));
        obs.setCollisionHandler(collisionDispatcher);
        obs.setScrollSpeed(scrollSpeed);
        entityManager.addEntity(obs);
        collisionManager.addObject(obs);
        activeObstacles.add(obs);
    }

    private void spawnAnswerBlocks() {
        if (currentQuestion == null) return;

        float x = WORLD_WIDTH + 50f;

        int[] laneOrder = {0, 1, 2};
        shuffleLanes(laneOrder);

        int[] answers = {
            currentQuestion.getCorrectAnswer(),
            currentQuestion.getWrongAnswer1(),
            currentQuestion.getWrongAnswer2()
        };
        boolean[] isCorrect = {true, false, false};

        for (int i = 0; i < 3; i++) {
            float y = Player.LANE_Y[laneOrder[i]];
            AnswerBlock block = answerBlockFactory.create(x, y, answers[i], isCorrect[i]);
            movementManager.add(block.getComponent(ScrollMovement.class));
            block.setCollisionHandler(collisionDispatcher);
            block.setScrollSpeed(scrollSpeed);
            entityManager.addEntity(block);
            collisionManager.addObject(block);
            activeAnswers.add(block);
        }
    }

    private void shuffleLanes(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = MathUtils.random(0, i);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // ---- Cleanup ----

    private void cleanupEntities() {
        for (int i = activeObstacles.size - 1; i >= 0; i--) {
            Obstacle obs = activeObstacles.get(i);
            Transform t = obs.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !obs.isActive()) {
                collisionManager.removeObject(obs);
                entityManager.removeEntity(obs);
                movementManager.remove(obs.getComponent(ScrollMovement.class));
                activeObstacles.removeIndex(i);
            }
        }

        boolean allAnswersGone = true;
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            Transform t = block.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !block.isActive()) {
                collisionManager.removeObject(block);
                entityManager.removeEntity(block);
                movementManager.remove(block.getComponent(ScrollMovement.class));
                activeAnswers.removeIndex(i);
            } else {
                allAnswersGone = false;
            }
        }

        if (answersOnScreen && allAnswersGone) {
            generateNewQuestion();
        }
    }

    // ---- Rendering ----

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw sky background (upper portion only, with parallax)
        float bgWidth = WORLD_WIDTH;
        float bgOffset = bgScrollX % bgWidth;
        batch.draw(bgTexture, -bgOffset, SKY_Y, bgWidth, WORLD_HEIGHT - SKY_Y);
        batch.draw(bgTexture, bgWidth - bgOffset, SKY_Y, bgWidth, WORLD_HEIGHT - SKY_Y);

        // Draw scrolling lane bands
        float laneOffset = floorScrollX % bgWidth;
        for (float[] band : GRASS_BANDS) {
            batch.draw(grassBgTexture, -laneOffset, band[0], bgWidth, band[1]);
            batch.draw(grassBgTexture, bgWidth - laneOffset, band[0], bgWidth, band[1]);
        }
        float dirtTileW = 100f;
        int dirtTilesNeeded = (int)(WORLD_WIDTH / dirtTileW) + 2;
        float dirtOffset = floorScrollX % dirtTileW;
        for (float[] band : DIRT_BANDS) {
            for (int i = 0; i < dirtTilesNeeded; i++) {
                float dx = i * dirtTileW - dirtOffset;
                batch.draw(dirtBgTexture, dx, band[0], dirtTileW, band[1]);
            }
        }

        // Draw decorations
        for (int i = 0; i < decorations.size; i++) {
            float[] d = decorations.get(i);
            Texture tex = d[2] == 0f ? decoGrassTex : decoBushTex;
            batch.draw(tex, d[0], d[1], 48, 48);
        }

        // Draw entities
        entityManager.render(batch);

        // Draw answer values on blocks
        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform t = block.getComponent(Transform.class);
            if (t == null) continue;

            String text = String.valueOf(block.getAnswerValue());
            glyphLayout.setText(font, text);
            font.draw(batch, text,
                t.getX() - glyphLayout.width / 2f,
                t.getY() + glyphLayout.height / 2f);
        }

        // Draw HUD - Hearts
        float hudY = WORLD_HEIGHT - 40f;
        int lives = player != null ? player.getLives() : 0;
        for (int i = 0; i < 3; i++) {
            Texture hTex = i < lives ? heartTexture : heartEmptyTexture;
            batch.draw(hTex, 10 + i * 35, hudY, 30, 30);
        }

        // Draw score
        String scoreText = "Score: " + score;
        glyphLayout.setText(hudFont, scoreText);
        hudFont.draw(batch, scoreText, WORLD_WIDTH - glyphLayout.width - 20, hudY + 25);

        // Draw level
        String levelText = "Level " + level;
        glyphLayout.setText(hudFont, levelText);
        hudFont.draw(batch, levelText, WORLD_WIDTH / 2f - glyphLayout.width / 2f, hudY + 25);

        // Draw question
        if (currentQuestion != null) {
            String qText = currentQuestion.getQuestionText();
            glyphLayout.setText(questionFont, qText);
            questionFont.draw(batch, qText,
                WORLD_WIDTH / 2f - glyphLayout.width / 2f,
                WORLD_HEIGHT - 70);
        }

        // ---- Surge Meter Bar ----
        float barW = 200f, barH = 16f;
        float barX = WORLD_WIDTH / 2f - barW / 2f;
        float barY = 15f;
        // Background
        batch.draw(surgeBarBgTex, barX - 2, barY - 2, barW + 4, barH + 4);
        // Fill
        float fillW = barW * surgeComponent.getSurgeAmount();
        if (surgeComponent.isSurging()) {
            // Pulsing gold during surge mode
            float pulse = 0.8f + 0.2f * MathUtils.sin(surgeComponent.getSurgeProgress() * 20f);
            batch.setColor(1f, 0.85f, 0f, pulse);
        } else {
            // Green fill
            batch.setColor(0.2f, 0.9f, 0.3f, 0.9f);
        }
        batch.draw(surgeBarFillTex, barX, barY, fillW, barH);
        batch.setColor(Color.WHITE);

        // Label
        String surgeLabel = surgeComponent.isSurging() ? "SURGE!" : "Surge";
        glyphLayout.setText(streakFont, surgeLabel);
        streakFont.draw(batch, surgeLabel,
            barX + barW / 2f - glyphLayout.width / 2f,
            barY + barH + glyphLayout.height + 4);

        // Streak counter
        if (difficulty.getCorrectStreak() >= 2) {
            String streakText = difficulty.getCorrectStreak() + "x Streak!";
            glyphLayout.setText(streakFont, streakText);
            streakFont.draw(batch, streakText,
                WORLD_WIDTH / 2f - glyphLayout.width / 2f,
                barY + barH + glyphLayout.height + 24);
        }

        // Surge mode golden screen tint
        if (surgeComponent.isSurging()) {
            float alpha = 0.1f + 0.05f * MathUtils.sin(surgeComponent.getSurgeProgress() * 15f);
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(surgeTintTex, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.setColor(Color.WHITE);
        }

        batch.end();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    // ---- CollisionDispatcher.GameEventListener callbacks ----

    @Override
    public void onHealthChanged(int newHealth) {
        // HUD updates automatically via render - no difficulty/surge logic here
    }

    @Override
    public void onObstacleHit() {
        // Only called when player actually takes damage from an obstacle
        difficulty.onObstacleHit();
        surgeComponent.resetSurge();
    }

    @Override
    public void onPlayerDeath() {
        // Transition to GameOver state
        gameStateManager.setState("gameover");
    }

    @Override
    public void onCorrectAnswer() {
        score++;
        difficulty.onCorrect();
        surgeComponent.addSurge();

        // Apply adaptive speed to factories
        float newSpeed = BASE_SCROLL_SPEED * difficulty.getSpeedMultiplier();
        obstacleFactory.setScrollSpeed(newSpeed);
        answerBlockFactory.setScrollSpeed(newSpeed);

        clearAnswerBlocks();
        generateNewQuestion();
    }

    @Override
    public void onWrongAnswer() {
        difficulty.onWrong();
        surgeComponent.resetSurge();

        float newSpeed = BASE_SCROLL_SPEED * difficulty.getSpeedMultiplier();
        obstacleFactory.setScrollSpeed(newSpeed);
        answerBlockFactory.setScrollSpeed(newSpeed);

        clearAnswerBlocks();
        generateNewQuestion();
    }

    /** Called by GameOverState - performs the actual scene transition. */
    private void handleGameOver() {
        saveHighScore();
        Scene deathSceneRef = sceneManager.getScene("death");
        if (deathSceneRef instanceof DeathScene) {
            ((DeathScene) deathSceneRef).setFinalScore(score);
            ((DeathScene) deathSceneRef).setLevel(level);
        }
        sceneManager.setScene("death");
    }

    private void clearAnswerBlocks() {
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            block.setActive(false);
            collisionManager.removeObject(block);
            entityManager.removeEntity(block);
            movementManager.remove(block.getComponent(ScrollMovement.class));
        }
        activeAnswers.clear();
        answersOnScreen = false;
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
        // Reset to playing state when shown (e.g. returning from pause)
        if (gameStateManager != null && !"playing".equals(gameStateManager.getCurrentStateName())) {
            // Only reset if not already in gameover
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
