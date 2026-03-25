package io.github.mathdash.engine.entity;
import com.badlogic.gdx.math.Vector2;

/**
 * PhysicsBody - Component that stores an entity's velocity and mass for movement.
 */

public class PhysicsBody implements Component {

    private Vector2 velocity;
    private float mass;

    //Creates a PhysicsBody with zero velocity and a mass of 1.
    public PhysicsBody() {
        this(0f, 0f, 1f);
    }

    // Creates a PhysicsBody with the given velocity and mass.
    public PhysicsBody(float vx, float vy, float mass) {
        this.velocity = new Vector2(vx, vy);
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass must be greater than 0.");
        }
        this.mass = mass;
    }

    @Override
    public void init(Entity owner) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {

    }

    // ---- Getters and Setters ----

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float vx, float vy) {
        this.velocity.set(vx, vy);
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass must be greater than 0.");
        }
        this.mass = mass;
    }

    // Applies a force to this body, modifying velocity based on mass.
    public void applyForce(float forceX, float forceY) {
        velocity.add(forceX / mass, forceY / mass);
    }

    // Returns the speed (magnitude of velocity).
    public float getSpeed() {
        return velocity.len();
    }
}