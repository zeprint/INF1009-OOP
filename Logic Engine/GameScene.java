package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * GameScene - Logic Engine scene (contextual).
 *
 * Extends SimulationScene to create the actual game:
 *  - 5 falling droplets with GravityMovement
 *  - 1 player-controlled bucket
 *  - 1 rotating triangle with RotationComponent
 *  - 1 moving circle that bounces off walls
 *  - 1 static square
 *  - Collision between bucket and droplet plays click.wav
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

    // Screen dimensions (cached after create)
    private float screenW;
    private float screenH;

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

        handleCircleBounce();

        return super.update(dt);   // movement â†’ entities â†’ collision
    }

    // --- Entity creation (Logic Engine territory) ---

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
        droplets = new Droplet[NUM_DROPLETS];

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

            entitySystem.addEntity(d);
            getMovementSystem().registerComponent(gm);
            getCollisionSystem().addObject(d);

            droplets[i] = d;
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
        // No movement component â€” it's static
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
