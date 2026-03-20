package io.github.some_example_name.lwjgl3.logic.movement;

import java.util.function.BooleanSupplier;

// Handles Dodge Movement to Entity - Input Key is 'Down Arrow'

public class DodgeMovement extends io.github.some_example_name.lwjgl3.MovementComponent {

    private final StateTarget target;
    private final BooleanSupplier dodgeHeldSupplier;

    public DodgeMovement(StateTarget target, BooleanSupplier dodgeHeldSupplier) {
        super();
        if (target == null || dodgeHeldSupplier == null) {
            throw new IllegalArgumentException("target and dodgeHeldSupplier cannot be null");
        }
        this.target = target;
        this.dodgeHeldSupplier = dodgeHeldSupplier;
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);
        // Dodge remains active while the dodge action key is held.
        target.setStateActive(dodgeHeldSupplier.getAsBoolean());
    }
}
