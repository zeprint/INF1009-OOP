package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * GameScene - Logic Engine scene (contextual).
 *
 * Extends SimulationScene to create the actual game:
 *  - 5 falling droplets with GravityMovement
 *  - 1 player-controlled bucket (keyboard or mouse)
 *  - 1 rotating triangle with RotationComponent
 *  - 1 moving circle that bounces off walls
 *  - 1 static square
 *
 * Features:
 *  - Mute toggle     : Press M to mute/unmute all audio
 *  - Mouse mode      : Press T to toggle between keyboard and mouse control
 *  - Droplet bounce  : Droplets bounce off non-bucket entities (shapes)
 *  - Bucket catch    : Collision between bucket and droplet plays click.wav
 *
 * Demonstrates all five engine managers working together.
 */
public class GameScene extends SimulationScene {

    // --- Game entities ---
    private Bucket         bucket;
    private Droplet[]      droplets;
    private RotatingShape  rotatingTriangle;
    private RotatingShape  movingCircle;
    private RotatingShape  staticSquare;

    // Movement components
    private InputMovement     bucketMovement;
    private RotationComponent circleMovement;
    private GravityMovement[] dropletMovements;   // stored for bounce access

    // Screen dimensions (cached after create)
    private float screenW;
    private float screenH;

    // --- Mouse mode state ---
    private boolean mouseMode = false;

    // --- Cached rectangles for shape collision (avoid GC per frame) ---
    private final Rectangle shapeBounds  = new Rectangle();
    private final Rectangle dropBounds   = new Rectangle();

    // Sizes
    private static final int   BUCKET_SIZE   = 64;
    private static final int   DROPLET_SIZE  = 32;
    private static final int   NUM_DROPLETS  = 5;
    private static final float TRIANGLE_R    = 30f;
    private static final float CIRCLE_R      = 20f;
    private static final float SQUARE_R      = 25f;

    public GameScene(
            IEntitySystem    entitySystem,
            IMovementSystem  movementSystem,
            ICollisionSystem collisionSystem,
            IInputSystem     inputSystem,
            IAudioSystem     audioSystem,
            SpriteBatch      spriteBatch,
            ShapeRenderer    shapeRenderer) {

        super(entitySystem, movementSystem, collisionSystem,
              inputSystem, audioSystem, spriteBatch, shapeRenderer);
    }

    // --- Scene lifecycle ---

    @Override
    public boolean create() {
        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();

        createBucket();
        createDroplets();
        createRotatingTriangle();
        createMovingCircle();
        createStaticSquare();

        return true;
    }

    // --- Per-frame update ---

    @Override
    public boolean update(float dt) {
        if (isPaused) return false;

        // --- Feature 1: Mute toggle (M key, already bound to TOGGLE_MUTE) ---
        handleMuteToggle();

        // --- Feature 2: Mouse mode toggle (T key) ---
        handleMouseModeToggle();

        // --- Feature 2b: Apply mouse movement when active ---
        handleMouseMovement();

        // --- Existing: bounce circle off screen edges ---
        handleCircleBounce();

        // --- Engine pipeline: movement → entities → collision ---
        boolean result = super.update(dt);

        // --- Feature 3: Droplet bounces off shape entities ---
        // (Runs after engine collision so it handles shapes not in CollisionManager)
        handleDropletShapeCollisions();

        return result;
    }

    // =====================================================================
    //  FEATURE 1 — Mute toggle
    // =====================================================================

    /**
     * Toggles audio mute state when the TOGGLE_MUTE action is triggered.
     * The M key binding is configured in GameMaster (Abstract Engine).
     * This handler lives here because mute is game-specific behaviour.
     */
    private void handleMuteToggle() {
        if (getInputSystem().isActionTriggered(InputAction.TOGGLE_MUTE)) {
            IAudioSystem audio = getAudioSystem();
            if (audio != null) {
                audio.setMuted(!audio.isMuted());
            }
        }
    }

    // =====================================================================
    //  FEATURE 2 — Mouse mode toggle
    // =====================================================================

    /**
     * Toggles between keyboard (A/D) and mouse-driven bucket movement.
     * Uses T key directly via Gdx.input (game-specific input in Logic layer).
     * When mouse mode activates, the InputMovement component is disabled
     * so the two control schemes don't conflict.
     */
    private void handleMouseModeToggle() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            mouseMode = !mouseMode;

            if (mouseMode) {
                bucketMovement.disable();   // stop keyboard movement
            } else {
                bucketMovement.enable();    // resume keyboard movement
            }
        }
    }

    /**
     * When mouse mode is active, the bucket follows the mouse X position,
     * clamped to the screen boundaries.
     */
    private void handleMouseMovement() {
        if (!mouseMode || bucket == null) return;

        float mouseX = Gdx.input.getX();
        // Clamp so the bucket stays fully on screen
        mouseX = Math.max(0f, Math.min(screenW - BUCKET_SIZE, mouseX));
        bucket.setX(mouseX);
    }

    // =====================================================================
    //  FEATURE 3 — Droplet ↔ shape bounce collisions
    // =====================================================================

    /**
     * Manually checks AABB overlap between every droplet and every shape
     * entity (rotating triangle, moving circle, static square).
     *
     * These shapes are not registered as Collidable in the CollisionManager
     * (they are Abstract Engine RotatingShape instances that don't implement
     * Collidable), so collision with them must be handled here in the Logic
     * Engine. When overlap is detected, the droplet's GravityMovement
     * velocity is reversed to simulate a bounce.
     */
    private void handleDropletShapeCollisions() {
        if (droplets == null || dropletMovements == null) return;

        // Collect shape entities to test against
        RotatingShape[] shapes = { rotatingTriangle, movingCircle, staticSquare };

        for (int i = 0; i < droplets.length; i++) {
            Droplet droplet = droplets[i];
            GravityMovement gm = dropletMovements[i];
            if (droplet == null || gm == null) continue;

            dropBounds.set(droplet.getX(), droplet.getY(),
                           droplet.getWidth(), droplet.getHeight());

            for (RotatingShape shape : shapes) {
                if (shape == null) continue;

                // Compute AABB for the shape (centered at posX, posY with radius)
                float r = shape.getRadius();
                shapeBounds.set(shape.getX() - r, shape.getY() - r, r * 2f, r * 2f);

                if (dropBounds.overlaps(shapeBounds)) {
                    bounceDropletOffShape(droplet, gm, shapeBounds);
                    break;  // one bounce per frame per droplet is enough
                }
            }
        }
    }

    /**
     * Reverses the droplet's velocity and separates it from the shape
     * to prevent repeated collision triggers on the same frame.
     */
    private void bounceDropletOffShape(Droplet droplet, GravityMovement gm,
                                        Rectangle sBounds) {
        float vx = gm.getVelocityX();
        float vy = gm.getVelocityY();

        // Determine overlap on each axis for minimum-separation direction
        float overlapX = Math.min(
                droplet.getX() + droplet.getWidth() - sBounds.x,
                sBounds.x + sBounds.width - droplet.getX());
        float overlapY = Math.min(
                droplet.getY() + droplet.getHeight() - sBounds.y,
                sBounds.y + sBounds.height - droplet.getY());

        if (overlapX < overlapY) {
            // Horizontal collision — reverse X, nudge sideways
            float centerDX = droplet.getX() - (sBounds.x + sBounds.width / 2f);
            gm.setVelocity(-vx, vy);
            droplet.setX(droplet.getX() + (centerDX > 0 ? overlapX : -overlapX));
        } else {
            // Vertical collision — reverse Y, nudge up/down
            float centerDY = droplet.getY() - (sBounds.y + sBounds.height / 2f);
            float newVy = -vy;
            if (Math.abs(newVy) < 50f) {
                newVy = (newVy >= 0f) ? 50f : -50f;
            }
            gm.setVelocity(vx, newVy);
            droplet.setY(droplet.getY() + (centerDY > 0 ? overlapY : -overlapY));
        }
    }

    // =====================================================================
    //  Entity creation (Logic Engine territory)
    // =====================================================================

    private void createBucket() {
        Texture tex = new Texture(Gdx.files.internal("bucket.png"));
        float startX = (screenW - BUCKET_SIZE) / 2f;
        float startY = 20f;

        bucket = new Bucket(tex, startX, startY, BUCKET_SIZE, BUCKET_SIZE,
                            getAudioSystem());

        // InputMovement drives the bucket via the input system (SRP fix)
        bucketMovement = new InputMovement(
                bucket, getInputSystem(), InputAxis.MOVE_X, bucket.getSpeed());
        bucketMovement.setBounds(0f, screenW - BUCKET_SIZE);

        entitySystem.addEntity(bucket);
        getMovementSystem().registerComponent(bucketMovement);
        getCollisionSystem().addObject(bucket);
    }

    private void createDroplets() {
        Texture tex = new Texture(Gdx.files.internal("droplet.png"));
        droplets         = new Droplet[NUM_DROPLETS];
        dropletMovements = new GravityMovement[NUM_DROPLETS];

        MobileRandom xRandom = new MobileRandom(20f, screenW - DROPLET_SIZE - 20f);

        for (int i = 0; i < NUM_DROPLETS; i++) {
            float startX = xRandom.next();
            float startY = screenH + (i * 80f);  // stagger initial positions

            Droplet d = new Droplet(tex, startX, startY,
                                    DROPLET_SIZE, DROPLET_SIZE, screenH + 40f);
            d.setXDistribution(new MobileRandom(20f, screenW - DROPLET_SIZE - 20f));

            // GravityMovement makes each droplet fall continuously
            GravityMovement gm = new GravityMovement(d, 0f);  // no gravity accel
            gm.setVelocity(0f, -(120f + i * 30f));            // varying fall speeds
            gm.setVerticalBounds(-50f, screenH + 40f);
            gm.setHorizontalResetRange(20f, screenW - DROPLET_SIZE - 20f);
            gm.setSpeedMultiplier(1f);                         // constant speed on reset
            gm.setMaxDropSpeed(300f);

            // Link the movement component to the droplet for bounce access
            d.setGravityMovement(gm);

            entitySystem.addEntity(d);
            getMovementSystem().registerComponent(gm);
            getCollisionSystem().addObject(d);

            droplets[i]         = d;
            dropletMovements[i] = gm;
        }
    }

    private void createRotatingTriangle() {
        rotatingTriangle = new RotatingShape(
                screenW * 0.25f, screenH * 0.6f, TRIANGLE_R, Color.CYAN, ShapeType.TRIANGLE);

        RotationComponent rc = new RotationComponent(rotatingTriangle);
        rc.setAngularVelocity(90f);  // 90 degrees per second

        entitySystem.addEntity(rotatingTriangle);
        getMovementSystem().registerComponent(rc);
    }

    private void createMovingCircle() {
        movingCircle = new RotatingShape(
                screenW * 0.5f, screenH * 0.5f, CIRCLE_R, Color.YELLOW, ShapeType.CIRCLE);

        circleMovement = new RotationComponent(movingCircle, 0f, 100f, 80f);
        circleMovement.setAngularVelocity(0f);  // no spin, just linear movement

        entitySystem.addEntity(movingCircle);
        getMovementSystem().registerComponent(circleMovement);
    }

    private void createStaticSquare() {
        staticSquare = new RotatingShape(
                screenW * 0.75f, screenH * 0.4f, SQUARE_R, Color.CORAL, ShapeType.SQUARE);

        entitySystem.addEntity(staticSquare);
        // No movement component — it's static
    }

    // --- Boundary handling (Logic Engine territory) ---

    private void handleCircleBounce() {
        if (movingCircle == null || circleMovement == null) return;

        float cx = movingCircle.getX();
        float cy = movingCircle.getY();
        float vx = circleMovement.getVelocityX();
        float vy = circleMovement.getVelocityY();

        boolean changed = false;

        if (cx - CIRCLE_R <= 0 || cx + CIRCLE_R >= screenW) {
            vx = -vx;
            cx = Math.max(CIRCLE_R, Math.min(screenW - CIRCLE_R, cx));
            changed = true;
        }
        if (cy - CIRCLE_R <= 0 || cy + CIRCLE_R >= screenH) {
            vy = -vy;
            cy = Math.max(CIRCLE_R, Math.min(screenH - CIRCLE_R, cy));
            changed = true;
        }

        if (changed) {
            circleMovement.setVelocity(vx, vy);
            movingCircle.setX(cx);
            movingCircle.setY(cy);
        }
    }
}
