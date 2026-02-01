package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * GameMaster - Comprehensive Integration Test
 * 
 * This demonstrates all three component systems:
 * 1. Movement Components (GravityMovement, RotationComponent)
 * 2. Entity Components (TextureObject, Shapes, RotatingShape)
 * 3. Input/Output Components (InputHandler, AudioManager)
 * 
 * GAME MECHANICS:
 * - Move bucket left/right to catch falling droplets
 * - Watch rotating shapes move across screen
 * - See shapes falling with gravity
 * - Score increases when catching droplets
 * 
 * CONTROLS:
 * - A/D or LEFT/RIGHT arrows: Move bucket
 * - M: Toggle sound on/off
 * - F2: Reset game
 * - ESC: Exit
 */
public class GameMaster extends ApplicationAdapter {
    
    // ========== CORE SYSTEMS ==========
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // Component Managers
    private EntityManager entityManager;
    private MovementManager movementManager;
    private InputOutputManagement ioManager;
    
    // ========== TEXTURES ==========
    private Texture bucketImage;
    private Texture dropletImage;
    
    // ========== PLAYER ENTITY ==========
    private TextureObject bucket;
    private static final float BUCKET_SPEED = 400f;
    private Rectangle bucketBounds;
    
    // ========== FALLING DROPLETS (TextureObject + GravityMovement) ==========
    private TextureObject droplet1;
    private TextureObject droplet2;
    private TextureObject droplet3;
    private GravityMovement droplet1Movement;
    private GravityMovement droplet2Movement;
    private GravityMovement droplet3Movement;
    private Rectangle droplet1Bounds;
    private Rectangle droplet2Bounds;
    private Rectangle droplet3Bounds;
    
    // ========== ROTATING SHAPES (RotatingShape + RotationComponent) ==========
    private RotatingShape rotatingCircle;
    private RotatingShape rotatingTriangle;
    private RotatingShape rotatingSquare;
    private RotationComponent circleRotation;
    private RotationComponent triangleRotation;
    private RotationComponent squareRotation;
    
    // ========== FALLING SHAPES (Shapes + GravityMovement) ==========
    private Shapes fallingRectangle;
    private Shapes fallingCircle;
    private Shapes fallingTriangle;
    private GravityMovement rectangleMovement;
    private GravityMovement circleShapeMovement;
    private GravityMovement triangleMovement;
    
    // ========== GAME STATE ==========
    private float screenWidth;
    private float screenHeight;
    private int score;
    private boolean soundEnabled;
    private float gameTime;
    
    @Override
    public void create() {
        // Initialize rendering systems
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        
        // Initialize component managers
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        ioManager = new InputOutputManagement();
        
        // Load assets
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        dropletImage = new Texture(Gdx.files.internal("droplet.png"));
        ioManager.getAudio().loadSound("click", "click.wav");
        
        // Get screen dimensions
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        // Initialize game state
        score = 0;
        soundEnabled = true;
        gameTime = 0f;
        
        // Create all game entities
        createPlayerBucket();
        createFallingDroplets();
        createRotatingShapes();
        createFallingShapes();
        
        System.out.println("\n===========================================");
        System.out.println("GAME ENGINE INTEGRATION TEST INITIALIZED");
        System.out.println("===========================================");
        System.out.println("\nCOMPONENTS ACTIVE:");
        System.out.println("✓ Movement System (" + movementManager.getComponentCount() + " components)");
        System.out.println("✓ Entity System (" + entityManager.getEntityList().size + " entities)");
        System.out.println("✓ Input/Output System");
        System.out.println("\nCONTROLS:");
        System.out.println("  A/D or LEFT/RIGHT: Move bucket");
        System.out.println("  M: Toggle sound");
        System.out.println("  F2: Reset game");
        System.out.println("  ESC: Exit");
        System.out.println("\nENTITY TYPES TESTING:");
        System.out.println("  • TextureObject (bucket + droplets)");
        System.out.println("  • RotatingShape (circle, triangle, square)");
        System.out.println("  • Shapes (falling circle, rectangle, triangle)");
        System.out.println("\nMOVEMENT TYPES TESTING:");
        System.out.println("  • GravityMovement (droplets + shapes)");
        System.out.println("  • RotationComponent (rotating shapes)");
        System.out.println("===========================================\n");
    }
    
    /**
     * Create player-controlled bucket
     * Tests: TextureObject entity, keyboard input
     */
    private void createPlayerBucket() {
        bucket = new TextureObject(
            bucketImage,
            screenWidth / 2 - 32,
            20,
            64,
            64
        );
        entityManager.addEntity(bucket);
        bucketBounds = new Rectangle(bucket.getX(), bucket.getY(), 64, 64);
    }
    
    /**
     * Create falling droplets with gravity
     * Tests: TextureObject + GravityMovement + collision detection
     */
    private void createFallingDroplets() {
        // Droplet 1
        droplet1 = new TextureObject(
            dropletImage,
            MathUtils.random(0, screenWidth - 32),
            screenHeight,
            32, 32
        );
        entityManager.addEntity(droplet1);
        droplet1Bounds = new Rectangle();
        
        droplet1Movement = new GravityMovement(droplet1, -200f);
        droplet1Movement.setVelocity(0, -50);
        droplet1Movement.setVerticalBounds(-50, screenHeight + 100);
        droplet1Movement.setHorizontalResetRange(0, screenWidth - 32);
        droplet1Movement.setMaxDropSpeed(500f);
        droplet1Movement.setSpeedMultiplier(1.05f);
        movementManager.registerComponent(droplet1Movement);
        
        // Droplet 2
        droplet2 = new TextureObject(
            dropletImage,
            MathUtils.random(0, screenWidth - 32),
            screenHeight + 150,
            32, 32
        );
        entityManager.addEntity(droplet2);
        droplet2Bounds = new Rectangle();
        
        droplet2Movement = new GravityMovement(droplet2, -220f);
        droplet2Movement.setVelocity(0, -70);
        droplet2Movement.setVerticalBounds(-50, screenHeight + 100);
        droplet2Movement.setHorizontalResetRange(0, screenWidth - 32);
        droplet2Movement.setMaxDropSpeed(550f);
        droplet2Movement.setSpeedMultiplier(1.08f);
        movementManager.registerComponent(droplet2Movement);
        
        // Droplet 3
        droplet3 = new TextureObject(
            dropletImage,
            MathUtils.random(0, screenWidth - 32),
            screenHeight + 300,
            32, 32
        );
        entityManager.addEntity(droplet3);
        droplet3Bounds = new Rectangle();
        
        droplet3Movement = new GravityMovement(droplet3, -250f);
        droplet3Movement.setVelocity(0, -90);
        droplet3Movement.setVerticalBounds(-50, screenHeight + 100);
        droplet3Movement.setHorizontalResetRange(0, screenWidth - 32);
        droplet3Movement.setMaxDropSpeed(600f);
        droplet3Movement.setSpeedMultiplier(1.1f);
        movementManager.registerComponent(droplet3Movement);
    }
    
    /**
     * Create rotating shapes with linear movement
     * Tests: RotatingShape + RotationComponent
     */
    private void createRotatingShapes() {
        // Rotating cyan circle - moves right and bounces
        rotatingCircle = new RotatingShape(
            50,
            screenHeight - 150,
            35,
            Color.CYAN,
            true
        );
        entityManager.addEntity(rotatingCircle);
        
        circleRotation = new RotationComponent(rotatingCircle, 0, 120, 0);
        circleRotation.setAngularVelocity(90f);
        movementManager.registerComponent(circleRotation);
        
        // Rotating magenta triangle - moves diagonally
        rotatingTriangle = new RotatingShape(
            screenWidth / 2,
            screenHeight / 2 + 100,
            45,
            Color.MAGENTA,
            false
        );
        entityManager.addEntity(rotatingTriangle);
        
        triangleRotation = new RotationComponent(rotatingTriangle, 0, 60, 40);
        triangleRotation.setAngularVelocity(120f);
        movementManager.registerComponent(triangleRotation);
        
        // Rotating yellow square - moves left
        rotatingSquare = new RotatingShape(
            screenWidth - 80,
            screenHeight / 2 - 50,
            40,
            Color.YELLOW,
            false
        );
        rotatingSquare.setIsSquare(true);
        entityManager.addEntity(rotatingSquare);
        
        squareRotation = new RotationComponent(rotatingSquare, 45, -80, 0);
        squareRotation.setAngularVelocity(150f);
        movementManager.registerComponent(squareRotation);
    }
    
    /**
     * Create falling shapes with gravity
     * Tests: Shapes (CIRCLE, RECTANGLE, TRIANGLE) + GravityMovement
     */
    private void createFallingShapes() {
        // Falling green rectangle
        fallingRectangle = new Shapes(
            ShapeType.RECTANGLE,
            screenWidth * 0.25f,
            screenHeight,
            Color.GREEN
        );
        fallingRectangle.setDimensions("width", 50);
        fallingRectangle.setDimensions("height", 70);
        entityManager.addEntity(fallingRectangle);
        
        rectangleMovement = new GravityMovement(fallingRectangle, -180f);
        rectangleMovement.setVelocity(15, 0);
        rectangleMovement.setVerticalBounds(-100, screenHeight + 50);
        rectangleMovement.setHorizontalResetRange(50, screenWidth - 100);
        rectangleMovement.setMaxDropSpeed(450f);
        rectangleMovement.setSpeedMultiplier(1.06f);
        movementManager.registerComponent(rectangleMovement);
        
        // Falling orange circle
        fallingCircle = new Shapes(
            ShapeType.CIRCLE,
            screenWidth * 0.75f,
            screenHeight + 200,
            Color.ORANGE
        );
        fallingCircle.setDimensions("radius", 30);
        entityManager.addEntity(fallingCircle);
        
        circleShapeMovement = new GravityMovement(fallingCircle, -200f);
        circleShapeMovement.setVelocity(-20, 0);
        circleShapeMovement.setVerticalBounds(-100, screenHeight + 50);
        circleShapeMovement.setHorizontalResetRange(50, screenWidth - 80);
        circleShapeMovement.setMaxDropSpeed(480f);
        circleShapeMovement.setSpeedMultiplier(1.07f);
        movementManager.registerComponent(circleShapeMovement);
        
        // Falling red triangle
        fallingTriangle = new Shapes(
            ShapeType.TRIANGLE,
            screenWidth * 0.5f,
            screenHeight + 400,
            Color.RED
        );
        fallingTriangle.setDimensions("size", 60);
        entityManager.addEntity(fallingTriangle);
        
        triangleMovement = new GravityMovement(fallingTriangle, -190f);
        triangleMovement.setVelocity(0, 0);
        triangleMovement.setVerticalBounds(-100, screenHeight + 50);
        triangleMovement.setHorizontalResetRange(50, screenWidth - 110);
        triangleMovement.setMaxDropSpeed(470f);
        triangleMovement.setSpeedMultiplier(1.065f);
        movementManager.registerComponent(triangleMovement);
    }
    
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        gameTime += deltaTime;
        
        // Clear screen
        ScreenUtils.clear(0.05f, 0.05f, 0.2f, 1);
        
        // Handle input
        handleInput(deltaTime);
        
        // Update all movement components
        movementManager.update(deltaTime);
        
        // Handle rotating shape boundaries (bounce)
        handleRotatingShapeBoundaries();
        
        // Check collisions
        checkCollisions();
        
        // Render everything
        renderGame();
    }
    
    /**
     * Handle keyboard input
     * Tests: InputHandler functionality
     */
    private void handleInput(float deltaTime) {
        // Move bucket left
        if (ioManager.isKeyDown("A") || ioManager.isKeyDown("LEFT")) {
            bucket.setX(bucket.getX() - BUCKET_SPEED * deltaTime);
            if (bucket.getX() < 0) {
                bucket.setX(0);
            }
        }
        
        // Move bucket right
        if (ioManager.isKeyDown("D") || ioManager.isKeyDown("RIGHT")) {
            bucket.setX(bucket.getX() + BUCKET_SPEED * deltaTime);
            if (bucket.getX() > screenWidth - 64) {
                bucket.setX(screenWidth - 64);
            }
        }
        
        // Toggle sound
        if (ioManager.isKeyJustPressed("M")) {
            soundEnabled = !soundEnabled;
            ioManager.getAudio().setVolume(soundEnabled ? 1.0f : 0.0f);
            System.out.println("Sound: " + (soundEnabled ? "ON" : "OFF"));
        }
        
        // Reset game
        if (ioManager.isKeyJustPressed("F2")) {
            resetGame();
        }
        
        // Exit
        if (ioManager.isKeyJustPressed("ESC") || ioManager.isKeyJustPressed("ESCAPE")) {
            Gdx.app.exit();
        }
    }
    
    /**
     * Make rotating shapes bounce off screen edges
     * Tests: Entity position manipulation
     */
    private void handleRotatingShapeBoundaries() {
        // Circle bounces horizontally
        if (rotatingCircle.getX() < 0 || rotatingCircle.getX() > screenWidth) {
            circleRotation.setVelocity(-circleRotation.getVelocityX(), 0);
        }
        
        // Triangle bounces in both directions
        if (rotatingTriangle.getX() < 0 || rotatingTriangle.getX() > screenWidth) {
            triangleRotation.setVelocity(-triangleRotation.getVelocityX(), triangleRotation.getVelocityY());
        }
        if (rotatingTriangle.getY() < 100 || rotatingTriangle.getY() > screenHeight - 100) {
            triangleRotation.setVelocity(triangleRotation.getVelocityX(), -triangleRotation.getVelocityY());
        }
        
        // Square bounces horizontally
        if (rotatingSquare.getX() < 0 || rotatingSquare.getX() > screenWidth) {
            squareRotation.setVelocity(-squareRotation.getVelocityX(), 0);
        }
    }
    
    /**
     * Check collisions between bucket and droplets
     * Tests: Collision detection, audio playback
     */
    private void checkCollisions() {
        bucketBounds.set(bucket.getX(), bucket.getY(), 64, 64);
        
        // Check droplet 1
        droplet1Bounds.set(droplet1.getX(), droplet1.getY(), 32, 32);
        if (bucketBounds.overlaps(droplet1Bounds) && droplet1.getY() > 0) {
            score++;
            droplet1.setY(screenHeight + MathUtils.random(0, 200));
            droplet1.setX(MathUtils.random(0, screenWidth - 32));
            if (soundEnabled) {
                ioManager.getAudio().playSound("click");
            }
        }
        
        // Check droplet 2
        droplet2Bounds.set(droplet2.getX(), droplet2.getY(), 32, 32);
        if (bucketBounds.overlaps(droplet2Bounds) && droplet2.getY() > 0) {
            score++;
            droplet2.setY(screenHeight + MathUtils.random(0, 200));
            droplet2.setX(MathUtils.random(0, screenWidth - 32));
            if (soundEnabled) {
                ioManager.getAudio().playSound("click");
            }
        }
        
        // Check droplet 3
        droplet3Bounds.set(droplet3.getX(), droplet3.getY(), 32, 32);
        if (bucketBounds.overlaps(droplet3Bounds) && droplet3.getY() > 0) {
            score++;
            droplet3.setY(screenHeight + MathUtils.random(0, 200));
            droplet3.setX(MathUtils.random(0, screenWidth - 32));
            if (soundEnabled) {
                ioManager.getAudio().playSound("click");
            }
        }
    }
    
    /**
     * Render all entities and UI
     * Tests: EntityManager draw methods, ShapeRenderer, SpriteBatch
     */
    private void renderGame() {
        // Draw textured entities (bucket + droplets)
        batch.begin();
        entityManager.draw(batch, null);
        
        // Draw UI text
        font.draw(batch, "Score: " + score, 20, screenHeight - 20);
        font.draw(batch, "Time: " + (int)gameTime + "s", 20, screenHeight - 60);
        font.draw(batch, "Sound: " + (soundEnabled ? "ON" : "OFF"), 20, screenHeight - 100);
        font.draw(batch, "Entities: " + entityManager.getEntityList().size, 20, screenHeight - 140);
        font.draw(batch, "Movement Components: " + movementManager.getComponentCount(), 20, screenHeight - 180);
        
        batch.end();
        
        // Draw shapes (rotating + falling)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(null, shapeRenderer);
        shapeRenderer.end();
    }
    
    /**
     * Reset game state
     * Tests: Component re-initialization
     */
    private void resetGame() {
        score = 0;
        gameTime = 0f;
        
        // Reset droplets
        droplet1.setY(screenHeight);
        droplet1.setX(MathUtils.random(0, screenWidth - 32));
        droplet1Movement.setVelocity(0, -50);
        
        droplet2.setY(screenHeight + 150);
        droplet2.setX(MathUtils.random(0, screenWidth - 32));
        droplet2Movement.setVelocity(0, -70);
        
        droplet3.setY(screenHeight + 300);
        droplet3.setX(MathUtils.random(0, screenWidth - 32));
        droplet3Movement.setVelocity(0, -90);
        
        // Reset bucket
        bucket.setX(screenWidth / 2 - 32);
        
        System.out.println("Game Reset!");
    }
    
    @Override
    public void dispose() {
        // Dispose textures
        if (bucketImage != null) bucketImage.dispose();
        if (dropletImage != null) dropletImage.dispose();
        
        // Dispose rendering
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        
        // Dispose audio
        if (ioManager != null) ioManager.dispose();
        
        System.out.println("\n===========================================");
        System.out.println("Game Engine Test Completed Successfully!");
        System.out.println("Final Score: " + score);
        System.out.println("Total Time: " + (int)gameTime + " seconds");
        System.out.println("===========================================\n");
    }
}
