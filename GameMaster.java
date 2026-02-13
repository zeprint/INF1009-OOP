package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

// GameMaster class - Central controller managing all game systems and entities
// Handles initialization, rendering, and cleanup of the game
public class GameMaster implements ApplicationListener {
    
    // Core rendering components
    private SpriteBatch batch;
    private ShapeRenderer shape;
    
    // Game system managers (direct associations)
    private EntityManager entityManager;
    private MovementManager movementManager;
    private InputManager inputManager;
    private AudioManager audioManager;
    private CollisionManager collisionManager;
    private SceneManager sceneManager;
    
    // Game entities
    private TextureObject bucket;
    private Shapes staticCircle;
    private Shapes dynamicTriangle;
    private Shapes rotatingRectangle;
    private Array<Droplet> droplets;
    private float dropletSpawnTimer;
    private float soundCooldown; // Prevent erratic sound playing
    
    // Constants
    private static final float DROPLET_SPAWN_INTERVAL = 1.0f;
    private static final float BUCKET_SPEED = 300f; // pixels per second
    private static final float SOUND_COOLDOWN_TIME = 0.3f; // 300ms between sounds
    
    // Inner class for Droplet entity
    private class Droplet extends TextureObject {
        private MovementComponent movementComponent;
        private boolean caught = false;
        
        public Droplet(float x, float y, int size, Texture texture) {
            super(texture, x, y, size, size);
        }
        
        public void setMovementComponent(MovementComponent component) {
            this.movementComponent = component;
        }
        
        public MovementComponent getMovementComponent() {
            return movementComponent;
        }
        
        public boolean isCaught() {
            return caught;
        }
        
        public void setCaught(boolean caught) {
            this.caught = caught;
        }
    }
    
    // Inner class for Bucket collision detection
    private class BucketCollider implements Collidable {
        private TextureObject entity;
        
        public BucketCollider(TextureObject entity) {
            this.entity = entity;
        }
        
        @Override
        public Rectangle getBounds() {
            return new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("BUCKET", true, true);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            if (result.getOther() instanceof DropletCollider) {
                DropletCollider dropletCollider = (DropletCollider) result.getOther();
                Droplet caughtDroplet = dropletCollider.getDroplet();
                
                // Only process if droplet hasn't been caught already
                if (!caughtDroplet.isCaught()) {
                    caughtDroplet.setCaught(true);
                    
                    // Play sound when droplet is caught
                    if (soundCooldown <= 0) {
                        audioManager.playSound("click");
                        soundCooldown = SOUND_COOLDOWN_TIME;
                    }
                    
                    // Remove the droplet and its collider
                    collisionManager.removeObject(dropletCollider);
                    entityManager.removeEntity(caughtDroplet, true);
                    droplets.removeValue(caughtDroplet, true);
                }
            }
        }
    }
    
    // Inner class for Droplet collision detection
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
            return new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("DROPLET", true, true);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            // Bounce off shapes when colliding
            if (result.getOther() instanceof ShapeCollider) {
                ShapeCollider shapeCollider = (ShapeCollider) result.getOther();
                Shapes shape = shapeCollider.getShape();
                
                // Print collision message
                String shapeName = shape.getShapeType().toString();
                shapeName = shapeName.substring(0, 1).toUpperCase() + shapeName.substring(1).toLowerCase();
                System.out.println("Collision between Droplet and " + shapeName);
                
                MovementComponent movement = entity.getMovementComponent();
                if (movement instanceof GravityMovement) {
                    GravityMovement gravityMove = (GravityMovement) movement;
                    try {
                        // Get collision data from result
                        String direction = result.getDirection();
                        float overlapX = result.getOverlapX();
                        float overlapY = result.getOverlapY();
                        
                        float velX = gravityMove.getVelocityX();
                        float velY = gravityMove.getVelocityY();
                        
                        // Push droplet away from collision and apply escape velocity
                        switch (direction) {
                            case "LEFT":
                                // Shape is to the LEFT, push droplet LEFT with escape velocity
                                entity.setX(entity.getX() - (overlapX + 10f));
                                // Clamp within screen bounds
                                if (entity.getX() < 0) entity.setX(0);
                                if (entity.getX() + entity.getWidth() > 800) entity.setX(800 - entity.getWidth());
                                // Move left to escape, ensure vertical movement too if falling into shape
                                if (velX < 0) {
                                    float escapeY = velY != 0 ? velY : 50f;
                                    gravityMove.setVelocity(-100f, escapeY);
                                }
                                break;
                            case "RIGHT":
                                // Shape is to the RIGHT, push droplet RIGHT with escape velocity
                                entity.setX(entity.getX() + (overlapX + 10f));
                                // Clamp within screen bounds
                                if (entity.getX() < 0) entity.setX(0);
                                if (entity.getX() + entity.getWidth() > 800) entity.setX(800 - entity.getWidth());
                                // Move right to escape, ensure vertical movement too if falling into shape
                                if (velX > 0) {
                                    float escapeY = velY != 0 ? velY : 50f;
                                    gravityMove.setVelocity(100f, escapeY);
                                }
                                break;
                            case "TOP":
                                // Shape is ABOVE, push droplet DOWN and move laterally to escape
                                entity.setY(entity.getY() - (overlapY + 10f));
                                // Clamp within screen bounds
                                if (entity.getX() < 0) entity.setX(0);
                                if (entity.getX() + entity.getWidth() > 800) entity.setX(800 - entity.getWidth());
                                // Move down to escape, AND move laterally (left/right) to clear shape
                                if (velY > 0) {
                                    float lateralVel = (MathUtils.randomBoolean() ? -100f : 100f);
                                    gravityMove.setVelocity(lateralVel, -100f);
                                }
                                break;
                            case "BOTTOM":
                                // Shape is BELOW, push droplet UP and move laterally to escape
                                entity.setY(entity.getY() + (overlapY + 10f));
                                // Clamp within screen bounds
                                if (entity.getX() < 0) entity.setX(0);
                                if (entity.getX() + entity.getWidth() > 800) entity.setX(800 - entity.getWidth());
                                // Move up to escape, AND move laterally (left/right) to clear shape
                                if (velY < 0) {
                                    float lateralVel = (MathUtils.randomBoolean() ? -100f : 100f);
                                    gravityMove.setVelocity(lateralVel, 100f);
                                }
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Failed to bounce droplet: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    // Inner class for Shape collision detection
    private class ShapeCollider implements Collidable {
        private Shapes entity;
        
        public ShapeCollider(Shapes entity) {
            this.entity = entity;
        }
        
        public Shapes getShape() {
            return entity;
        }
        
        @Override
        public Rectangle getBounds() {
            // Create bounds based on shape type
            if (entity.getShapeType() == ShapeType.CIRCLE) {
                float radius = entity.getDimension("radius");
                return new Rectangle(entity.getX() - radius, entity.getY() - radius, radius * 2, radius * 2);
            } else if (entity.getShapeType() == ShapeType.RECTANGLE) {
                float width = entity.getDimension("width");
                float height = entity.getDimension("height");
                return new Rectangle(entity.getX(), entity.getY(), width, height);
            } else if (entity.getShapeType() == ShapeType.TRIANGLE) {
                float size = entity.getDimension("size");
                return new Rectangle(entity.getX(), entity.getY(), size, size);
            }
            return new Rectangle(0, 0, 0, 0);
        }
        
        @Override
        public CollisionType getType() {
            return new CollisionType("SHAPE", true, false);
        }
        
        @Override
        public void onCollision(CollisionResult result) {
            // Shapes don't react to collisions
        }
    }
    
    // Constructor
    public GameMaster() {
        this.batch = null;
        this.shape = null;
        this.entityManager = null;
        this.movementManager = null;
        this.inputManager = null;
        this.audioManager = null;
        this.collisionManager = null;
        this.sceneManager = null;
        this.droplets = new Array<>();
        this.dropletSpawnTimer = 0f;
        this.soundCooldown = 0f;
        this.rotatingRectangle = null;
    }
    
    // Initializes the game systems and rendering components
    public void create() {
        // Initialize rendering components
        if (batch == null) {
            batch = new SpriteBatch();
        }
        if (shape == null) {
            shape = new ShapeRenderer();
        }
        
        // Initialize game system managers
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        if (movementManager == null) {
            movementManager = new MovementManager();
        }
        if (inputManager == null) {
            InputBindings bindings = new InputBindings();
            // Set up key bindings since InputBindings constructor is empty
            bindings.bindAxis(InputAxis.MOVE_X, Keys.A, Keys.D);
            bindings.bindAxis(InputAxis.MOVE_X, Keys.LEFT, Keys.RIGHT);
            bindings.bindAction(InputAction.TOGGLE_MOUSE_MODE, Keys.N);
            bindings.bindAction(InputAction.TOGGLE_DEBUG, Keys.F3);
            bindings.bindAction(InputAction.TOGGLE_MUTE, Keys.M);
            bindings.bindAction(InputAction.PAUSE, Keys.P);
            inputManager = new InputManager(bindings);
        }
        if (audioManager == null) {
            audioManager = new AudioManager();
        }
        if (collisionManager == null) {
            collisionManager = new CollisionManager();
        }
        if (sceneManager == null) {
            sceneManager = new SceneManager();
            
            // Create empty simulation scene placeholder (GameMaster handles actual gameplay)
            Scene emptySimScene = new Scene() {
                @Override
                public void update(float dt) {
                    // GameMaster handles updates
                }
                @Override
                public void render(SpriteBatch batch) {
                    // GameMaster handles rendering
                }
            };
            sceneManager.addScene("simulation", emptySimScene);
            
            // Create pause scene
            PauseScene pauseScene = new PauseScene();
            sceneManager.addScene("pause", pauseScene);
            
            // Start with simulation scene
            sceneManager.loadScene("simulation");
        }
        
        // Load audio
        audioManager.loadSound("click", "click.wav");
        
        // Initialize game entities
        initializeBucket();
        initializeStaticCircle();
        initializeDynamicTriangle();
        initializeRotatingRectangle();
        
        // Spawn initial droplets
        for (int i = 0; i < 3; i++) {
            spawnDroplet();
        }
    }
    
    // Initialize the bucket entity
    private void initializeBucket() {
        try {
            Texture bucketTexture = new Texture("bucket.png");
            bucket = new TextureObject(bucketTexture, 350f, 50f, 64, 64);
            bucket.setColor(Color.WHITE);
            entityManager.addEntity(bucket);
            
            // Add collision detection for bucket
            BucketCollider bucketCollider = new BucketCollider(bucket);
            collisionManager.addObject(bucketCollider);
        } catch (Exception e) {
            System.err.println("Failed to load bucket: " + e.getMessage());
        }
    }
    
    // Initialize the static circle
    private void initializeStaticCircle() {
        staticCircle = new Shapes(ShapeType.CIRCLE, 600f, 450f, Color.RED);
        staticCircle.setDimensions("radius", 30f);
        entityManager.addEntity(staticCircle);
        
        // Add collision detection for the circle
        ShapeCollider circleCollider = new ShapeCollider(staticCircle);
        collisionManager.addObject(circleCollider);
    }
    
    // Initialize the dynamic triangle with movement
    private void initializeDynamicTriangle() {
        try {
            dynamicTriangle = new Shapes(ShapeType.TRIANGLE, 200f, 250f, Color.GREEN);
            dynamicTriangle.setDimensions("size", 50f);
            entityManager.addEntity(dynamicTriangle);
            
            // Add rotation and movement
            RotationComponent triangleMovement = new RotationComponent(dynamicTriangle, 0f, 100f, 0f);
            triangleMovement.setAngularVelocity(45f);
            movementManager.registerComponent(triangleMovement);
            
            // Add collision detection for the triangle
            ShapeCollider triangleCollider = new ShapeCollider(dynamicTriangle);
            collisionManager.addObject(triangleCollider);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to initialize dynamic triangle: " + e.getMessage());
        }
    }
    // Initialize the rotating rectangle
    private void initializeRotatingRectangle() {
        try {
            rotatingRectangle = new Shapes(ShapeType.RECTANGLE, 100f, 450f, Color.CYAN);
            rotatingRectangle.setDimensions("width", 80f);
            rotatingRectangle.setDimensions("height", 40f);
            // Note: Not added to entityManager, rendered separately with rotation
            
            // Add rotation component - rotation will be managed by the RotationComponent system
            RotationComponent rectangleRotation = new RotationComponent(rotatingRectangle, 0f, 0f, 0f);
            rectangleRotation.setAngularVelocity(60f);
            movementManager.registerComponent(rectangleRotation);
            
            // Add collision detection for the rectangle
            ShapeCollider rectangleCollider = new ShapeCollider(rotatingRectangle);
            collisionManager.addObject(rectangleCollider);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to initialize rotating rectangle: " + e.getMessage());
        }
    }
    // Spawn a new droplet
    private void spawnDroplet() {
        try {
            float randomX = MathUtils.random(0f, 800f - 32f);
            Texture dropletTexture = new Texture("droplet.png");
            Droplet droplet = new Droplet(randomX, 600f, 32, dropletTexture);
            
            // Add gravity movement
            GravityMovement dropletMovement = new GravityMovement(droplet, -200f);
            dropletMovement.setMaxDropSpeed(200f);
            droplet.setMovementComponent(dropletMovement);
            movementManager.registerComponent(dropletMovement);
            
            entityManager.addEntity(droplet);
            droplets.add(droplet);
            
            // Add collision detection
            DropletCollider dropletCollider = new DropletCollider(droplet);
            collisionManager.addObject(dropletCollider);
        } catch (Exception e) {
            System.err.println("Failed to spawn droplet: " + e.getMessage());
        }
    }
    
    // Renders the current frame with all active entities and scenes
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Calculate delta time
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // Update scene system first (for pause menu handling)
        if (sceneManager != null) {
            sceneManager.update(deltaTime);
            
            // If in pause scene, render game entities then pause overlay and skip game updates
            if (sceneManager.getCurrentScene() instanceof PauseScene) {
                // Render game entities first (frozen state)
                if (batch != null && entityManager != null) {
                    batch.begin();
                    entityManager.draw(batch, shape);
                    batch.end();
                }
                
                if (shape != null && entityManager != null) {
                    shape.begin(ShapeRenderer.ShapeType.Filled);
                    entityManager.draw(batch, shape);
                    
                    // Draw rotating rectangle with rotation from RotationComponent
                    if (rotatingRectangle != null) {
                        float rotation = 0f;
                        MovementComponent component = movementManager.getComponent(rotatingRectangle);
                        if (component instanceof Rotatable) {
                            RotationComponent rotComp = (RotationComponent) component;
                            rotation = rotComp.getRotationAngle();
                        }
                        
                        shape.setColor(rotatingRectangle.getColor());
                        float x = rotatingRectangle.getX();
                        float y = rotatingRectangle.getY();
                        float w = rotatingRectangle.getDimension("width");
                        float h = rotatingRectangle.getDimension("height");
                        
                        shape.rect(x, y, w / 2, h / 2, w, h, 1, 1, rotation);
                    }
                    
                    shape.end();
                }
                
                // Render pause overlay
                sceneManager.render();
                return;
            }
        }
        
        // Update sound cooldown
        if (soundCooldown > 0) {
            soundCooldown -= deltaTime;
        }
        
        // Update droplet spawn timer
        dropletSpawnTimer += deltaTime;
        if (dropletSpawnTimer >= DROPLET_SPAWN_INTERVAL) {
            spawnDroplet();
            dropletSpawnTimer = 0f;
        }
        
        // Update input system
        if (inputManager != null) {
            inputManager.update();
            
            // Check for pause input via ESC key (only when NOT already in pause scene)
            // The PauseScene handles its own resume logic with P key
            // Also skip if scene just changed this frame to avoid double detection
            if (sceneManager != null 
                && !(sceneManager.getCurrentScene() instanceof PauseScene)
                && !sceneManager.hasSceneChangedThisFrame()) {
                if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                    sceneManager.loadScene("pause");
                    return;
                }
            }
            
            // Handle input actions
            if (inputManager.isActionTriggered(InputAction.TOGGLE_MUTE)) {
                if (audioManager != null) {
                    audioManager.setMuted(!audioManager.isMuted());
                    System.out.println("Audio " + (audioManager.isMuted() ? "muted" : "unmuted"));
                }
            }
            
            if (inputManager.isActionTriggered(InputAction.TOGGLE_DEBUG)) {
                System.out.println("Debug mode toggled");
            }
            
            if (inputManager.isActionTriggered(InputAction.TOGGLE_MOUSE_MODE)) {
                System.out.println("Mouse mode: " + inputManager.isMouseMode());
            }
            
            // Handle bucket movement with input or mouse
            if (bucket != null) {
                if (inputManager.isMouseMode()) {
                    // Mouse mode: follow mouse position
                    float mouseX = inputManager.getMouseX();
                    // Clamp bucket position to screen bounds
                    if (mouseX < 0) mouseX = 0;
                    if (mouseX > 800 - bucket.getWidth()) mouseX = 800 - bucket.getWidth();
                    bucket.setX(mouseX);
                } else {
                    // Keyboard mode: use MOVE_X axis
                    float moveX = inputManager.getAxis(InputAxis.MOVE_X);
                    if (moveX != 0) {
                        float newX = bucket.getX() + (moveX * BUCKET_SPEED * deltaTime);
                        // Clamp bucket position to screen bounds
                        if (newX < 0) newX = 0;
                        if (newX > 800 - bucket.getWidth()) newX = 800 - bucket.getWidth();
                        bucket.setX(newX);
                    }
                }
            }
        }
        
        // Update movement components via MovementManager
        if (movementManager != null) {
            movementManager.update(deltaTime);
        }
        
        // Bounce triangle left-right within screen bounds
        if (dynamicTriangle != null) {
            float triangleSize = dynamicTriangle.getDimension("size");
            float triangleX = dynamicTriangle.getX();
            
            // Get triangle's movement component
            MovementComponent triangleMovement = movementManager.getComponent(dynamicTriangle);
            
            // Bounce at left boundary
            if (triangleX < 0) {
                dynamicTriangle.setX(0);
                if (triangleMovement != null) {
                    try {
                        // Reverse horizontal velocity to bounce right
                        triangleMovement.setVelocity(-triangleMovement.getVelocityX(), triangleMovement.getVelocityY());
                    } catch (IllegalArgumentException e) {
                        // Silently handle error
                    }
                }
            } else if (triangleX + triangleSize > 800) {
                // Bounce at right boundary
                dynamicTriangle.setX(800 - triangleSize);
                if (triangleMovement != null) {
                    try {
                        // Reverse horizontal velocity to bounce left
                        triangleMovement.setVelocity(-triangleMovement.getVelocityX(), triangleMovement.getVelocityY());
                    } catch (IllegalArgumentException e) {
                        // Silently handle error
                    }
                }
            }
        }
        
        // Update all entities
        if (entityManager != null) {
            entityManager.update(deltaTime);
        }
        
        // Check collisions
        if (collisionManager != null) {
            collisionManager.checkCollisions();
        }
        
        // Enforce droplet boundary constraints and cleanup off-screen droplets
        for (int i = droplets.size - 1; i >= 0; i--) {
            Droplet droplet = droplets.get(i);
            
            // Clamp droplet X position within screen bounds
            if (droplet.getX() < 0) {
                droplet.setX(0);
            } else if (droplet.getX() + droplet.getWidth() > 800) {
                droplet.setX(800 - droplet.getWidth());
            }
            
            // Remove droplets that fall below the screen
            if (droplet.getY() > 800) {
                entityManager.removeEntity(droplet, true);
                droplets.removeIndex(i);
            }
        }
        
        // Render entities directly
        if (batch != null && entityManager != null) {
            batch.begin();
            entityManager.draw(batch, shape);
            batch.end();
        }
        
        // Render shapes
        if (shape != null && entityManager != null) {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            entityManager.draw(batch, shape);
            
            // Draw rotating rectangle with rotation from RotationComponent
            if (rotatingRectangle != null) {
                // Get rotation from the RotationComponent via MovementManager
                float rotation = 0f;
                MovementComponent component = movementManager.getComponent(rotatingRectangle);
                if (component instanceof Rotatable) {
                    RotationComponent rotComp = (RotationComponent) component;
                    rotation = rotComp.getRotationAngle();
                }
                
                shape.setColor(rotatingRectangle.getColor());
                float x = rotatingRectangle.getX();
                float y = rotatingRectangle.getY();
                float w = rotatingRectangle.getDimension("width");
                float h = rotatingRectangle.getDimension("height");
                
                // Draw rotated rectangle using rotation from component system
                shape.rect(x, y, w / 2, h / 2, w, h, 1, 1, rotation);
            }
            
            shape.end();
        }
    }
    
    // Disposes of all resources and performs cleanup
    public void dispose() {
        // Dispose of game system managers
        if (entityManager != null) {
            entityManager.dispose();
            entityManager = null;
        }
        if (audioManager != null) {
            audioManager.dispose();
            audioManager = null;
        }
        if (sceneManager != null) {
            sceneManager.dispose();
            sceneManager = null;
        }
        if (inputManager != null) {
            inputManager.dispose();
            inputManager = null;
        }
        
        // Dispose of rendering components
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (shape != null) {
            shape.dispose();
            shape = null;
        }
    }
    
    // Called when the application is paused
    @Override
    public void pause() {
        // No specific action needed on pause
    }
    
    // Called when the application is resumed from a paused state
    @Override
    public void resume() {
        // No specific action needed on resume
    }
    
    // Called when the screen is resized
    @Override
    public void resize(int width, int height) {
        // No specific action needed on resize
    }
    
    // Getter methods for accessing game systems
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public MovementManager getMovementManager() {
        return movementManager;
    }
    
    public InputManager getInputManager() {
        return inputManager;
    }
    
    public AudioManager getAudioManager() {
        return audioManager;
    }
    
    public CollisionManager getCollisionManager() {
        return collisionManager;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
    
    public ShapeRenderer getShapeRenderer() {
        return shape;
    }
    
    // Setter methods for dependency injection
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void setMovementManager(MovementManager movementManager) {
        this.movementManager = movementManager;
    }
    
    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }
    
    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
    
    public void setCollisionManager(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
