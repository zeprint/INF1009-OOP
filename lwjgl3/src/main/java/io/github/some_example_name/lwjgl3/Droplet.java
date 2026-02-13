package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
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
 */
public class Droplet extends TextureObject implements Collidable {

    private final CollisionType collisionType;
    private final float resetY;
    private final Rectangle bounds;
    private DistributionType xDistribution;

    /** Reference to the GravityMovement driving this droplet (for bounce). */
    private GravityMovement gravityMovement;

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
            // Reset velocity to a gentle fall speed; gravity will accelerate naturally
            if (gravityMovement != null) {
                gravityMovement.setVelocity(0f, -60f);
            }
            return;
        }

        // Non-bucket collision: bounce off with energy damping.
        // Gravity (configured in GravityMovement) naturally decelerates the
        // upward bounce and pulls the droplet back down to continue falling.
        if (gravityMovement != null) {
            float vx = gravityMovement.getVelocityX();
            float vy = gravityMovement.getVelocityY();

            // Energy damping — lose speed on each bounce (realistic)
            float damping = 0.65f;

            // Random horizontal kick for a natural-looking arc to one side
            float kickX = MathUtils.random(-80f, 80f);

            // Separate from the other entity to prevent repeated collision
            CollisionDirection dir = result.getDirection();
            if (dir == CollisionDirection.TOP) {
                setY(getY() + result.getOverlapY() + 1f);
                gravityMovement.setVelocity(vx + kickX, -vy * damping);
            } else if (dir == CollisionDirection.BOTTOM) {
                setY(getY() - result.getOverlapY() - 1f);
                gravityMovement.setVelocity(vx + kickX, -vy * damping);
            } else if (dir == CollisionDirection.LEFT) {
                setX(getX() - result.getOverlapX() - 1f);
                gravityMovement.setVelocity(-Math.abs(vx) * damping - 30f, vy);
            } else if (dir == CollisionDirection.RIGHT) {
                setX(getX() + result.getOverlapX() + 1f);
                gravityMovement.setVelocity(Math.abs(vx) * damping + 30f, vy);
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
