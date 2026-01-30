package io.github.some_example_name.lwjgl3;

/* Abstract base class defining the interface for all movement components */
public abstract class MovementComponent {
    protected static final float DEFAULT_VELOCITY = 0f;
    
    private Entity entity;
    protected float velocityX;
    protected float velocityY;
    protected boolean enabled;
    
    public MovementComponent(Entity entity) {
        this.entity = entity;
        this.velocityX = DEFAULT_VELOCITY;
        this.velocityY = DEFAULT_VELOCITY;
        this.enabled = true;
    }
    
    /* Update movement component logic */
    public abstract void update(float deltaTime);
    
    /* Set the velocity of the entities */
    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    /*  Get velocity of X */
    public float getVelocityX() {
        return velocityX;
    }
    
    /*  Get velocity of Y */
    public float getVelocityY() {
        return velocityY;
    }
    
    public void enable() {
        this.enabled = true;
    }
    
    public void disable() {
        this.enabled = false;
    }
    
    /* Checks if movement component is enabled */
    public boolean isEnabled() {
        return enabled;
    }
    
    /* Get the entity associated with this component */
    public Entity getEntity() {
        return entity;
    }
}
