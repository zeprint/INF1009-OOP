package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.AbstractEngine.collision.CollisionManager;
import io.github.mathdash.AbstractEngine.entity.Entity;
import io.github.mathdash.AbstractEngine.entity.EntityManager;
import io.github.mathdash.AbstractEngine.entity.Transform;
import io.github.mathdash.AbstractEngine.inputouput.AudioManager;
import io.github.mathdash.AbstractEngine.inputouput.InputAction;
import io.github.mathdash.AbstractEngine.inputouput.InputBindings;
import io.github.mathdash.AbstractEngine.inputouput.InputManager;
import io.github.mathdash.AbstractEngine.movement.MovementManager;
import io.github.mathdash.AbstractEngine.scene.Scene;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.logic.Collision.CollisionDispatcher;
import io.github.mathdash.logic.Collision.CollisionHandler;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.AnswerBlockFactory;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.entity.ObstacleFactory;
import io.github.mathdash.logic.entity.Player;
import io.github.mathdash.logic.entity.PlayerFactory;
import io.github.mathdash.logic.math.MathQuestion;
import io.github.mathdash.logic.math.MathQuestionGenerator;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * GameScene - The main gameplay scene for MathDash.
 * Uses engine managers: EntityManager, CollisionManager, MovementManager, InputManager, AudioManager.
 */
public class GameScene extends Scene implements CollisionDispatcher.GameEventListener {

    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;

    private static final String ASSET_BASE = "kenney_new-platformer-pack-1.1/";
    private static final float BASE_SCROLL_SPEED = 200f;
    private static final float SPEED_INCREMENT = 15f;
    private static final float SPAWN_INTERVAL_BASE = 3.0f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2.0f;

    private final SceneManager sceneManager;
    private final int level;

    // Engine managers
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private InputManager inputManager;
    private AudioManager audioManager;

    // Camera
    private OrthographicCamera camera;
    private Viewport viewport;

    // Textures
    private Texture bgTexture;
    private Texture terrainTopTexture;
    private Texture terrainFillTexture;
    private Texture heartTexture;
    private Texture heartEmptyTexture;
    private Texture obstacleTexSaw;
    private Texture obstacleTexSpike;
    private Texture obstacleTexSlime;
    private Texture answerBlockTex;
    private Texture playerWalkA, playerWalkB, playerIdle, playerHit;

    // Fonts
    private BitmapFont font;
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
    private boolean gameOver = false;

    // Track active answer blocks
    private Array<AnswerBlock> activeAnswers = new Array<>();
    private Array<Obstacle> activeObstacles = new Array<>();

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
        setupAudio();
        setupFactories();
        spawnPlayer();
        generateNewQuestion();
    }

    private Texture loadTex(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return tex;
    }

    private void loadTextures() {
        bgTexture = loadTex(ASSET_BASE + "Sprites/Backgrounds/Default/background_color_trees.png");
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        terrainTopTexture = loadTex(ASSET_BASE + "Sprites/Tiles/Default/terrain_grass_block_top.png");
        terrainFillTexture = loadTex(ASSET_BASE + "Sprites/Tiles/Default/terrain_grass_block_center.png");
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

        font = FontGenerator.create(24, Color.WHITE);
        questionFont = FontGenerator.create(32, Color.YELLOW, Color.DARK_GRAY, 1f);

        glyphLayout = new GlyphLayout();
    }

    private void setupManagers() {
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        movementManager = new MovementManager(entityManager);
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

    private void setupAudio() {
        audioManager = new AudioManager();
        audioManager.loadSound("hurt", ASSET_BASE + "Sounds/sfx_hurt.ogg");
        audioManager.loadSound("correct", ASSET_BASE + "Sounds/sfx_coin.ogg");
        audioManager.loadSound("wrong", ASSET_BASE + "Sounds/sfx_bump.ogg");
        audioManager.loadSound("select", ASSET_BASE + "Sounds/sfx_select.ogg");
        audioManager.loadSound("jump", ASSET_BASE + "Sounds/sfx_jump.ogg");
    }

    private void setupFactories() {
        playerFactory = new PlayerFactory(playerWalkA, playerWalkB, playerIdle, playerHit);
        obstacleFactory = new ObstacleFactory(scrollSpeed, obstacleTexSaw, obstacleTexSpike, obstacleTexSlime);
        answerBlockFactory = new AnswerBlockFactory(answerBlockTex, scrollSpeed);
    }

    private void spawnPlayer() {
        player = playerFactory.create(120f, Player.LANE_Y[0]);

        collisionDispatcher = new CollisionDispatcher(audioManager, this);
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

    @Override
    public void update(float deltaTime) {
        if (gameOver) return;

        inputManager.update();

        // Pause
        if (inputManager.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.setScene("pause");
            return;
        }

        // Player lane switching
        if (inputManager.isActionTriggered(InputAction.JUMP)) {
            player.switchLane(1);
            audioManager.playSound("jump");
        }
        if (inputManager.isActionTriggered(InputAction.CONFIRM)) {
            player.switchLane(-1);
            audioManager.playSound("jump");
        }

        collisionDispatcher.update(deltaTime);
        entityManager.update(deltaTime);
        collisionManager.checkCollisions();

        // Scroll background
        bgScrollX += scrollSpeed * 0.3f * deltaTime;
        floorScrollX += scrollSpeed * deltaTime;

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

        // Clean up off-screen entities
        cleanupEntities();
    }

    private static final float ANSWER_SAFE_DISTANCE = 100f;

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
            // Convert safe distance to time: how long it takes to scroll 100px
            float safeTime = ANSWER_SAFE_DISTANCE / scrollSpeed;
            if (timeUntilAnswers <= safeTime) {
                return;
            }
        }

        int lane = MathUtils.random(0, 2);
        float y = Player.LANE_Y[lane];

        Obstacle obs = obstacleFactory.create(spawnX, y);
        obs.setCollisionHandler(collisionDispatcher);
        obs.setScrollSpeed(scrollSpeed);
        entityManager.addEntity(obs);
        collisionManager.addObject(obs);
        activeObstacles.add(obs);
    }

    private void spawnAnswerBlocks() {
        if (currentQuestion == null) return;

        float x = WORLD_WIDTH + 50f;

        // Assign answers to random lanes
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

    private void cleanupEntities() {
        // Clean obstacles
        for (int i = activeObstacles.size - 1; i >= 0; i--) {
            Obstacle obs = activeObstacles.get(i);
            Transform t = obs.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !obs.isActive()) {
                collisionManager.removeObject(obs);
                entityManager.removeEntity(obs);
                activeObstacles.removeIndex(i);
            }
        }

        // Clean answer blocks
        boolean allAnswersGone = true;
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            Transform t = block.getComponent(Transform.class);
            if (t == null || t.getX() < -100f || !block.isActive()) {
                collisionManager.removeObject(block);
                entityManager.removeEntity(block);
                activeAnswers.removeIndex(i);
            } else {
                allAnswersGone = false;
            }
        }

        // If all answers are gone, generate new question
        if (answersOnScreen && allAnswersGone) {
            generateNewQuestion();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw scrolling background
        float bgWidth = WORLD_WIDTH;
        float bgOffset = bgScrollX % bgWidth;
        batch.draw(bgTexture, -bgOffset, 0, bgWidth, WORLD_HEIGHT);
        batch.draw(bgTexture, bgWidth - bgOffset, 0, bgWidth, WORLD_HEIGHT);

        // Draw scrolling floor
        float tileSize = 48f;
        int tilesNeeded = (int)(WORLD_WIDTH / tileSize) + 2;
        float floorOffset = floorScrollX % tileSize;
        for (int i = 0; i < tilesNeeded; i++) {
            float tileX = i * tileSize - floorOffset;
            batch.draw(terrainTopTexture, tileX, 32, tileSize, tileSize);
            batch.draw(terrainFillTexture, tileX, 0, tileSize, 32);
        }

        // Draw lane lines (subtle)
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
        glyphLayout.setText(font, scoreText);
        font.draw(batch, scoreText, WORLD_WIDTH - glyphLayout.width - 20, hudY + 25);

        // Draw level
        String levelText = "Level " + level;
        glyphLayout.setText(font, levelText);
        font.draw(batch, levelText, WORLD_WIDTH / 2f - glyphLayout.width / 2f, hudY + 25);

        // Draw question
        if (currentQuestion != null) {
            String qText = currentQuestion.getQuestionText();
            glyphLayout.setText(questionFont, qText);
            questionFont.draw(batch, qText,
                WORLD_WIDTH / 2f - glyphLayout.width / 2f,
                WORLD_HEIGHT - 70);
        }

        batch.end();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    // CollisionDispatcher.GameEventListener callbacks
    @Override
    public void onHealthChanged(int newHealth) {
        // HUD will update automatically via render
    }

    @Override
    public void onPlayerDeath() {
        gameOver = true;
        // Save high score
        saveHighScore();
        // Pass score to death scene before switching
        Scene deathSceneRef = sceneManager.getScene("death");
        if (deathSceneRef instanceof DeathScene) {
            ((DeathScene) deathSceneRef).setFinalScore(score);
            ((DeathScene) deathSceneRef).setLevel(level);
        }
        // Switch to death scene
        sceneManager.setScene("death");
    }

    @Override
    public void onCorrectAnswer() {
        score++;
        scrollSpeed += SPEED_INCREMENT;
        obstacleFactory.setScrollSpeed(scrollSpeed);
        answerBlockFactory.setScrollSpeed(scrollSpeed);

        // Clear remaining answer blocks
        clearAnswerBlocks();
        generateNewQuestion();
    }

    @Override
    public void onWrongAnswer() {
        // Clear remaining answer blocks
        clearAnswerBlocks();
        generateNewQuestion();
    }

    private void clearAnswerBlocks() {
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            block.setActive(false);
            collisionManager.removeObject(block);
            entityManager.removeEntity(block);
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

    @Override
    protected void onUnload() {
        if (entityManager != null) entityManager.dispose();
        if (collisionManager != null) collisionManager.clear();
        if (audioManager != null) audioManager.dispose();
        if (inputManager != null) inputManager.dispose();

        disposeTexture(bgTexture);
        disposeTexture(terrainTopTexture);
        disposeTexture(terrainFillTexture);
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
        if (questionFont != null) questionFont.dispose();
    }

    private void disposeTexture(Texture tex) {
        if (tex != null) tex.dispose();
    }

    @Override
    protected void onShow() {
        gameOver = false;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    protected void onHide() {}
}
