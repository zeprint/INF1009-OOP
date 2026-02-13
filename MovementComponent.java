package io.github.some_example_name.lwjgl3;

// Abstract base class defining the interface for all movement components 
public abstract class MovementComponent {
    protected static final float DEFAULT_VELOCITY = 0f;
    
    private Entity entity;
    protected float velocityX;
    protected float velocityY;
    protected boolean enabled;
    
    public MovementComponent(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        this.entity = entity;
        this.velocityX = DEFAULT_VELOCITY;
        this.velocityY = DEFAULT_VELOCITY;
        this.enabled = true;
    }
    
    // Updates movement component logic 
    public abstract void update(float deltaTime);
    
    // Sets the velocity of the entities 
    public void setVelocity(float velocityX, float velocityY) {
        if (!Float.isFinite(velocityX) || !Float.isFinite(velocityY)) {
            throw new IllegalArgumentException("Velocity values must be finite numbers");
        }
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    // Gets velocity of X 
    public float getVelocityX() {
        return velocityX;
    }
    
    // Gets velocity of Y 
    public float getVelocityY() {
        return velocityY;
    }
    
    public void enable() {
        this.enabled = true;
    }
    
    public void disable() {
        this.enabled = false;
    }
    
    // Checks if movement component has been enabled 
    public boolean isEnabled() {
        return enabled;
    }
    
    // Gets the entity associated with this component 
    public Entity getEntity() {
        return entity;
    }
}
