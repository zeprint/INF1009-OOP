package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * GameMaster - Core game coordinator following Abstract Game Engine principles
 * 
 * Responsibilities:
 * - Manager coordination
 * - Game lifecycle
 * - Asset loading
 * - Rendering delegation
 */
public class GameMaster extends ApplicationAdapter {
    
    // Core managers
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private InputManager inputManager;
    private AudioManager audioManager;
    
    // Rendering
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    
    // Game entities
    private TextureObject bucket;
    private Shapes staticCircle;
    private Shapes dynamicTriangle;
    private RotatingShape rotatingSquare;
    
    // Droplet system
    private Array<Droplet> activeDroplets;
    private Array<DropletCollider> activeDropletColliders;
    private float dropletSpawnTimer = 0f;
    
    // Input
    private InputBindings inputBindings;
    
    // Game constants
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;
    private static final float BUCKET_WIDTH = 64f;
    private static final float BUCKET_HEIGHT = 64f;
    private static final float BUCKET_SPEED = 300f;
    private static final float DROPLET_RADIUS = 8f;
    private static final float DROPLET_SPAWN_INTERVAL = 0.5f;
    
    @Override
    public void create() {
        // Initialize managers
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        audioManager = new AudioManager();
        
        // Initialize rendering
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize input
        inputBindings = new InputBindings();
        setupInputBindings();
        inputManager = new InputManager(inputBindings);
        
        // Initialize droplet tracking
        activeDroplets = new Array<>();
        activeDropletColliders = new Array<>();
        
        // Load assets
        loadAssets();
        
        // Initialize game entities
        initializeBucket();
        initializeStaticCircle();
        initializeDynamicTriangle();
        initializeRotatingSquare();
        
        // Spawn first droplet
        spawnDroplet();
    }
    
    /**
     * Configure input key bindings
     */
    private void setupInputBindings() {
        // Movement axes: A/D for left/right
        inputBindings.bindAxis(InputAxis.MOVE_X, com.badlogic.gdx.Input.Keys.A, com.badlogic.gdx.Input.Keys.D);
        
        // Action bindings
        inputBindings.bindAction(InputAction.TOGGLE_DEBUG, com.badlogic.gdx.Input.Keys.F1);
        inputBindings.bindAction(InputAction.TOGGLE_MUTE, com.badlogic.gdx.Input.Keys.M);
    }
    
    /**
     * Load game assets (textures, sounds)
     */
    private void loadAssets() {
        try {
            // Load sound effects
            audioManager.loadSound("click", "click.wav");
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Failed to load assets: " + e.getMessage());
        }
    }
    
    /**
     * Initialize player-controlled bucket (TextureObject)
     * - Static entity with no movement component
     * - Player controls via input
     */
    private void initializeBucket() {
        try {
            Texture bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
            bucket = new TextureObject(
                bucketTexture,
                WORLD_WIDTH / 2 - BUCKET_WIDTH / 2,
                50f,
                (int) BUCKET_HEIGHT,
                (int) BUCKET_WIDTH
            );
            bucket.setColor(Color.WHITE);
            entityManager.addEntity(bucket);
            
            // Register as collidable (passive - only receives collision events)
            BucketCollider bucketCollider = new BucketCollider(bucket);
            collisionManager.addObject(bucketCollider);
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Failed to load bucket: " + e.getMessage());
        }
    }
    
    /**
     * Initialize static circle (Shapes - CIRCLE type)
     * - Static: no movement component
     */
    private void initializeStaticCircle() {
        staticCircle = new Shapes(ShapeType.CIRCLE, 200f, 300f, Color.RED);
        staticCircle.setDimensions("radius", 30f);
        entityManager.addEntity(staticCircle);
        
        // Register as collidable
        ObstacleCollider obstacleCollider = new ObstacleCollider(staticCircle);
        collisionManager.addObject(obstacleCollider);
    }
    
    /**
     * Initialize dynamic triangle (Shapes - TRIANGLE type)
     * - Dynamic: uses oscillating movement component
     */
    private void initializeDynamicTriangle() {
        dynamicTriangle = new Shapes(ShapeType.TRIANGLE, 600f, 350f, Color.GREEN);
        dynamicTriangle.setDimensions("size", 40f);
        entityManager.addEntity(dynamicTriangle);
        
        // Add movement component for side-to-side motion
        MovementComponent triangleMovement = new MovementComponent(dynamicTriangle) {
            private float direction = 1f;
            private static final float SPEED = 150f;
            
            @Override
            public void update(float deltaTime) {
                if (!enabled) return;
                
                float newX = dynamicTriangle.getX() + direction * SPEED * deltaTime;
                
                // Bounce off screen edges
                if (newX <= 0 || newX >= WORLD_WIDTH - 40f) {
                    direction *= -1f;
                }
                dynamicTriangle.setX(Math.max(0, Math.min(WORLD_WIDTH - 40f, newX)));
            }
        };
        movementManager.registerComponent(triangleMovement);
        
        // Register as collidable
        ObstacleCollider obstacleCollider = new ObstacleCollider(dynamicTriangle);
        collisionManager.addObject(obstacleCollider);
    }
    
    /**
     * Initialize rotating square (RotatingShape)
     * - Dynamic: uses RotationComponent
     */
    private void initializeRotatingSquare() {
        rotatingSquare = new RotatingShape(400f, 450f, 25f, Color.YELLOW, false);
        rotatingSquare.setIsSquare(true);
        entityManager.addEntity(rotatingSquare);
        
        // Add rotation component
        RotationComponent rotationComponent = new RotationComponent(
            rotatingSquare,
            0f,    // Initial rotation angle
            0f,    // No X velocity
            0f     // No Y velocity
        );
        rotationComponent.setAngularVelocity(180f); // Degrees per second
        movementManager.registerComponent(rotationComponent);
        
        // Register as collidable
        ObstacleCollider obstacleCollider = new ObstacleCollider(rotatingSquare);
        collisionManager.addObject(obstacleCollider);
    }
    
    /**
     * Spawn a new falling droplet at random X position from top of screen
     */
    private void spawnDroplet() {
        try {
            // Random X position across screen width
            float randomX = (float) Math.random() * WORLD_WIDTH;
            
            // Spawn at top of visible area
            float spawnY = WORLD_HEIGHT - 50f;
            
            // Load droplet texture
            Texture dropletTexture = new Texture(Gdx.files.internal("droplet.png"));
            
            Droplet droplet = new Droplet(
                randomX,
                spawnY,
                32,  // Size: 32x32 pixels
                dropletTexture
            );
            entityManager.addEntity(droplet);
            
            // Add gravity movement component
            // Use NEGATIVE gravity so droplets fall DOWN
            GravityMovement gravityComponent = new GravityMovement(droplet, -500f);
            gravityComponent.setMaxDropSpeed(400f);
            
            movementManager.registerComponent(gravityComponent);
            droplet.setMovementComponent(gravityComponent);
            
            // Register as collidable
            DropletCollider dropletCollider = new DropletCollider(droplet);
            collisionManager.addObject(dropletCollider);
            
            // Track active droplets
            activeDroplets.add(droplet);
            activeDropletColliders.add(dropletCollider);
            
            Gdx.app.log("GameMaster", "✓ Droplet spawned at X=" + String.format("%.1f", randomX) + 
                        ", Y=" + String.format("%.1f", spawnY) + ", Total active: " + activeDroplets.size);
            Gdx.app.log("GameMaster", "  Total entities in manager: " + entityManager.getEntityList().size);
        } catch (Exception e) {
            Gdx.app.error("GameMaster", "Failed to spawn droplet: " + e.getMessage());
        }
    }
    
    @Override
    public void render() {
        // Input update
        inputManager.update();
        handleInput();
        
        // Game logic
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // Spawn new droplets periodically
        dropletSpawnTimer += deltaTime;
        if (dropletSpawnTimer >= DROPLET_SPAWN_INTERVAL) {
            spawnDroplet();
            dropletSpawnTimer = 0f;
        }
        
        // Remove droplets that fell off screen
        cleanupDroplets();
        
        // Update all components and entities
        movementManager.update(deltaTime);
        entityManager.update(deltaTime);
        collisionManager.checkCollisions();
        
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Debug: Log what's about to be rendered
        int totalEntities = entityManager.getEntityList().size;
        if (totalEntities != 4) {  // Should be 4 base entities + droplets
            Gdx.app.log("GameMaster", "Rendering " + totalEntities + " entities (4 static + " + 
                       (totalEntities - 4) + " droplets)");
        }
        
        // Render textures first (bucket)
        spriteBatch.begin();
        for (Entity entity : entityManager.getEntityList()) {
            entity.draw(spriteBatch);
        }
        spriteBatch.end();
        
        // Render shapes (circles, triangles, squares, droplets)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int shapeCount = 0;
        for (Entity entity : entityManager.getEntityList()) {
            entity.draw(shapeRenderer);
            if (entity instanceof RotatingShape) {
                shapeCount++;
                RotatingShape rs = (RotatingShape) entity;
                Gdx.app.log("Render", "Drawing RotatingShape at (" + rs.getX() + ", " + rs.getY() + ")");
            }
        }
        shapeRenderer.end();
        
        if (shapeCount > 0) {
            Gdx.app.log("GameMaster", "Drew " + shapeCount + " RotatingShapes");
        }
    }
    
    /**
     * Handle player input
     */
    private void handleInput() {
        // Bucket movement
        float moveX = inputManager.getAxis(InputAxis.MOVE_X);
        if (moveX != 0) {
            float newX = bucket.getX() + moveX * BUCKET_SPEED * Gdx.graphics.getDeltaTime();
            newX = Math.max(0, Math.min(WORLD_WIDTH - BUCKET_WIDTH, newX));
            bucket.setX(newX);
        }
        
        // Toggle mute
        if (inputManager.isActionTriggered(InputAction.TOGGLE_MUTE)) {
            audioManager.setMuted(!audioManager.isMuted());
        }
    }
    
    /**
     * Remove droplets that have fallen off the bottom of the screen
     */
    private void cleanupDroplets() {
        for (int i = activeDroplets.size - 1; i >= 0; i--) {
            Droplet droplet = activeDroplets.get(i);
            
            // Remove if fallen below screen (droplet size is 32x32)
            if (droplet.getY() < -32) {
                entityManager.removeEntity(droplet, true);
                movementManager.unregisterComponent(droplet.getMovementComponent());
                collisionManager.removeObject(activeDropletColliders.get(i));
                
                activeDroplets.removeIndex(i);
                activeDropletColliders.removeIndex(i);
                
                Gdx.app.log("GameMaster", "Droplet cleaned up, total remaining: " + activeDroplets.size);
            }
        }
    }
    
    @Override
    public void dispose() {
        audioManager.dispose();
        movementManager.clearComponents();
        entityManager.dispose();
        
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }
    
    // ============================================================
    // COLLIDABLE ADAPTER CLASSES
    // ============================================================
    
    /**
     * Adapter to make TextureObject bucket a Collidable
     */
    private class BucketCollider implements Collidable {
        private TextureObject entity;
        
        public BucketCollider(TextureObject entity) {
            this.entity = entity;
        }
        
        @Override
        public Rectangle getBounds() {
            return new Rectangle(
                entity.getX(),
                entity.getY(),
                entity.getWidth(),
                entity.getHeight()
            );
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("BUCKET", true, true);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            // Only respond to droplet collisions
            if (result.getOther() instanceof DropletCollider) {
                audioManager.playSound("click");
                
                // Get the droplet that was caught
                DropletCollider dropletCollider = (DropletCollider) result.getOther();
                Droplet caughtDroplet = dropletCollider.getDroplet();
                
                // Remove caught droplet
                if (caughtDroplet != null) {
                    entityManager.removeEntity(caughtDroplet, true);
                    movementManager.unregisterComponent(caughtDroplet.getMovementComponent());
                    collisionManager.removeObject(dropletCollider);
                    
                    activeDroplets.removeValue(caughtDroplet, true);
                    activeDropletColliders.removeValue(dropletCollider, true);
                }
            }
        }
    }
    
    /**
     * Adapter to make Droplet a Collidable
     * Active sender: bounces off obstacles
     */
    private class DropletCollider implements Collidable {
        private Droplet entity;
        
        public DropletCollider(Droplet entity) {
            this.entity = entity;
        }
        
        public Droplet getDroplet() {
            return entity;
        }
        
        @Override
        public Rectangle getBounds() {
            // Droplet is now a TextureObject, use width/height
            return new Rectangle(
                entity.getX(),
                entity.getY(),
                entity.getWidth(),
                entity.getHeight()
            );
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("DROPLET", false, true);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            // Don't bounce on bucket - let bucket handle the collision
            if (result.getOther() instanceof BucketCollider) {
                return;
            }
            
            // Bounce off all other obstacles
            String direction = result.getDirection();
            MovementComponent movement = entity.getMovementComponent();
            if (movement != null) {
                switch (direction) {
                    case "LEFT":
                    case "RIGHT":
                        movement.setVelocity(-movement.getVelocityX() * 0.8f, movement.getVelocityY());
                        break;
                    case "TOP":
                    case "BOTTOM":
                        movement.setVelocity(movement.getVelocityX(), -movement.getVelocityY() * 0.8f);
                        break;
                }
            }
        }
    }
    
    /**
     * Adapter to make Shapes/RotatingShape entities collidable obstacles
     */
    private class ObstacleCollider implements Collidable {
        private Entity entity;
        
        public ObstacleCollider(Entity entity) {
            this.entity = entity;
        }
        
        @Override
        public Rectangle getBounds() {
            if (entity instanceof Shapes) {
                Shapes shape = (Shapes) entity;
                ShapeType shapeType = shape.getShapeType();
                
                if (shapeType == ShapeType.CIRCLE) {
                    float radius = shape.getDimension("radius");
                    return new Rectangle(
                        shape.getX() - radius,
                        shape.getY() - radius,
                        radius * 2,
                        radius * 2
                    );
                } else if (shapeType == ShapeType.TRIANGLE) {
                    float size = shape.getDimension("size");
                    return new Rectangle(shape.getX(), shape.getY(), size, size);
                } else if (shapeType == ShapeType.RECTANGLE) {
                    float w = shape.getDimension("width");
                    float h = shape.getDimension("height");
                    return new Rectangle(shape.getX(), shape.getY(), w, h);
                }
            } else if (entity instanceof RotatingShape) {
                RotatingShape shape = (RotatingShape) entity;
                float radius = shape.getRadius();
                return new Rectangle(
                    shape.getX() - radius,
                    shape.getY() - radius,
                    radius * 2,
                    radius * 2
                );
            }
            return new Rectangle(entity.getX(), entity.getY(), 1, 1);
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("OBSTACLE", true, true);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            // Obstacles don't need to handle collisions
        }
    }
    
    // ============================================================
    // CUSTOM ENTITY CLASSES
    // ============================================================
    
    /**
     * Droplet entity - uses TextureObject for droplet.png image
     */
    private class Droplet extends TextureObject {
        private MovementComponent movementComponent;
        
        public Droplet(float x, float y, int size, Texture texture) {
            super(texture, x, y, size, size);
        }
        
        public void setMovementComponent(MovementComponent component) {
            this.movementComponent = component;
        }
        
        public MovementComponent getMovementComponent() {
            return movementComponent;
        }
    }
}
