package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Droplet - Logic Engine entity: a falling water droplet (contextual).
 *
 * Extends TextureObject for texture rendering, implements Collidable
 * so the CollisionManager can detect bucket-droplet collisions.
 * On collision, the droplet resets to the top of the screen.
 *
 * FIX: Caches the Rectangle returned by getBounds() to avoid allocating
 * a new object every frame during collision checks.
 */
public class Droplet extends TextureObject implements Collidable {

    private final CollisionType collisionType;
    private final float         resetY;
    private final Rectangle     bounds;
    private DistributionType    xDistribution;

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
        // Reset to top of screen on collision with bucket
        setY(resetY);
        if (xDistribution != null) {
            setX(xDistribution.next());
        }
    }

    // --- Configuration ---

    /** Set random X distribution for respawn position. */
    public void setXDistribution(DistributionType dist) {
        this.xDistribution = dist;
    }
}
