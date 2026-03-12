package io.github.some_example_name.lwjgl3;

/**
 * MovementComponent - Abstract base for all movement components.
 */
public abstract class MovementComponent {

    protected static final float DEFAULT_VELOCITY = 0f;

    private final Positionable entity;
    protected float velocityX;
    protected float velocityY;
    protected boolean enabled;

    public MovementComponent(Positionable entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }
        this.entity = entity;
        this.velocityX = DEFAULT_VELOCITY;
        this.velocityY = DEFAULT_VELOCITY;
        this.enabled = true;
    }

    /** Subclasses implement their specific movement logic. */
    public abstract void update(float deltaTime);

    protected void applyVelocity(float deltaTime) {
        Positionable e = getEntity();
        e.setX(e.getX() + velocityX * deltaTime);
        e.setY(e.getY() + velocityY * deltaTime);
    }

    /** Shared guard to keep all movement updates consistent. */
    protected final void validateDeltaTime(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("deltaTime must be a finite, non-negative value");
        }
    }

    // --- Velocity ---

    public void  setVelocity(float vx, float vy) {
        if (!Float.isFinite(vx) || !Float.isFinite(vy)) {
            throw new IllegalArgumentException("velocity values must be finite");
        }
        velocityX = vx;
        velocityY = vy;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    // --- Enable / Disable ---

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // --- Entity reference ---

    public Positionable getEntity() {
        return entity;
    }
}
