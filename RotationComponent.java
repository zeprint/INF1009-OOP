package io.github.some_example_name.lwjgl3;

/* Rotation-based movement with angular velocity */
public class RotationComponent extends MovementComponent implements Rotatable {
    protected static final float DEFAULT_ROTATION = 0f;
    protected static final float DEFAULT_ANGULAR_VELOCITY = 0f;
    
    private float rotationAngle;
    private float angularVelocity;
    
    public RotationComponent(Entity entity) {
        super(entity);
        this.rotationAngle = DEFAULT_ROTATION;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
    }
    
    public RotationComponent(Entity entity, float rotationAngle) {
        super(entity);
        this.rotationAngle = rotationAngle;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
    }
    
    /* Constructor with initial rotation angle and fixed velocity */
    public RotationComponent(Entity entity, float rotationAngle, float velocityX, float velocityY) {
        super(entity);
        this.rotationAngle = rotationAngle;
        this.angularVelocity = DEFAULT_ANGULAR_VELOCITY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    @Override
    public void update(float deltaTime) {
        if (!enabled) {
            return;
        }
        
        // Update rotation angle based on angular velocity
        rotationAngle += angularVelocity * deltaTime;
        
        // Normalise rotation angle to 0-360 degrees
        normaliseRotationAngle();
        
        // Update entity's rotation angle representation
        Entity entity = getEntity();
        if (entity != null) {
            entity.setRotationAngle(rotationAngle);
        }
        
        // Apply velocity to position
        updateEntityPosition(deltaTime);
    }
    
    /* Update the entity's position based on current velocity */
    private void updateEntityPosition(float deltaTime) {
        Entity entity = getEntity();
        if (entity != null) {
            entity.setX(entity.getX() + velocityX * deltaTime);
            entity.setY(entity.getY() + velocityY * deltaTime);
        }
    }
    
    /* Normalize the rotation angle to range [0, 360) */
    private void normaliseRotationAngle() {
        rotationAngle = rotationAngle % 360f;
        if (rotationAngle < 0) {
            rotationAngle += 360f;
        }
    }
    
    /* Set entity's angular velocity */
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    /* Get current angular velocity */
    @Override
    public float getAngularVelocity() {
        return angularVelocity;
    }
    
    /* Set entity's rotation angle */
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
        normaliseRotationAngle();
    }
    
    /* Get current rotation angle */
    public float getRotationAngle() {
        return rotationAngle;
    }
    
    /* Set velocity for component */
    @Override
    public void setVelocity(float velocityX, float velocityY) {
        super.setVelocity(velocityX, velocityY);
    }
}
