package io.github.some_example_name.lwjgl3.logic.movement;

import java.util.function.DoubleSupplier;

// Handles the Left and Right Movement to Entity - Input Keys are 'Left Arrow' and 'Right Arrow' OR 'A' and 'D'

public class HorizontalMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final CoordinateTarget target;
    private final MotionState state;
    private final DoubleSupplier axisSupplier;
    private final float speed;
    private final float minX;
    private final float maxX;

    public HorizontalMovement(CoordinateTarget target,
                              MotionState state,
                              DoubleSupplier axisSupplier,
                              float speed,
                              float minX,
                              float maxX) {
        super();
        if (target == null || state == null || axisSupplier == null) {
            throw new IllegalArgumentException("target/state/axisSupplier cannot be null");
        }

        this.target = target;
        this.state = state;
        this.axisSupplier = axisSupplier;
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;

        setPosition(state.getX(), state.getY());
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        velocityX = (float) axisSupplier.getAsDouble() * speed;
        float nextX = clamp(state.getX() + velocityX * deltaTime, minX, maxX);

        state.setX(nextX);
        setPosition(state.getX(), state.getY());

        target.setX(state.getX());
        target.setY(state.getY());
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
