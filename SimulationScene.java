package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class SimulationScene extends Scene {

    // Managers
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private InputManager inputManager;
    private AudioManager audioManager;

    // Rendering
    private ShapeRenderer shapeRenderer;

    // Entities
    private Texture bucketTexture;
    private Texture dropletTexture;
    private TextureObject bucket;
    private Shapes staticCircle;
    private Shapes dynamicTriangle;
    private RotatingShape rotatingSquare;

    // Droplet System
    private Array<TextureObject> activeDroplets;
    private Array<Collidable> activeDropletColliders;
    private float dropletSpawnTimer = 0f;
    private InputBindings inputBindings;

    // Constants
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;
    private static final float BUCKET_SPEED = 300f;
    private static final float BUCKET_WIDTH = 64f;
    private static final float BUCKET_HEIGHT = 64f;

    @Override
    public void create() {
        // 1. Initialize Managers
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        audioManager = new AudioManager();
        shapeRenderer = new ShapeRenderer();

        activeDroplets = new Array<>();
        activeDropletColliders = new Array<>();

        // 2. Input Setup
        inputBindings = new InputBindings();
        inputBindings.bindAxis(InputAxis.MOVE_X, Input.Keys.A, Input.Keys.D);
        inputManager = new InputManager(inputBindings);

        // 3. Load Assets
        bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
        dropletTexture = new Texture(Gdx.files.internal("droplet.png"));
        audioManager.loadSound("click", "click.wav");

        // 4. Create World Entities
        initializeBucket();
        initializeStaticCircle();
        initializeDynamicTriangle();
        initializeRotatingSquare();

        // 5. Start Game
        spawnDroplet();
    }

    private void initializeBucket() {
        bucket = new TextureObject(bucketTexture, WORLD_WIDTH / 2 - BUCKET_WIDTH / 2, 50, (int) BUCKET_HEIGHT,
                (int) BUCKET_WIDTH);
        bucket.setColor(Color.WHITE);
        entityManager.addEntity(bucket);

        Collidable bucketCol = new Collidable() {
            @Override
            public Rectangle getBounds() {
                return new Rectangle(bucket.getX(), bucket.getY(), BUCKET_WIDTH, BUCKET_HEIGHT);
            }

            @Override
            public CollisionType getType() {
                return new CollisionType("BUCKET", true, true);
            }

            @Override
            public void onCollision(CollisionResult result) {
                // If we catch a droplet, play sound
                if (result.getOther().getType().getName().equals("DROPLET")) {
                    audioManager.playSound("click");
                }
            }
        };
        collisionManager.addObject(bucketCol);
    }

    private void initializeStaticCircle() {
        staticCircle = new Shapes(ShapeType.CIRCLE, 200f, 300f, Color.RED);
        staticCircle.setDimensions("radius", 30f);
        entityManager.addEntity(staticCircle);

        Collidable col = new Collidable() {
            @Override
            public Rectangle getBounds() {
                float r = staticCircle.getDimension("radius");
                return new Rectangle(staticCircle.getX() - r, staticCircle.getY() - r, r * 2, r * 2);
            }

            @Override
            public CollisionType getType() {
                return new CollisionType("OBSTACLE", true, false);
            }

            @Override
            public void onCollision(CollisionResult result) {
            }
        };
        collisionManager.addObject(col);
    }

    private void initializeDynamicTriangle() {
        dynamicTriangle = new Shapes(ShapeType.TRIANGLE, 600f, 300f, Color.GREEN);
        dynamicTriangle.setDimensions("size", 40f);
        entityManager.addEntity(dynamicTriangle);

        MovementComponent move = new MovementComponent(dynamicTriangle) {
            private float direction = -1f;
            private float speed = 150f;

            @Override
            public void update(float dt) {
                if (!enabled)
                    return;
                float x = dynamicTriangle.getX() + direction * speed * dt;
                if (x <= 400 || x >= 700)
                    direction *= -1;
                dynamicTriangle.setX(x);
            }
        };
        movementManager.registerComponent(move);

        Collidable col = new Collidable() {
            @Override
            public Rectangle getBounds() {
                return new Rectangle(dynamicTriangle.getX(), dynamicTriangle.getY(), 40, 40);
            }

            @Override
            public CollisionType getType() {
                return new CollisionType("OBSTACLE", true, false);
            }

            @Override
            public void onCollision(CollisionResult result) {
            }
        };
        collisionManager.addObject(col);
    }

    private void initializeRotatingSquare() {
        rotatingSquare = new RotatingShape(350f, 450f, 25f, Color.YELLOW, false);
        rotatingSquare.setIsSquare(true);
        entityManager.addEntity(rotatingSquare);

        RotationComponent rot = new RotationComponent(rotatingSquare, 0f);
        rot.setAngularVelocity(100f);
        movementManager.registerComponent(rot);

        Collidable col = new Collidable() {
            @Override
            public Rectangle getBounds() {
                return new Rectangle(rotatingSquare.getX() - 25, rotatingSquare.getY() - 25, 50, 50);
            }

            @Override
            public CollisionType getType() {
                return new CollisionType("OBSTACLE", true, false);
            }

            @Override
            public void onCollision(CollisionResult result) {
            }
        };
        collisionManager.addObject(col);
    }

    private void spawnDroplet() {
        float randomX = (float) Math.random() * (WORLD_WIDTH - 32);
        final TextureObject droplet = new TextureObject(dropletTexture, randomX, WORLD_HEIGHT, 32, 32);
        droplet.setColor(Color.CYAN);
        entityManager.addEntity(droplet);

        // Physics: Falling (-Y gravity)
        GravityMovement grav = new GravityMovement(droplet);
        grav.setGravity(-500f); // Strong gravity
        grav.setMaxDropSpeed(400f);
        // Initial random slide
        grav.setVelocity((float) (Math.random() - 0.5) * 50f, 0);
        movementManager.registerComponent(grav);

        // Physics: Collision Logic (Including Bouncing)
        Collidable dropletCol = new Collidable() {
            @Override
            public Rectangle getBounds() {
                return new Rectangle(droplet.getX(), droplet.getY(), 32, 32);
            }

            @Override
            public CollisionType getType() {
                return new CollisionType("DROPLET", true, true);
            }

            @Override
            public void onCollision(CollisionResult result) {
                String otherType = result.getOther().getType().getName();

                if (otherType.equals("BUCKET")) {
                    // Bucket logic handled by bucket collider, we just disappear
                    droplet.setY(-2000f);
                } else if (otherType.equals("OBSTACLE")) {
                    // === BOUNCING PHYSICS ===
                    String direction = result.getDirection();
                    MovementComponent movement = movementManager.getComponent(droplet);

                    if (movement != null) {
                        float velX = movement.getVelocityX();
                        float velY = movement.getVelocityY();

                        // Push out of overlap first to prevent sticking
                        float overlapX = result.getOverlapX();
                        float overlapY = result.getOverlapY();

                        switch (direction) {
                            case "LEFT":
                                droplet.setX(droplet.getX() - overlapX);
                                movement.setVelocity(-velX * 0.8f, velY); // Bounce X
                                break;
                            case "RIGHT":
                                droplet.setX(droplet.getX() + overlapX);
                                movement.setVelocity(-velX * 0.8f, velY); // Bounce X
                                break;
                            case "TOP":
                                droplet.setY(droplet.getY() - overlapY);
                                movement.setVelocity(velX, -velY * 0.8f); // Bounce Y
                                break;
                            case "BOTTOM":
                                droplet.setY(droplet.getY() + overlapY);
                                movement.setVelocity(velX, -velY * 0.8f); // Bounce Y
                                break;
                        }
                    }
                }
            }
        };

        collisionManager.addObject(dropletCol);
        activeDroplets.add(droplet);
        activeDropletColliders.add(dropletCol);
    }

    @Override
    public void update(float dt) {
        // Pause Check
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (sceneManager != null)
                sceneManager.loadScene("pause");
            return;
        }

        // Input & Player Movement
        inputManager.update();
        float moveX = inputManager.getAxis(InputAxis.MOVE_X);
        float newBucketX = bucket.getX() + moveX * BUCKET_SPEED * dt;
        newBucketX = MathUtils.clamp(newBucketX, 0, WORLD_WIDTH - BUCKET_WIDTH);
        bucket.setX(newBucketX);

        // Spawning
        dropletSpawnTimer += dt;
        if (dropletSpawnTimer > 0.5f) {
            spawnDroplet();
            dropletSpawnTimer = 0f;
        }

        // Core Systems Update
        movementManager.update(dt);
        entityManager.update(dt);
        collisionManager.checkCollisions();

        // Cleanup Droplets below screen
        for (int i = activeDroplets.size - 1; i >= 0; i--) {
            if (activeDroplets.get(i).getY() < -50) {
                TextureObject d = activeDroplets.get(i);
                Collidable c = activeDropletColliders.get(i);

                entityManager.removeEntity(d, true);
                MovementComponent mc = movementManager.getComponent(d);
                if (mc != null)
                    movementManager.unregisterComponent(mc);
                collisionManager.removeObject(c);

                activeDroplets.removeIndex(i);
                activeDropletColliders.removeIndex(i);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 1. Draw Texture Entities (Bucket, Droplets)
        batch.begin();
        entityManager.draw(batch, null);
        batch.end();

        // 2. Draw Shape Entities (Obstacles)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(null, shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        if (bucketTexture != null)
            bucketTexture.dispose();
        if (dropletTexture != null)
            dropletTexture.dispose();
        if (audioManager != null)
            audioManager.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();

        entityManager.dispose();
        movementManager.clearComponents();
    }
}
