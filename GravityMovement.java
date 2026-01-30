package io.github.some_example_name.lwjgl3;

/* Drop speed with acceleration and speed reset */
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
    
    public GravityMovement(Entity entity) {
        super(entity);
        this.gravity = DEFAULT_GRAVITY;
        this.accelerationX = DEFAULT_ACCELERATION;
        this.accelerationY = DEFAULT_ACCELERATION;
        this.speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
        this.maxDropSpeed = DEFAULT_MAX_DROP_SPEED;
        this.boundsConfigured = false;
    }
    
    public GravityMovement(Entity entity, float gravity) {
        super(entity);
        this.gravity = gravity;
        this.accelerationX = DEFAULT_ACCELERATION;
        this.accelerationY = DEFAULT_ACCELERATION;
        this.speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
        this.maxDropSpeed = DEFAULT_MAX_DROP_SPEED;
        this.boundsConfigured = false;
    }
    
    /* Update the movement with gravity and acceleration applied */
    @Override
    public void update(float deltaTime) {
        if (!enabled) {
            return;
        }
        
        // Apply acceleration to velocity
        velocityX += accelerationX * deltaTime;
        velocityY += accelerationY * deltaTime;
        
        // Apply gravity to Y velocity
        velocityY += gravity * deltaTime;
        
        // Update entity position
        updateEntityPosition(deltaTime);
    }
    
    /* Update entity's position based on current velocity */
    private void updateEntityPosition(float deltaTime) {
        Entity entity = getEntity();
        if (entity != null) {
            entity.setX(entity.getX() + velocityX * deltaTime);
            entity.setY(entity.getY() + velocityY * deltaTime);
            if (boundsConfigured && entity.getY() <= bottomBoundaryY) {
                entity.setY(resetTopY);

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

    /* Configure vertical bounds for reset behavior */
    public void setVerticalBounds(float bottomBoundaryY, float resetTopY) {
        this.bottomBoundaryY = bottomBoundaryY;
        this.resetTopY = resetTopY;
        this.boundsConfigured = true;
    }

    /* Set maximum drop speed cap */
    public void setMaxDropSpeed(float maxDropSpeed) {
        this.maxDropSpeed = maxDropSpeed;
    }

    /* Set speed multiplier applied on each reset */
    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    
    /* Set the acceleration affecting this entity */
    public void setAcceleration(float accelerationX, float accelerationY) {
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }
    
    /* Get acceleration of X component */
    @Override
    public float getAccelerationX() {
        return accelerationX;
    }
    
    /* Get acceleration of X component */
    @Override
    public float getAccelerationY() {
        return accelerationY;
    }
    
    /* Set gravity value */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    /* Retrieve current gravity value */
    public float getGravity() {
        return gravity;
    }
}
