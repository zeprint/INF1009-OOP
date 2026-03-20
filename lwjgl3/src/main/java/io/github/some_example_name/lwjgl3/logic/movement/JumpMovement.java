package io.github.some_example_name.lwjgl3.logic.movement;

import java.util.function.BooleanSupplier;

// Handles Jump Movement to Entity - Input Key is 'SPACE'

public class JumpMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final CoordinateTarget target;
    private final MotionState state;
    private final BooleanSupplier jumpTriggeredSupplier;

    private final float floorY;
    private final float jumpVelocity;
    private final float gravity;

    public JumpMovement(CoordinateTarget target,
                        MotionState state,
                        BooleanSupplier jumpTriggeredSupplier,
                        float floorY,
                        float jumpVelocity,
                        float gravity) {
        super();
        if (target == null || state == null || jumpTriggeredSupplier == null) {
            throw new IllegalArgumentException("target/state/jumpTriggeredSupplier cannot be null");
        }

        this.target = target;
        this.state = state;
        this.jumpTriggeredSupplier = jumpTriggeredSupplier;
        this.floorY = floorY;
        this.jumpVelocity = jumpVelocity;
        this.gravity = gravity;

        setPosition(state.getX(), state.getY());
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        if (state.isGrounded() && jumpTriggeredSupplier.getAsBoolean()) {
            state.setVerticalVelocity(jumpVelocity);
            state.setGrounded(false);
        }

        if (!state.isGrounded()) {
            float nextVy = state.getVerticalVelocity() + gravity * deltaTime;
            float nextY = state.getY() + nextVy * deltaTime;

            if (nextY <= floorY) {
                nextY = floorY;
                nextVy = 0f;
                state.setGrounded(true);
            }

            state.setVerticalVelocity(nextVy);
            state.setY(nextY);
        }

        setPosition(state.getX(), state.getY());
        target.setX(state.getX());
        target.setY(state.getY());
    }
}
