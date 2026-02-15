package io.github.some_example_name.lwjgl3;

/**
 * MovementComponent - Abstract base for all movement components.
 *
 * Provides velocity state, enable/disable, and a shared applyVelocity()
 * helper so subclasses don't duplicate position-update logic.
 */
public abstract class MovementComponent {

    protected static final float DEFAULT_VELOCITY = 0f;

    private final Entity entity;
    protected float velocityX;
    protected float velocityY;
    protected boolean enabled;

    public MovementComponent(Entity entity) {
        this.entity = entity;
        this.velocityX = DEFAULT_VELOCITY;
        this.velocityY = DEFAULT_VELOCITY;
        this.enabled = true;
    }

    /** Subclasses implement their specific movement logic. */
    public abstract void update(float deltaTime);

    /**
     * Apply current velocity to the attached entity's position.
     * Shared helper extracted from GravityMovement and RotationComponent
     */
    protected void applyVelocity(float deltaTime) {
        Entity e = getEntity();
        if (e == null) return;
        e.setX(e.getX() + velocityX * deltaTime);
        e.setY(e.getY() + velocityY * deltaTime);
    }

    // --- Velocity ---

    public void  setVelocity(float vx, float vy) {
        velocityX = vx; velocityY = vy;
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

    public Entity getEntity() {
        return entity;
    }
}
