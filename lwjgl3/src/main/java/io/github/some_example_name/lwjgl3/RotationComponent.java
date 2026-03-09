package io.github.some_example_name.lwjgl3;

/**
 * RotationComponent - Movement component with angular velocity.
 */
public class RotationComponent extends MovementComponent {

    protected static final float DEFAULT_ROTATION = 0f;
    protected static final float DEFAULT_ANGULAR_VELOCITY = 0f;

    private float rotationAngle;
    private float angularVelocity;

    public RotationComponent(Positionable entity) {
        super(entity);
        setRotationAngle(DEFAULT_ROTATION);
        setAngularVelocity(DEFAULT_ANGULAR_VELOCITY);
    }

    public RotationComponent(Positionable entity, float rotationAngle) {
        super(entity);
        setRotationAngle(rotationAngle);
        setAngularVelocity(DEFAULT_ANGULAR_VELOCITY);
    }

    public RotationComponent(Positionable entity, float rotationAngle, float velocityX, float velocityY) {
        super(entity);
        setRotationAngle(rotationAngle);
        setAngularVelocity(DEFAULT_ANGULAR_VELOCITY);
        setVelocity(velocityX, velocityY);
    }

    // Per-frame update

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        if (!enabled) {
            return;
        }

        rotationAngle += angularVelocity * deltaTime;
        normaliseRotationAngle();

        Positionable entity = getEntity();
        if (entity instanceof HasRotation) {
            ((HasRotation) entity).setRotationAngle(rotationAngle);
        }

        applyVelocity(deltaTime);
    }

    private void normaliseRotationAngle() {
        rotationAngle = rotationAngle % 360f;
        if (rotationAngle < 0) rotationAngle += 360f;
    }

    // Angular velocity

    public void  setAngularVelocity(float av) {
        if (!Float.isFinite(av)) {
            throw new IllegalArgumentException("angularVelocity must be finite");
        }
        this.angularVelocity = av;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    // Rotation angle
    
    public void setRotationAngle(float angle) {
        if (!Float.isFinite(angle)) {
            throw new IllegalArgumentException("rotation angle must be finite");
        }
        this.rotationAngle = angle;
        normaliseRotationAngle();
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
}
