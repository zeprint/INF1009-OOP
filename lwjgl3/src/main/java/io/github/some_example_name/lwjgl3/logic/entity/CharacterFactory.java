package io.github.some_example_name.lwjgl3;

import io.github.some_example_name.lwjgl3.logic.movement.CoordinateTarget;
import io.github.some_example_name.lwjgl3.logic.movement.DodgeMovement;
import io.github.some_example_name.lwjgl3.logic.movement.HorizontalMovement;
import io.github.some_example_name.lwjgl3.logic.movement.JumpMovement;
import io.github.some_example_name.lwjgl3.logic.movement.MotionState;
import io.github.some_example_name.lwjgl3.logic.movement.StateTarget;

/**
 * CharacterFactory - Creates the player Character and wires up all
 * movement components via GenericFactory.
 *
 * The factory creates the shared {@link MotionState}, instantiates
 * {@link HorizontalMovement}, {@link JumpMovement}, and
 * {@link DodgeMovement}, and registers each with the
 * {@link IMovementSystem} so they are updated every frame.
 *
 * Input is read through the {@link IInputSystem} — the factory captures
 * lambda suppliers that query the input system at update time, keeping
 * the movement classes decoupled from the input layer.
 */
public class CharacterFactory implements GenericFactory<Character> {

    // ---- Physics tunables (moved here from Character) ----
    private static final float JUMP_VELOCITY     =  500f;
    private static final float GRAVITY            = -1200f;
    private static final float HORIZONTAL_SPEED   =  600f;  // pixels / sec

    // ---- Lane setup ----
    private static final float LANE_SPACING = 80f;

    // ---- Dependencies ----
    private final IMovementSystem movementSystem;
    private final IInputSystem    inputSystem;

    /**
     * @param movementSystem the engine's movement manager — components are registered here
     * @param inputSystem    the engine's input manager — used to create input suppliers
     */
    public CharacterFactory(IMovementSystem movementSystem, IInputSystem inputSystem) {
        if (movementSystem == null || inputSystem == null) {
            throw new IllegalArgumentException("movementSystem and inputSystem cannot be null");
        }
        this.movementSystem = movementSystem;
        this.inputSystem    = inputSystem;
    }

    /**
     * Creates a fully wired Character.
     *
     * @param x centre-lane x-coordinate
     * @param y floor surface y-coordinate (character stands on top)
     * @return a new Character with HorizontalMovement, JumpMovement, and DodgeMovement registered
     */
    @Override
    public Character create(float x, float y) {
        Character character = new Character(x, y);

        // Shared mutable state between HorizontalMovement and JumpMovement
        MotionState state = new MotionState(x, y);

        // Horizontal boundary: one lane-width left and right of centre
        float minX = x - LANE_SPACING;
        float maxX = x + LANE_SPACING;

        // --- HorizontalMovement ---
        // DoubleSupplier: reads MOVE_X axis each frame
        HorizontalMovement horizontal = new HorizontalMovement(
                character,                                          // CoordinateTarget
                state,
                () -> (double) inputSystem.getAxis(InputAxis.MOVE_X),  // DoubleSupplier
                HORIZONTAL_SPEED,
                minX,
                maxX
        );

        // --- JumpMovement ---
        // BooleanSupplier: true on the frame JUMP is triggered
        JumpMovement jump = new JumpMovement(
                character,                                          // CoordinateTarget
                state,
                () -> inputSystem.isActionTriggered(InputAction.JUMP),  // BooleanSupplier
                y,               // floorY
                JUMP_VELOCITY,
                GRAVITY
        );

        // --- DodgeMovement ---
        // BooleanSupplier: true while DODGE key is held
        DodgeMovement dodge = new DodgeMovement(
                character,                                          // StateTarget
                () -> inputSystem.isActionHeld(InputAction.DODGE)      // BooleanSupplier
        );

        // Register all three with the movement manager
        movementSystem.registerComponent(horizontal);
        movementSystem.registerComponent(jump);
        movementSystem.registerComponent(dodge);

        return character;
    }
}
