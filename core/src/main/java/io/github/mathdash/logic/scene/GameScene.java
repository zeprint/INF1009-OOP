package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import io.github.mathdash.AbstractEngine.collision.CollisionManager;
import io.github.mathdash.AbstractEngine.entity.Entity;
import io.github.mathdash.AbstractEngine.entity.EntityManager;
import io.github.mathdash.AbstractEngine.entity.Renderable;
import io.github.mathdash.AbstractEngine.entity.Transform;
import io.github.mathdash.AbstractEngine.inputouput.AudioManager;
import io.github.mathdash.AbstractEngine.inputouput.InputAction;
import io.github.mathdash.AbstractEngine.inputouput.InputBindings;
import io.github.mathdash.AbstractEngine.inputouput.InputManager;
import io.github.mathdash.AbstractEngine.movement.MovementManager;
import io.github.mathdash.AbstractEngine.scene.Scene;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.logic.Collision.CollisionDispatcher;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.AnswerBlockFactory;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.entity.ObstacleFactory;
import io.github.mathdash.logic.entity.Player;
import io.github.mathdash.logic.entity.PlayerFactory;
import io.github.mathdash.logic.math.MathQuestion;
import io.github.mathdash.logic.math.MathQuestionGenerator;
import io.github.mathdash.logic.movement.ScrollMovement;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * GameScene - The main gameplay scene for MathDash.
 */
public class GameScene extends Scene implements CollisionDispatcher.GameEventListener {

    public static final float WORLD_WIDTH  = 800f;
    public static final float WORLD_HEIGHT = 600f;

    private static final String ASSET_BASE = "kenney_new-platformer-pack-1.1/";
    private static final float BASE_SCROLL_SPEED = 200f;
    private static final float SPEED_INCREMENT = 15f;
    private static final float SPAWN_INTERVAL_BASE = 3.0f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2.0f;
    private static final float ANSWER_SAFE_DISTANCE = 100f;
    private static final float DECO_SPAWN_INTERVAL = 80f;

    // Lane background band definitions [yStart, height]
    private static final float[][] DIRT_BANDS = {
        {50f, 100f},
        {200f, 100f},
        {350f, 100f},
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
    private final InputBindings inputBindings;
    private final Random random = new Random();

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

    private float scrollSpeed = BASE_SCROLL_SPEED;
    private float bgScrollX = 0f;
    private float floorScrollX = 0f;
    private float obstacleSpawnTimer = 0f;
    private float answerSpawnTimer = 0f;
    private boolean answersOnScreen  = false;
    private int score = 0;
    private boolean gameOver = false;

    private final Array<AnswerBlock> activeAnswers = new Array<>();
    private final Array<Obstacle> activeObstacles = new Array<>();
    private final Array<float[]> decorations = new Array<>();
    private float decoSpawnAccum = 0f;

    public GameScene(SceneManager sceneManager, int level, InputBindings inputBindings) {
        super("game");
        this.sceneManager = sceneManager;
        this.level = level;
        this.inputBindings = inputBindings;
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

        // Pre-populate decorations across the screen
        for (float x = 0; x < WORLD_WIDTH; x += DECO_SPAWN_INTERVAL) {
            float[] band = GRASS_BANDS[random.nextInt(GRASS_BANDS.length)];
            float y = band[0] + random.nextFloat() * Math.max(0f, band[1] - 48f);
            float texIdx = random.nextBoolean() ? 0f : 1f;
            decorations.add(new float[]{x + (random.nextFloat() * 40f - 20f), y, texIdx});
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
        glyphLayout = new GlyphLayout();
    }

    private void setupManagers() {
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        movementManager = new MovementManager();
    }

    private void setupInput() {
        inputManager = new InputManager(inputBindings);
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

        if (inputManager.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.setScene("pause");
            return;
        }

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

        bgScrollX += scrollSpeed * 0.3f * deltaTime;
        floorScrollX += scrollSpeed * deltaTime;

        // Update decorations
        float decoMove = scrollSpeed * deltaTime;
        for (int i = decorations.size - 1; i >= 0; i--) {
            float[] d = decorations.get(i);
            d[0] -= decoMove;
            if (d[0] < -50f) decorations.removeIndex(i);
        }
        decoSpawnAccum += decoMove;
        while (decoSpawnAccum >= DECO_SPAWN_INTERVAL) {
            decoSpawnAccum -= DECO_SPAWN_INTERVAL;
            float[] band = GRASS_BANDS[random.nextInt(GRASS_BANDS.length)];
            float y = band[0] + random.nextFloat() * Math.max(0f, band[1] - 48f);
            float texIdx = random.nextBoolean() ? 0f : 1f;
            decorations.add(new float[]{WORLD_WIDTH + random.nextFloat() * 40f, y, texIdx});
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

    private void spawnObstacle() {
        float spawnX = WORLD_WIDTH + 50f;

        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform bt = block.getComponent(Transform.class);
            if (bt != null && Math.abs(bt.getX() - spawnX) < ANSWER_SAFE_DISTANCE) return;
        }

        if (!answersOnScreen) {
            float answerInterval = SPAWN_INTERVAL_BASE * (BASE_SCROLL_SPEED / scrollSpeed);
            float timeUntilAnswers = answerInterval - answerSpawnTimer;
            float safeTime = ANSWER_SAFE_DISTANCE / scrollSpeed;
            if (timeUntilAnswers <= safeTime) return;
        }

        int lane = random.nextInt(3);
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

        int[] answers = {currentQuestion.getCorrectAnswer(), currentQuestion.getWrongAnswer1(), currentQuestion.getWrongAnswer2()};
        boolean[] isCorrect = {true, false, false};

        for (int i = 0; i < 3; i++) {
            float      y     = Player.LANE_Y[laneOrder[i]];
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
            int j = random.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    private void cleanupEntities() {
        for (int i = activeObstacles.size - 1; i >= 0; i--) {
            Obstacle  obs = activeObstacles.get(i);
            Transform t   = obs.getComponent(Transform.class);
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

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float bgWidth  = WORLD_WIDTH;
        float bgOffset = bgScrollX % bgWidth;
        batch.draw(bgTexture, -bgOffset, SKY_Y, bgWidth, WORLD_HEIGHT - SKY_Y);
        batch.draw(bgTexture, bgWidth - bgOffset, SKY_Y, bgWidth, WORLD_HEIGHT - SKY_Y);

        float laneOffset = floorScrollX % bgWidth;
        for (float[] band : GRASS_BANDS) {
            batch.draw(grassBgTexture, -laneOffset, band[0], bgWidth, band[1]);
            batch.draw(grassBgTexture, bgWidth - laneOffset, band[0], bgWidth, band[1]);
        }

        float dirtTileW = 100f;
        int   dirtTilesNeeded = (int)(WORLD_WIDTH / dirtTileW) + 2;
        float dirtOffset = floorScrollX % dirtTileW;
        for (float[] band : DIRT_BANDS) {
            for (int i = 0; i < dirtTilesNeeded; i++) {
                batch.draw(dirtBgTexture, i * dirtTileW - dirtOffset, band[0], dirtTileW, band[1]);
            }
        }

        for (int i = 0; i < decorations.size; i++) {
            float[] d = decorations.get(i);
            Texture tex = d[2] == 0f ? decoGrassTex : decoBushTex;
            batch.draw(tex, d[0], d[1], 48, 48);
        }

        // Draw entities
        Array<Entity> entities = entityManager.getAllEntities();
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (!entity.isActive()) continue;
            Renderable renderable = entity.getComponent(Renderable.class);
            Transform  transform  = entity.getComponent(Transform.class);
            if (renderable != null && transform != null) {
                renderable.render(batch, transform);
            }
        }

        // Draw answer values on blocks
        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform t = block.getComponent(Transform.class);
            if (t == null) continue;
            String text = String.valueOf(block.getAnswerValue());
            glyphLayout.setText(font, text);
            font.draw(batch, text, t.getX() - glyphLayout.width / 2f, t.getY() + glyphLayout.height / 2f);
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
            questionFont.draw(batch, qText, WORLD_WIDTH / 2f - glyphLayout.width / 2f, WORLD_HEIGHT - 70);
        }

        batch.end();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void onHealthChanged(int newHealth) {}

    @Override
    public void onPlayerDeath() {
        gameOver = true;
        saveHighScore();
        Scene deathSceneRef = sceneManager.getScene("death");
        if (deathSceneRef instanceof DeathScene) {
            ((DeathScene) deathSceneRef).setFinalScore(score);
            ((DeathScene) deathSceneRef).setLevel(level);
        }
        sceneManager.setScene("death");
    }

    @Override
    public void onCorrectAnswer() {
        score++;
        scrollSpeed += SPEED_INCREMENT;
        obstacleFactory.setScrollSpeed(scrollSpeed);
        answerBlockFactory.setScrollSpeed(scrollSpeed);
        clearAnswerBlocks();
        generateNewQuestion();
    }

    @Override
    public void onWrongAnswer() {
        clearAnswerBlocks();
        generateNewQuestion();
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
        String key     = "highscore_level_" + level;
        int    current = prefs.getInteger(key, 0);
        if (score > current) {
            prefs.putInteger(key, score);
            prefs.flush();
        }
    }

    public int getScore() { 
        return score; 
    }
    public int getLevel() { 
        return level; 
    }

    @Override
    protected void onUnload() {
        if (entityManager != null) {
            entityManager.dispose();
        }
        if (collisionManager != null) {
            collisionManager.clear();
        }
        if (audioManager != null) {
            audioManager.dispose();
        }
        if (inputManager != null) {
            inputManager.dispose();
        }

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

        if (font != null) {
            font.dispose();
        }
        if (hudFont != null) {
            hudFont.dispose();
        }
        if (questionFont != null) {
            questionFont.dispose();
        }
    }

    private void disposeTexture(Texture tex) {
        if (tex != null) {
            tex.dispose();
        }
    }

    @Override
    protected void onShow() {
        gameOver = false;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if (movementManager != null) {
            movementManager.unfreezeAll();
        }
    }

    @Override
    protected void onHide() {
        if (movementManager != null) {
            movementManager.freezeAll();
        }
    }
}