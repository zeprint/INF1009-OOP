package io.github.some_example_name.lwjgl3;

/**
 * GravityMovement - Movement component that simulates gravitational acceleration.
 *
 * DRY: Uses inherited applyVelocity() for position updates.
 * DIP: Uses DistributionType for horizontal randomisation on reset.
 */
public class GravityMovement extends MovementComponent implements Acceleratable {

    protected static final float DEFAULT_GRAVITY          = 9.81f;
    protected static final float DEFAULT_ACCELERATION     = 0f;
    protected static final float DEFAULT_SPEED_MULTIPLIER = 2f;
    protected static final float DEFAULT_MAX_DROP_SPEED   = 10f;

    private float gravity;
    private float accelerationX;
    private float accelerationY;
    private float speedMultiplier;
    private float maxDropSpeed;

    // Vertical bounds (optional)
    private float   bottomBoundaryY;
    private float   resetTopY;
    private boolean boundsConfigured;

    // Horizontal reset randomisation (optional, DIP)
    private DistributionType xDistribution;

    // --- Constructors ---

    public GravityMovement(Entity entity) {
        this(entity, DEFAULT_GRAVITY);
    }

    public GravityMovement(Entity entity, float gravity) {
        super(entity);
        this.gravity          = gravity;
        this.accelerationX    = DEFAULT_ACCELERATION;
        this.accelerationY    = DEFAULT_ACCELERATION;
        this.speedMultiplier  = DEFAULT_SPEED_MULTIPLIER;
        this.maxDropSpeed     = DEFAULT_MAX_DROP_SPEED;
        this.boundsConfigured = false;
        this.xDistribution    = null;
    }

    // --- Per-frame update ---

    @Override
    public void update(float deltaTime) {
        if (!enabled) return;

        velocityX += accelerationX * deltaTime;
        velocityY += accelerationY * deltaTime;
        velocityY += gravity * deltaTime;

        applyVelocity(deltaTime);   // DRY: inherited from MovementComponent
        checkBounds();
    }

    /** Reset entity position and speed when it falls below the bottom boundary. */
    private void checkBounds() {
        Entity entity = getEntity();
        if (entity == null || !boundsConfigured) return;

        if (entity.getY() <= bottomBoundaryY) {
            entity.setY(resetTopY);

            if (xDistribution != null) {
                entity.setX(xDistribution.next());
            }

            float currentSpeed   = Math.abs(velocityY);
            float increasedSpeed = currentSpeed * speedMultiplier;
            float cappedSpeed    = Math.min(increasedSpeed, maxDropSpeed);
            velocityY = (velocityY < 0f) ? -cappedSpeed : cappedSpeed;
        }
    }

    // --- Configuration ---

    public void setVerticalBounds(float bottomBoundaryY, float resetTopY) {
        this.bottomBoundaryY  = bottomBoundaryY;
        this.resetTopY        = resetTopY;
        this.boundsConfigured = true;
    }

    public void setXDistribution(DistributionType distribution) {
        this.xDistribution = distribution;
    }

    public void setHorizontalResetRange(float minX, float maxX) {
        this.xDistribution = new MobileRandom(minX, maxX);
    }

    public void setMaxDropSpeed(float maxDropSpeed)       { this.maxDropSpeed     = maxDropSpeed;     }
    public void setSpeedMultiplier(float speedMultiplier) { this.speedMultiplier  = speedMultiplier;  }
    public void setGravity(float gravity)                 { this.gravity          = gravity;          }

    public void setAcceleration(float ax, float ay) {
        this.accelerationX = ax;
        this.accelerationY = ay;
    }

    // --- Acceleratable ---

    @Override public float getAccelerationX() { return accelerationX; }
    @Override public float getAccelerationY() { return accelerationY; }

    // --- Getters ---

    public float getGravity() { return gravity; }
}
