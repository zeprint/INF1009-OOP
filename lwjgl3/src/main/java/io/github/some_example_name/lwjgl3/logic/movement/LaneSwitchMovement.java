package io.github.some_example_name.lwjgl3.logic.movement;

import java.util.function.BooleanSupplier;

/**
 * LaneSwitchMovement - Handles discrete left/right lane switching for the Character.
 *
 * Unlike free-form horizontal movement, this operates on a fixed set of lanes
 * (like Subway Surfers).  When a lane-switch input fires, the target lane
 * changes and the character smoothly interpolates towards it each frame.
 *
 * Registered with MovementManager; the manager calls {@link #update(float)}
 * every frame.
 */
public class LaneSwitchMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final CoordinateTarget target;
    private final MotionState state;
    private final BooleanSupplier moveLeftSupplier;
    private final BooleanSupplier moveRightSupplier;

    private final int laneCount;
    private final float laneSpacing;
    private final float laneSwitchSpeed;   // pixels per second towards target lane
    private final float baseLaneX;         // x-coordinate of lane 0 (leftmost)

    private int currentLane;

    // ---- Internal edge-detection state ----
    // Prevents holding a key from firing repeated lane switches.
    private boolean leftWasPressed;
    private boolean rightWasPressed;

    /**
     * @param target           receives the final position each frame
     * @param state            shared motion state (position + grounded flag)
     * @param moveLeftSupplier returns true while the "move left" key is held
     * @param moveRightSupplier returns true while the "move right" key is held
     * @param centreX          x-coordinate of the centre lane
     * @param laneCount        total number of lanes (e.g. 3)
     * @param laneSpacing      distance in pixels between adjacent lane centres
     * @param laneSwitchSpeed  interpolation speed in pixels per second
     */
    public LaneSwitchMovement(CoordinateTarget target,
                              MotionState state,
                              BooleanSupplier moveLeftSupplier,
                              BooleanSupplier moveRightSupplier,
                              float centreX,
                              int laneCount,
                              float laneSpacing,
                              float laneSwitchSpeed) {
        super();
        if (target == null || state == null || moveLeftSupplier == null || moveRightSupplier == null) {
            throw new IllegalArgumentException("Constructor arguments cannot be null");
        }
        if (laneCount < 1) {
            throw new IllegalArgumentException("laneCount must be >= 1");
        }

        this.target = target;
        this.state = state;
        this.moveLeftSupplier = moveLeftSupplier;
        this.moveRightSupplier = moveRightSupplier;

        this.laneCount = laneCount;
        this.laneSpacing = laneSpacing;
        this.laneSwitchSpeed = laneSwitchSpeed;

        // Lane 0 sits (laneCount/2) spacings to the left of centre
        this.baseLaneX = centreX - (laneCount / 2) * laneSpacing;
        this.currentLane = laneCount / 2;   // start in the middle lane

        this.leftWasPressed = false;
        this.rightWasPressed = false;

        setPosition(state.getX(), state.getY());
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        // ---- Edge detection: switch lane only on the frame the key is first pressed ----
        boolean leftNow = moveLeftSupplier.getAsBoolean();
        boolean rightNow = moveRightSupplier.getAsBoolean();

        if (leftNow && !leftWasPressed && currentLane > 0) {
            currentLane--;
        }
        if (rightNow && !rightWasPressed && currentLane < laneCount - 1) {
            currentLane++;
        }

        leftWasPressed = leftNow;
        rightWasPressed = rightNow;

        // ---- Smooth interpolation towards the target lane ----
        float targetX = getLaneX(currentLane);
        float currentX = state.getX();

        if (Math.abs(currentX - targetX) > 1f) {
            float direction = Math.signum(targetX - currentX);
            float nextX = currentX + direction * laneSwitchSpeed * deltaTime;

            // Clamp so we don't overshoot
            if ((direction > 0 && nextX > targetX) || (direction < 0 && nextX < targetX)) {
                nextX = targetX;
            }
            state.setX(nextX);
        } else {
            state.setX(targetX);
        }

        // ---- Propagate to base class and target ----
        setPosition(state.getX(), state.getY());
        target.setX(state.getX());
        target.setY(state.getY());
    }

    /** Returns the world-x of the given lane index. */
    private float getLaneX(int lane) {
        return baseLaneX + lane * laneSpacing;
    }

    public int getCurrentLane() {
        return currentLane;
    }
}
