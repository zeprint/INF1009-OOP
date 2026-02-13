package io.github.some_example_name.lwjgl3;

/**
 * InputMovement - Movement component driven by the input system.
 *
 * SRP FIX: Moves entity-input handling out of GameScene and into a proper
 * MovementComponent, so the scene no longer directly manipulates entity
 * position. This also means the bucket's movement is managed by the
 * MovementManager like every other moving entity.
 *
 * DIP: Depends on IInputSystem interface, not InputManager.
 */
public class InputMovement extends MovementComponent {

    private final IInputSystem inputSystem;
    private final InputAxis    axis;
    private final float        speed;

    // Optional screen bounds for clamping
    private boolean boundsConfigured;
    private float   minBound;
    private float   maxBound;

    /**
     * @param entity      The entity to move.
     * @param inputSystem The input system to read axis values from (DIP).
     * @param axis        Which input axis drives this movement.
     * @param speed       Movement speed in pixels per second.
     */
    public InputMovement(Entity entity, IInputSystem inputSystem,
                         InputAxis axis, float speed) {
        super(entity);
        if (inputSystem == null) {
            throw new IllegalArgumentException("inputSystem cannot be null");
        }
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        }
        this.inputSystem     = inputSystem;
        this.axis            = axis;
        this.speed           = speed;
        this.boundsConfigured = false;
    }

    /**
     * Set horizontal bounds for clamping the entity's position.
     *
     * @param min Minimum X position (inclusive).
     * @param max Maximum X position (inclusive).
     */
    public void setBounds(float min, float max) {
        this.minBound         = min;
        this.maxBound         = max;
        this.boundsConfigured = true;
    }

    @Override
    public void update(float deltaTime) {
        if (!enabled || inputSystem == null) return;

        Entity entity = getEntity();
        if (entity == null) return;

        float axisValue = inputSystem.getAxis(axis);
        float dx = axisValue * speed * deltaTime;
        float newX = entity.getX() + dx;

        if (boundsConfigured) {
            newX = Math.max(minBound, Math.min(maxBound, newX));
        }

        entity.setX(newX);
    }

    // --- Accessors ---

    public float getSpeed() { return speed; }
}
