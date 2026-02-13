package io.github.some_example_name.lwjgl3;

/**
 * RotationComponent - Movement component with angular velocity.
 *
 * LSP: Uses HasRotation interface check instead of unsafe cast.
 * DRY: Uses inherited applyVelocity() for position updates.
 */
public class RotationComponent extends MovementComponent implements Rotatable {

    protected static final float DEFAULT_ROTATION         = 0f;
    protected static final float DEFAULT_ANGULAR_VELOCITY = 0f;

    private float rotationAngle;
    private float angularVelocity;

    public RotationComponent(Entity entity) {
        super(entity);
        this.rotationAngle   = DEFAULT_ROTATION;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
    }

    public RotationComponent(Entity entity, float rotationAngle) {
        super(entity);
        this.rotationAngle   = rotationAngle;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
    }

    public RotationComponent(Entity entity, float rotationAngle, float velocityX, float velocityY) {
        super(entity);
        this.rotationAngle   = rotationAngle;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
        this.velocityX       = velocityX;
        this.velocityY       = velocityY;
    }

    // --- Per-frame update ---

    @Override
    public void update(float deltaTime) {
        if (!enabled) return;

        rotationAngle += angularVelocity * deltaTime;
        normaliseRotationAngle();

        // LSP: check interface instead of unsafe cast to RotatingShape
        Entity entity = getEntity();
        if (entity instanceof HasRotation) {
            ((HasRotation) entity).setRotationAngle(rotationAngle);
        }

        applyVelocity(deltaTime);   // DRY: inherited from MovementComponent
    }

    private void normaliseRotationAngle() {
        rotationAngle = rotationAngle % 360f;
        if (rotationAngle < 0) rotationAngle += 360f;
    }

    // --- Angular velocity ---

    public void  setAngularVelocity(float av) { this.angularVelocity = av; }
    @Override
    public float getAngularVelocity()         { return angularVelocity; }

    // --- Rotation angle ---

    public void setRotationAngle(float angle) {
        this.rotationAngle = angle;
        normaliseRotationAngle();
    }

    public float getRotationAngle() { return rotationAngle; }
}
