package io.github.some_example_name.lwjgl3.logic.movement;

import java.util.function.BooleanSupplier;

/**
 * GravityJumpMovement - Handles vertical jump and gravity for the Character.
 *
 * When the jump input fires and the character is grounded, an upward velocity
 * is applied.  Every frame gravity pulls the character back down.  Landing is
 * detected when the y-position falls to the floor surface.
 *
 * Registered with MovementManager; the manager calls {@link #update(float)}
 * every frame.
 */
public class GravityJumpMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final CoordinateTarget target;
    private final MotionState state;
    private final BooleanSupplier jumpTriggeredSupplier;

    private final float floorY;
    private final float jumpVelocity;
    private final float gravity;

    /**
     * @param target                receives the final position each frame
     * @param state                 shared motion state (position, vertical velocity, grounded)
     * @param jumpTriggeredSupplier returns true on the frame the jump key is pressed
     * @param floorY                y-coordinate of the floor surface
     * @param jumpVelocity          initial upward velocity when jumping (positive)
     * @param gravity               downward acceleration (negative value, e.g. -1200)
     */
    public GravityJumpMovement(CoordinateTarget target,
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

        // ---- Trigger jump if grounded and input fires ----
        if (state.isGrounded() && jumpTriggeredSupplier.getAsBoolean()) {
            state.setVerticalVelocity(jumpVelocity);
            state.setGrounded(false);
        }

        // ---- Apply gravity while airborne ----
        if (!state.isGrounded()) {
            float nextVy = state.getVerticalVelocity() + gravity * deltaTime;
            float nextY  = state.getY() + nextVy * deltaTime;

            // Landing check
            if (nextY <= floorY) {
                nextY  = floorY;
                nextVy = 0f;
                state.setGrounded(true);
            }

            state.setVerticalVelocity(nextVy);
            state.setY(nextY);
        }

        // ---- Propagate to base class and target ----
        setPosition(state.getX(), state.getY());
        target.setX(state.getX());
        target.setY(state.getY());
    }

    public boolean isGrounded() {
        return state.isGrounded();
    }
}
