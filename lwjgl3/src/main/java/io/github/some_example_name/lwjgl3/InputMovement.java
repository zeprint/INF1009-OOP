package io.github.some_example_name.lwjgl3;

/**
 * InputMovement - Movement component driven by the input system.
 *
 * Moves input-driven translation into a reusable movement component so scenes
 * do not directly manipulate entity position.
 */
public class InputMovement extends MovementComponent {

    private final IInputSystem inputSystem;
    private final InputAxis axis;
    private final float speed;

    // Optional screen bounds for clamping
    private boolean boundsConfigured;
    private float minBound;
    private float maxBound;

    public InputMovement(Positionable entity, IInputSystem inputSystem,
                         InputAxis axis, float speed) {
        super(entity);
        if (inputSystem == null) {
            throw new IllegalArgumentException("inputSystem cannot be null");
        }
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        }
        if (!Float.isFinite(speed) || speed < 0f) {
            throw new IllegalArgumentException("speed must be a finite, non-negative value");
        }
        this.inputSystem = inputSystem;
        this.axis = axis;
        this.speed = speed;
        this.boundsConfigured = false;
    }

    /**
     * Set bounds for the selected movement axis.
     */
    public void setBounds(float min, float max) {
        if (!Float.isFinite(min) || !Float.isFinite(max)) {
            throw new IllegalArgumentException("bounds must be finite");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.minBound = min;
        this.maxBound = max;
        this.boundsConfigured = true;
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        if (!enabled) {
            return;
        }

        Positionable entity = getEntity();

        float axisValue = inputSystem.getAxis(axis);
        float delta = axisValue * speed * deltaTime;

        if (axis == InputAxis.MOVE_X) {
            float newX = entity.getX() + delta;
            if (boundsConfigured) {
                newX = Math.max(minBound, Math.min(maxBound, newX));
            }
            entity.setX(newX);
            return;
        }

        if (axis == InputAxis.MOVE_Y) {
            float newY = entity.getY() + delta;
            if (boundsConfigured) {
                newY = Math.max(minBound, Math.min(maxBound, newY));
            }
            entity.setY(newY);
        }
    }

    public float getSpeed() {
        return speed;
    }
}
