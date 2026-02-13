package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.math.MathUtils;

// Drop speed with acceleration and speed reset 
public class GravityMovement extends MovementComponent implements Acceleratable {
    protected static final float DEFAULT_GRAVITY = 9.81f;
    protected static final float DEFAULT_ACCELERATION = 0f;
    protected static final float DEFAULT_SPEED_MULTIPLIER = 2f;
    protected static final float DEFAULT_MAX_DROP_SPEED = 10f;
    
    private float gravity;
    private float accelerationX;
    private float accelerationY;
    private float speedMultiplier;
    private float maxDropSpeed;
    private float bottomBoundaryY;
    private float resetTopY;
    private boolean boundsConfigured;
    private boolean randomizeXOnReset;
    private float resetMinX;
    private float resetMaxX;
    private boolean resetXConfigured;
    
    public GravityMovement(Entity entity) {
        super(entity);
        this.gravity = DEFAULT_GRAVITY;
        this.accelerationX = DEFAULT_ACCELERATION;
        this.accelerationY = DEFAULT_ACCELERATION;
        this.speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
        this.maxDropSpeed = DEFAULT_MAX_DROP_SPEED;
        this.boundsConfigured = false;
        this.randomizeXOnReset = false;
        this.resetXConfigured = false;
    }
    
    public GravityMovement(Entity entity, float gravity) {
        super(entity);
        this.gravity = gravity;
        this.accelerationX = DEFAULT_ACCELERATION;
        this.accelerationY = DEFAULT_ACCELERATION;
        this.speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
        this.maxDropSpeed = DEFAULT_MAX_DROP_SPEED;
        this.boundsConfigured = false;
        this.randomizeXOnReset = false;
        this.resetXConfigured = false;
    }
    
    // Updates the movement with gravity and acceleration applied 
    @Override
    public void update(float deltaTime) {
        if (!enabled) {
            return;
        }
        
        // Appliies acceleration to velocity
        velocityX += accelerationX * deltaTime;
        velocityY += accelerationY * deltaTime;
        
        // Applies gravity to Y velocity
        velocityY += gravity * deltaTime;
        
        // Updates entity position
        updateEntityPosition(deltaTime);
    }
    
    // Updates entity's position based on current velocity 
    private void updateEntityPosition(float deltaTime) {
        Entity entity = getEntity();
        if (entity != null) {
            entity.setX(entity.getX() + velocityX * deltaTime);
            entity.setY(entity.getY() + velocityY * deltaTime);
            if (boundsConfigured && entity.getY() <= bottomBoundaryY) {
                entity.setY(resetTopY);
                if (randomizeXOnReset && resetXConfigured) {
                    entity.setX(MathUtils.random(resetMinX, resetMaxX));
                }

                float currentSpeed = Math.abs(velocityY);
                float increasedSpeed = currentSpeed * speedMultiplier;
                float cappedSpeed = Math.min(increasedSpeed, maxDropSpeed);

                if (velocityY < 0f) {
                    velocityY = -cappedSpeed;
                } else if (velocityY > 0f) {
                    velocityY = cappedSpeed;
                }
            }
        }
    }

    // Configures vertical bounds for reset behavior 
    public void setVerticalBounds(float bottomBoundaryY, float resetTopY) {
        if (!Float.isFinite(bottomBoundaryY) || !Float.isFinite(resetTopY)) {
            throw new IllegalArgumentException("Boundary values must be finite numbers");
        }
        if (resetTopY <= bottomBoundaryY) {
            throw new IllegalArgumentException("Reset top Y must be greater than bottom boundary Y");
        }
        this.bottomBoundaryY = bottomBoundaryY;
        this.resetTopY = resetTopY;
        this.boundsConfigured = true;
    }

    // Configures horizontal reset range for randomized spawn positions 
    public void setHorizontalResetRange(float minX, float maxX) {
        if (!Float.isFinite(minX) || !Float.isFinite(maxX)) {
            throw new IllegalArgumentException("Reset range values must be finite numbers");
        }
        if (maxX < minX) {
            throw new IllegalArgumentException("Maximum X must be greater than or equal to minimum X");
        }
        this.resetMinX = minX;
        this.resetMaxX = maxX;
        this.resetXConfigured = true;
        this.randomizeXOnReset = true;
    }

    // Sets maximum drop speed cap 
    public void setMaxDropSpeed(float maxDropSpeed) {
        if (!Float.isFinite(maxDropSpeed)) {
            throw new IllegalArgumentException("Max drop speed must be a finite number");
        }
        if (maxDropSpeed <= 0) {
            throw new IllegalArgumentException("Max drop speed must be positive");
        }
        this.maxDropSpeed = maxDropSpeed;
    }

    // Sets speed multiplier applied on each reset 
    public void setSpeedMultiplier(float speedMultiplier) {
        if (!Float.isFinite(speedMultiplier)) {
            throw new IllegalArgumentException("Speed multiplier must be a finite number");
        }
        if (speedMultiplier < 0) {
            throw new IllegalArgumentException("Speed multiplier cannot be negative");
        }
        this.speedMultiplier = speedMultiplier;
    }

    
    // Sets the acceleration affecting this entity 
    public void setAcceleration(float accelerationX, float accelerationY) {
        if (!Float.isFinite(accelerationX) || !Float.isFinite(accelerationY)) {
            throw new IllegalArgumentException("Acceleration values must be finite numbers");
        }
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }
    
    // Gets acceleration of X component 
    @Override
    public float getAccelerationX() {
        return accelerationX;
    }
    
    // Gets acceleration of Y component 
    @Override
    public float getAccelerationY() {
        return accelerationY;
    }
    
    // Sets gravity value 
    public void setGravity(float gravity) {
        if (!Float.isFinite(gravity)) {
            throw new IllegalArgumentException("Gravity must be a finite number");
        }
        this.gravity = gravity;
    }
    
    // Retrieves current gravity value 
    public float getGravity() {
        return gravity;
    }
}
