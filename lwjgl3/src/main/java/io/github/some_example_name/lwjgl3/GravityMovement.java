package io.github.some_example_name.lwjgl3;

/**
 * GravityMovement - Movement component that simulates gravitational acceleration.
 */
public class GravityMovement extends MovementComponent {

    protected static final float DEFAULT_GRAVITY = 9.81f;
    protected static final float DEFAULT_ACCELERATION = 0f;

    private float gravity;
    private float accelerationX;
    private float accelerationY;

    public GravityMovement(Positionable entity) {
        this(entity, DEFAULT_GRAVITY);
    }

    public GravityMovement(Positionable entity, float gravity) {
        super(entity);
        if (!Float.isFinite(gravity)) {
            throw new IllegalArgumentException("gravity must be finite");
        }
        this.gravity = gravity;
        this.accelerationX = DEFAULT_ACCELERATION;
        this.accelerationY = DEFAULT_ACCELERATION;
    }

    // Per-frame update

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        if (!enabled) {
            return;
        }

        velocityX += accelerationX * deltaTime;
        velocityY += accelerationY * deltaTime;
        velocityY += gravity * deltaTime;

        applyVelocity(deltaTime);
    }

    // Configuration

    public void setGravity(float gravity) {
        if (!Float.isFinite(gravity)) {
            throw new IllegalArgumentException("gravity must be finite");
        }
        this.gravity = gravity;
    }

    public void setAcceleration(float ax, float ay) {
        if (!Float.isFinite(ax) || !Float.isFinite(ay)) {
            throw new IllegalArgumentException("acceleration values must be finite");
        }
        this.accelerationX = ax;
        this.accelerationY = ay;
    }

    public float getAccelerationX() {
        return accelerationX;
    }

    public float getAccelerationY() {
        return accelerationY;
    }

    // Getters
    
    public float getGravity() {
        return gravity;
    }
}
