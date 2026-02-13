package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Droplet - Logic Engine entity: a falling water droplet (contextual).
 *
 * Extends TextureObject for texture rendering, implements Collidable
 * so the CollisionManager can detect collisions with other entities.
 *
 * Collision behaviour:
 *  - Bucket collision  → reset to top of screen (caught)
 *  - Any other entity  → bounce off (reverse fall velocity)
 *
 * FIX: Caches the Rectangle returned by getBounds() to avoid allocating
 * a new object every frame during collision checks.
 */
public class Droplet extends TextureObject implements Collidable {

    private final CollisionType collisionType;
    private final float         resetY;
    private final Rectangle     bounds;
    private DistributionType    xDistribution;

    /** Reference to the GravityMovement driving this droplet (for bounce). */
    private GravityMovement     gravityMovement;

    public Droplet(Texture texture, float x, float y, int width, int height, float resetY) {
        super(texture, x, y, height, width);
        this.resetY        = resetY;
        this.collisionType = new CollisionType("droplet", false, true);
        this.bounds        = new Rectangle(x, y, width, height);
    }

    // --- Collidable ---

    @Override
    public Rectangle getBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
        return bounds;
    }

    @Override
    public CollisionType getType() { return collisionType; }

    @Override
    public void onCollision(CollisionResult result) {
        Collidable other = result.getOther();

        // Ignore droplet-droplet collisions
        if (other != null && other.getType() != null
                && "droplet".equals(other.getType().getName())) {
            return;
        }

        // Bucket collision: reset to top of screen (droplet is "caught")
        if (other != null && other.getType() != null
                && "bucket".equals(other.getType().getName())) {
            setY(resetY);
            if (xDistribution != null) {
                setX(xDistribution.next());
            }
            // Reset velocity to a gentle fall speed after being caught
            if (gravityMovement != null) {
                gravityMovement.setVelocity(0f, -150f);
            }
            return;
        }

        // Non-bucket collision: bounce off by reversing Y velocity
        if (gravityMovement != null) {
            float vx = gravityMovement.getVelocityX();
            float vy = gravityMovement.getVelocityY();

            // Reverse Y direction; ensure minimum bounce speed
            float newVy = -vy;
            if (Math.abs(newVy) < 50f) {
                newVy = (newVy >= 0f) ? 50f : -50f;
            }
            gravityMovement.setVelocity(vx, newVy);

            // Separate from the other entity to prevent repeated collision
            CollisionDirection dir = result.getDirection();
            if (dir == CollisionDirection.TOP) {
                setY(getY() + result.getOverlapY() + 1f);
            } else if (dir == CollisionDirection.BOTTOM) {
                setY(getY() - result.getOverlapY() - 1f);
            } else if (dir == CollisionDirection.LEFT) {
                setX(getX() - result.getOverlapX() - 1f);
                gravityMovement.setVelocity(-Math.abs(vx) - 30f, newVy);
            } else if (dir == CollisionDirection.RIGHT) {
                setX(getX() + result.getOverlapX() + 1f);
                gravityMovement.setVelocity(Math.abs(vx) + 30f, newVy);
            }
        }
    }

    // --- Configuration ---

    /** Set random X distribution for respawn position. */
    public void setXDistribution(DistributionType dist) {
        this.xDistribution = dist;
    }

    /** Set the GravityMovement component so the droplet can reverse velocity on bounce. */
    public void setGravityMovement(GravityMovement gm) {
        this.gravityMovement = gm;
    }

    /** @return the GravityMovement component driving this droplet, or null. */
    public GravityMovement getGravityMovement() {
        return gravityMovement;
    }
}
