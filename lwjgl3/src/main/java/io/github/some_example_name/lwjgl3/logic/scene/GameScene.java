package io.github.some_example_name.lwjgl3.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.EntityManager;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;
import io.github.some_example_name.lwjgl3.AbstractEngine.movement.MovementManager;
import io.github.some_example_name.lwjgl3.CollisionManager;
import io.github.some_example_name.lwjgl3.GameMaster;
import io.github.some_example_name.lwjgl3.IAudioSystem;
import io.github.some_example_name.lwjgl3.IInputSystem;
import io.github.some_example_name.lwjgl3.ISceneManager;
import io.github.some_example_name.lwjgl3.InputAction;
import io.github.some_example_name.lwjgl3.InputAxis;
import io.github.some_example_name.lwjgl3.Scene;
import io.github.some_example_name.lwjgl3.logic.entity.Character;
import io.github.some_example_name.lwjgl3.logic.entity.CharacterFactory;
import io.github.some_example_name.lwjgl3.logic.entity.Floor;
import io.github.some_example_name.lwjgl3.logic.entity.FloorFactory;
import io.github.some_example_name.lwjgl3.logic.movement.JumpMovement;
import io.github.some_example_name.lwjgl3.logic.movement.LaneSwitchMovement;
import io.github.some_example_name.lwjgl3.logic.movement.MotionState;
import io.github.some_example_name.lwjgl3.logic.movement.ScrollMovement;

import java.util.ArrayList;
import java.util.Collections;

/**
 * GameScene - Core gameplay for the Language Runner.
 *
 * Shield system: Toggle ON/OFF with S/Down. While ON, wrong word
 * collisions consume a shield charge instead of health. When all 3
 * charges are used, shield auto-deactivates.
 *
 * Jump: Raises the character vertically. Since word boxes scroll down
 * at floor level, jumping lets the character dodge words entirely (the
 * AABB check uses real Y position so an airborne character's hitbox is
 * above the word box). Pressing Up+Right simultaneously jumps AND
 * switches lane, so the character lands in the new lane.
 */
public class GameScene extends Scene {

    private static final String TAG = "GameScene";

    // ---- Engine references ----
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final IInputSystem inputSystem;
    private final IAudioSystem audioSystem;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;

    // ---- Entities ----
    private Character character;
    private Floor floor;

    // ---- Movement ----
    private MotionState motionState;
    private LaneSwitchMovement laneSwitchMovement;
    private JumpMovement jumpMovement;
    private ScrollMovement floorScrollMovement;

    // ---- Game state ----
    private int score;
    private int health;
    private int shieldCharges;
    private boolean shieldActive;
    private boolean needsReset;
    private static final int MAX_HEALTH = 3;
    private static final int MAX_SHIELDS = 3;

    // ---- Notification popup ----
    private String notificationText;
    private float notificationTimer;
    private Color notificationColor;
    private static final float NOTIFICATION_DURATION = 2.5f;

    // ---- Word challenge system ----
    private ArrayList<WordChallenge> questionPool;
    private int questionIndex;
    private WordChallenge currentChallenge;
    private boolean wordOptionsActive;
    private float wordSpawnTimer;
    private static final float WORD_SPAWN_DELAY = 2.0f;

    private String[] wordTexts;
    private boolean[] wordIsCorrect;
    private float[] wordOptionY;
    private boolean[] wordCollected;
    private static final float WORD_BOX_W = 100f;
    private static final float WORD_BOX_H = 40f;
    private static final float WORD_SCROLL_SPEED = 180f;

    // ---- Hit flash ----
    private float hitFlashTimer;

    // ---- Textures and fonts ----
    private Texture characterTexture;
    private Texture healthTexture;
    private Texture shieldTexture;
    private BitmapFont hudFont;
    private BitmapFont questionFont;
    private BitmapFont wordFont;
    private GlyphLayout layout;
    private boolean resourcesCreated;

    // ---- Layout constants ----
    private static final float FLOOR_Y = 80f;
    private static final float FLOOR_HEIGHT = 40f;
    private static final float SCROLL_SPEED = 150f;
    private static final int LANE_COUNT = 3;
    private static final float LANE_SPACING = 120f;
    private static final float LANE_SWITCH_SPEED = 600f;
    private static final float JUMP_VELOCITY = 450f;
    private static final float GRAVITY = -1100f;
    private static final float QUESTION_BAR_HEIGHT = 70f;

    public GameScene(ISceneManager sceneManager,
                     SpriteBatch spriteBatch, ShapeRenderer shapeRenderer,
                     IInputSystem inputSystem, IAudioSystem audioSystem,
                     EntityManager entityManager, MovementManager movementManager,
                     CollisionManager collisionManager) {
        super(sceneManager);
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.inputSystem = inputSystem;
        this.audioSystem = audioSystem;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.needsReset = true;
        this.resourcesCreated = false;
    }

    @Override
    public boolean create() {
        try {
            hudFont = new BitmapFont();
            hudFont.getData().setScale(1.4f);
            questionFont = new BitmapFont();
            questionFont.getData().setScale(1.6f);
            wordFont = new BitmapFont();
            wordFont.getData().setScale(1.3f);
            layout = new GlyphLayout();

            characterTexture = new Texture(Gdx.files.internal("character.png"));
            healthTexture = new Texture(Gdx.files.internal("health.png"));
            shieldTexture = new Texture(Gdx.files.internal("shield.png"));

            questionPool = QuestionLoader.load("questions.json");
            questionIndex = 0;

            wordTexts = new String[3];
            wordIsCorrect = new boolean[3];
            wordOptionY = new float[3];
            wordCollected = new boolean[3];

            resourcesCreated = true;
            setupGameEntities();
            resetGameState();
            needsReset = false;

            Gdx.app.log(TAG, "GameScene created");
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "create() failed", e);
            return false;
        }
    }

    @Override
    public boolean resume() {
        super.resume();
        if (needsReset && resourcesCreated) {
            teardownGameEntities();
            setupGameEntities();
            resetGameState();
            needsReset = false;
        }
        audioSystem.resumeMusic("bgm");
        return true;
    }

    @Override
    public boolean update(float dt) {
        if (isPaused) return false;

        // P → Pause
        if (inputSystem.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            audioSystem.playSound("click");
            audioSystem.pauseMusic("bgm");
            sceneManager.loadScene(GameMaster.SCENE_PAUSE);
            return true;
        }

        // ESC → Main Menu
        if (inputSystem.isActionTriggered(InputAction.BACK_TO_MENU)) {
            audioSystem.stopMusic("bgm");
            needsReset = true;
            sceneManager.loadScene(GameMaster.SCENE_MAIN_MENU);
            return true;
        }

        // M → Toggle Mute
        if (inputSystem.isActionTriggered(InputAction.TOGGLE_MUTE)) {
            boolean newMute = !audioSystem.isMuted();
            audioSystem.setMuted(newMute);
            showNotification(newMute ? "Sound: OFF" : "Sound: ON", Color.YELLOW);
        }

        // S/Down → TOGGLE Shield ON/OFF
        if (inputSystem.isActionTriggered(InputAction.SHIELD)) {
            if (shieldActive) {
                // Deactivate shield
                shieldActive = false;
                audioSystem.playSound("click");
                showNotification("Shield OFF (" + shieldCharges + " charges left)", Color.LIGHT_GRAY);
            } else if (shieldCharges > 0) {
                // Activate shield
                shieldActive = true;
                audioSystem.playSound("click");
                showNotification("Shield ON (" + shieldCharges + " charges)", Color.CYAN);
            } else {
                showNotification("No shields remaining!", Color.RED);
            }
        }

        // Timers
        if (hitFlashTimer > 0f) hitFlashTimer -= dt;
        if (notificationTimer > 0f) {
            notificationTimer -= dt;
            if (notificationTimer <= 0f) notificationText = null;
        }

        // Update subsystems
        movementManager.update(dt);
        entityManager.update(dt);

        // Word challenge spawning
        if (!wordOptionsActive) {
            wordSpawnTimer -= dt;
            if (wordSpawnTimer <= 0f) spawnWordChallenge();
        }

        // Word option scrolling & collision
        if (wordOptionsActive) updateWordOptions(dt);

        return true;
    }

    @Override
    public boolean render() {
        try {
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();
            float surfaceY = floor.getSurfaceY();
            float centreX = screenW / 2f;
            float baseLaneX = centreX - (LANE_COUNT / 2) * LANE_SPACING;

            // ---- Pass 1: ShapeRenderer ----
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Question bar
            shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 1f);
            shapeRenderer.rect(0, 0, screenW, QUESTION_BAR_HEIGHT);
            shapeRenderer.setColor(0f, 0.8f, 0.9f, 1f);
            shapeRenderer.rect(0, QUESTION_BAR_HEIGHT - 3f, screenW, 3f);

            // Floor
            shapeRenderer.setColor(0.2f, 0.6f, 0.3f, 1f);
            shapeRenderer.rect(floor.getTileAX(), floor.getY(), floor.getTileWidth(), floor.getHeight());
            shapeRenderer.rect(floor.getTileBX(), floor.getY(), floor.getTileWidth(), floor.getHeight());
            shapeRenderer.setColor(0.3f, 0.8f, 0.4f, 1f);
            shapeRenderer.rect(0, surfaceY - 2f, screenW, 4f);

            // Lane markers
            shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 0.25f);
            for (int i = 0; i < LANE_COUNT; i++) {
                float laneX = baseLaneX + i * LANE_SPACING;
                shapeRenderer.rect(laneX - 1f, surfaceY, 2f, screenH - surfaceY);
            }

            // Word option boxes
            if (wordOptionsActive) {
                for (int i = 0; i < 3; i++) {
                    if (wordCollected[i]) continue;
                    float laneX = baseLaneX + i * LANE_SPACING;
                    float boxLeft = laneX - WORD_BOX_W / 2f;
                    shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f);
                    shapeRenderer.rect(boxLeft, wordOptionY[i], WORD_BOX_W, WORD_BOX_H);
                    shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.6f);
                    shapeRenderer.rect(boxLeft, wordOptionY[i], WORD_BOX_W, 2f);
                    shapeRenderer.rect(boxLeft, wordOptionY[i] + WORD_BOX_H - 2f, WORD_BOX_W, 2f);
                }
            }

            // Shield glow
            if (shieldActive) {
                Transform ct = character.getComponent(Transform.class);
                if (ct != null) {
                    shapeRenderer.setColor(0.2f, 0.6f, 1f, 0.25f);
                    shapeRenderer.rect(ct.getX() - 35f, ct.getY() - 5f, 70f, 115f);
                }
            }

            // Hit flash
            if (hitFlashTimer > 0f) {
                shapeRenderer.setColor(1f, 0f, 0f, hitFlashTimer * 2f);
                shapeRenderer.rect(0, 0, screenW, screenH);
            }

            shapeRenderer.end();

            // ---- Pass 2: SpriteBatch ----
            spriteBatch.begin();

            // Character
            Transform charT = character.getComponent(Transform.class);
            if (charT != null && characterTexture != null) {
                float drawX = charT.getX() - character.getWidth() / 2f;
                float drawY = charT.getY();
                boolean visible = hitFlashTimer <= 0f || (int)(Gdx.graphics.getFrameId() % 6) < 3;
                if (visible) {
                    spriteBatch.draw(characterTexture, drawX, drawY,
                                     character.getWidth(), character.getHeight());
                }
            }

            // Word option text
            if (wordOptionsActive) {
                wordFont.setColor(Color.WHITE);
                for (int i = 0; i < 3; i++) {
                    if (wordCollected[i]) continue;
                    float laneX = baseLaneX + i * LANE_SPACING;
                    layout.setText(wordFont, wordTexts[i]);
                    wordFont.draw(spriteBatch, wordTexts[i],
                            laneX - layout.width / 2f,
                            wordOptionY[i] + WORD_BOX_H / 2f + layout.height / 2f);
                }
            }

            // ====== HUD: Text labels + icons (always visible) ======
            spriteBatch.setColor(Color.WHITE);
            float hudY = screenH - 15f;

            // Health (top-left)
            hudFont.setColor(Color.RED);
            String hpLabel = "HP: " + health + "/" + MAX_HEALTH + "  ";
            layout.setText(hudFont, hpLabel);
            hudFont.draw(spriteBatch, hpLabel, 15f, hudY);
            float hpIconX = 15f + layout.width;
            for (int i = 0; i < MAX_HEALTH; i++) {
                spriteBatch.setColor(1f, 1f, 1f, (i < health) ? 1f : 0.2f);
                spriteBatch.draw(healthTexture, hpIconX + i * 30f, hudY - 22f, 24f, 24f);
            }
            spriteBatch.setColor(Color.WHITE);

            // Shield (below health)
            float shieldY = hudY - 35f;
            hudFont.setColor(Color.CYAN);
            String shLabel = "Shield: " + shieldCharges + "/" + MAX_SHIELDS;
            if (shieldActive) shLabel += " [ON]";
            shLabel += "  ";
            layout.setText(hudFont, shLabel);
            hudFont.draw(spriteBatch, shLabel, 15f, shieldY);
            float shIconX = 15f + layout.width;
            for (int i = 0; i < MAX_SHIELDS; i++) {
                spriteBatch.setColor(1f, 1f, 1f, (i < shieldCharges) ? 1f : 0.2f);
                spriteBatch.draw(shieldTexture, shIconX + i * 30f, shieldY - 22f, 24f, 24f);
            }
            spriteBatch.setColor(Color.WHITE);

            // Score (top-right)
            hudFont.setColor(Color.YELLOW);
            String scoreText = "Score: " + score;
            layout.setText(hudFont, scoreText);
            hudFont.draw(spriteBatch, scoreText, screenW - layout.width - 20f, hudY);

            // Shield active banner (top-centre)
            if (shieldActive) {
                hudFont.setColor(0.3f, 0.8f, 1f, 1f);
                String banner = ">>> SHIELD ACTIVE <<<";
                layout.setText(hudFont, banner);
                hudFont.draw(spriteBatch, banner, (screenW - layout.width) / 2f, hudY);
            }

            // Question text (bottom bar)
            if (currentChallenge != null) {
                questionFont.setColor(Color.WHITE);
                String sentence = currentChallenge.getSentence();
                layout.setText(questionFont, sentence);
                questionFont.draw(spriteBatch, sentence,
                        (screenW - layout.width) / 2f,
                        QUESTION_BAR_HEIGHT / 2f + layout.height / 2f + 5f);
            } else {
                questionFont.setColor(0.5f, 0.8f, 1f, 1f);
                layout.setText(questionFont, "Get ready...");
                questionFont.draw(spriteBatch, "Get ready...",
                        (screenW - layout.width) / 2f,
                        QUESTION_BAR_HEIGHT / 2f + layout.height / 2f + 5f);
            }

            // Notification popup
            if (notificationText != null && notificationTimer > 0f) {
                float alpha = Math.min(1f, notificationTimer);
                Color nc = (notificationColor != null) ? notificationColor : Color.YELLOW;
                hudFont.setColor(nc.r, nc.g, nc.b, alpha);
                layout.setText(hudFont, notificationText);
                hudFont.draw(spriteBatch, notificationText,
                        (screenW - layout.width) / 2f, screenH * 0.55f);
            }

            spriteBatch.end();
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "render() error", e);
            try { if (shapeRenderer.isDrawing()) shapeRenderer.end(); } catch (Exception ex) {}
            try { if (spriteBatch.isDrawing()) spriteBatch.end(); } catch (Exception ex) {}
            return false;
        }
    }

    @Override
    public boolean dispose() {
        try {
            teardownGameEntities();
            if (characterTexture != null) characterTexture.dispose();
            if (healthTexture != null) healthTexture.dispose();
            if (shieldTexture != null) shieldTexture.dispose();
            if (hudFont != null) hudFont.dispose();
            if (questionFont != null) questionFont.dispose();
            if (wordFont != null) wordFont.dispose();
            audioSystem.stopMusic("bgm");
            resourcesCreated = false;
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "dispose() error", e);
            return false;
        }
    }

    // ---- Game state management ----

    private void setupGameEntities() {
        float screenW = Gdx.graphics.getWidth();
        float centreX = screenW / 2f;
        float floorSurface = FLOOR_Y + FLOOR_HEIGHT;

        character = new CharacterFactory().create(centreX, floorSurface);
        entityManager.addEntity(character);

        floor = new FloorFactory(screenW, FLOOR_HEIGHT).create(0f, FLOOR_Y);
        entityManager.addEntity(floor);

        motionState = new MotionState(centreX, floorSurface);
        character.setMotionState(motionState);

        laneSwitchMovement = new LaneSwitchMovement(
                character, motionState,
                () -> inputSystem.getAxis(InputAxis.MOVE_X) < 0f,
                () -> inputSystem.getAxis(InputAxis.MOVE_X) > 0f,
                centreX, LANE_COUNT, LANE_SPACING, LANE_SWITCH_SPEED);
        movementManager.registerComponent(laneSwitchMovement);

        jumpMovement = new JumpMovement(
                character, motionState,
                () -> inputSystem.isActionTriggered(InputAction.JUMP),
                floorSurface, JUMP_VELOCITY, GRAVITY);
        movementManager.registerComponent(jumpMovement);

        floorScrollMovement = new ScrollMovement(screenW, SCROLL_SPEED);
        floor.setScrollMovement(floorScrollMovement);
        movementManager.registerComponent(floorScrollMovement);

        collisionManager.addObject(character);
    }

    private void teardownGameEntities() {
        if (laneSwitchMovement != null) movementManager.unregisterComponent(laneSwitchMovement);
        if (jumpMovement != null) movementManager.unregisterComponent(jumpMovement);
        if (floorScrollMovement != null) movementManager.unregisterComponent(floorScrollMovement);
        collisionManager.clear();
        entityManager.dispose();
    }

    private void resetGameState() {
        score = 0;
        health = MAX_HEALTH;
        shieldCharges = MAX_SHIELDS;
        shieldActive = false;
        hitFlashTimer = 0f;
        notificationText = null;
        notificationTimer = 0f;
        notificationColor = null;
        wordOptionsActive = false;
        wordSpawnTimer = 1.5f;
        currentChallenge = null;

        if (questionPool != null) {
            Collections.shuffle(questionPool);
            questionIndex = 0;
        }

        audioSystem.playMusic("bgm", true);
        Gdx.app.log(TAG, "Game reset: HP=" + health + " Shield=" + shieldCharges + " Score=" + score);
    }

    // ---- Word challenge system ----

    private void spawnWordChallenge() {
        if (questionPool == null || questionPool.isEmpty()) return;

        if (questionIndex >= questionPool.size()) {
            Collections.shuffle(questionPool);
            questionIndex = 0;
        }
        currentChallenge = questionPool.get(questionIndex);
        questionIndex++;
        wordOptionsActive = true;

        // Randomise lane positions
        ArrayList<Integer> lanes = new ArrayList<>();
        lanes.add(0); lanes.add(1); lanes.add(2);
        Collections.shuffle(lanes);

        wordTexts[lanes.get(0)] = currentChallenge.getCorrectWord();
        wordIsCorrect[lanes.get(0)] = true;
        wordTexts[lanes.get(1)] = currentChallenge.getWrongWord1();
        wordIsCorrect[lanes.get(1)] = false;
        wordTexts[lanes.get(2)] = currentChallenge.getWrongWord2();
        wordIsCorrect[lanes.get(2)] = false;

        float screenH = Gdx.graphics.getHeight();
        for (int i = 0; i < 3; i++) {
            wordOptionY[i] = screenH + 50f;
            wordCollected[i] = false;
        }
    }

    private void updateWordOptions(float dt) {
        Transform charT = character.getComponent(Transform.class);
        if (charT == null) return;

        float charX = charT.getX();
        float charY = charT.getY();
        float screenW = Gdx.graphics.getWidth();
        float centreX = screenW / 2f;
        float baseLaneX = centreX - (LANE_COUNT / 2) * LANE_SPACING;

        boolean allDone = true;
        for (int i = 0; i < 3; i++) {
            if (wordCollected[i]) continue;
            allDone = false;

            wordOptionY[i] -= WORD_SCROLL_SPEED * dt;

            // AABB collision: character vs word box (uses real Y, so jumping dodges)
            float laneX = baseLaneX + i * LANE_SPACING;
            Rectangle wordBounds = new Rectangle(
                    laneX - WORD_BOX_W / 2f, wordOptionY[i], WORD_BOX_W, WORD_BOX_H);
            Rectangle charBounds = new Rectangle(
                    charX - character.getWidth() / 2f, charY,
                    character.getWidth(), character.getHeight());

            if (wordBounds.overlaps(charBounds)) {
                wordCollected[i] = true;
                handleWordCollision(wordIsCorrect[i]);
            }

            if (wordOptionY[i] + WORD_BOX_H < QUESTION_BAR_HEIGHT) {
                wordCollected[i] = true;
            }
        }

        if (allDone || (wordCollected[0] && wordCollected[1] && wordCollected[2])) {
            wordOptionsActive = false;
            currentChallenge = null;
            wordSpawnTimer = WORD_SPAWN_DELAY;
        }
    }

    private void handleWordCollision(boolean correct) {
        if (correct) {
            score += 10;
            audioSystem.playSound("correct");
            showNotification("+10 Correct!", Color.GREEN);
            for (int i = 0; i < 3; i++) wordCollected[i] = true;
        } else {
            String correctAnswer = (currentChallenge != null) ? currentChallenge.getCorrectWord() : "?";

            if (shieldActive && shieldCharges > 0) {
                shieldCharges--;
                audioSystem.playSound("click");
                showNotification("Shield used! Answer: " + correctAnswer
                        + " (" + shieldCharges + " left)", Color.CYAN);
                if (shieldCharges <= 0) {
                    shieldActive = false;
                }
            } else {
                health--;
                hitFlashTimer = 0.3f;
                audioSystem.playSound("wrong");
                showNotification("Wrong! Answer: " + correctAnswer, Color.RED);
                if (health <= 0) handleDeath();
            }
            for (int i = 0; i < 3; i++) wordCollected[i] = true;
        }
    }

    private void handleDeath() {
        audioSystem.stopMusic("bgm");
        audioSystem.playSound("death");
        Scene end = sceneManager.getScene(GameMaster.SCENE_END);
        if (end instanceof EndScene) {
            ((EndScene) end).setFinalScore(score);
        }
        needsReset = true;
        sceneManager.loadScene(GameMaster.SCENE_END);
    }

    private void showNotification(String text, Color color) {
        notificationText = text;
        notificationTimer = NOTIFICATION_DURATION;
        notificationColor = color;
    }

    public int getScore() { return score; }
}
