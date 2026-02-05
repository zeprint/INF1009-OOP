package io.github.some_example_name.lwjgl3;

// Rotation-based movement with angular velocity 
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
    
    // Constructor with initial rotation angle and fixed velocity 
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
        
        // Updates rotation angle based on angular velocity
        rotationAngle += angularVelocity * deltaTime;
        
        // Normalises rotation angle to 0-360 degrees
        normaliseRotationAngle();
        
        // Updates entity's rotation angle representation
        Entity entity = getEntity();
        if (entity != null) {
            entity.setRotationAngle(rotationAngle);
        }
        
        // Applies velocity to position
        updateEntityPosition(deltaTime);
    }
    
    // Updates the entity's position based on current velocity 
    private void updateEntityPosition(float deltaTime) {
        Entity entity = getEntity();
        if (entity != null) {
            entity.setX(entity.getX() + velocityX * deltaTime);
            entity.setY(entity.getY() + velocityY * deltaTime);
        }
    }
    
    // Normalises the rotation angle to range [0, 360) 
    private void normaliseRotationAngle() {
        rotationAngle = rotationAngle % 360f;
        if (rotationAngle < 0) {
            rotationAngle += 360f;
        }
    }
    
    // Sets entity's angular velocity 
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    // Gets current angular velocity 
    @Override
    public float getAngularVelocity() {
        return angularVelocity;
    }
    
    // Sets entity's rotation angle 
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
        normaliseRotationAngle();
    }
    
    // Gets current rotation angle 
    public float getRotationAngle() {
        return rotationAngle;
    }
    
    // Sets velocity for component 
    @Override
    public void setVelocity(float velocityX, float velocityY) {
        super.setVelocity(velocityX, velocityY);
    }
}
