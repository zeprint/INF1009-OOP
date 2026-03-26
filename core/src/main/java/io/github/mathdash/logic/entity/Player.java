package io.github.mathdash.logic.entity;

import com.badlogic.gdx.math.Rectangle;
import io.github.mathdash.engine.collision.Collidable;
import io.github.mathdash.engine.collision.CollisionResult;
import io.github.mathdash.engine.entity.Entity;
import io.github.mathdash.engine.entity.Renderable;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.logic.Collision.CollisionHandler;
import io.github.mathdash.logic.component.HealthComponent;

/**
 * Player - The player character entity.
 * Accepts pre-built Renderable components for animation frames,
 * keeping the logic layer decoupled from raw LibGDX texture loading.
 * Implements Collidable for collision detection.
 */
public class Player extends Entity implements Collidable {

    private static final float WIDTH = 48f;
    private static final float HEIGHT = 48f;
    private static final int MAX_LIVES = 3;

    private CollisionHandler collisionHandler;
    private final Rectangle bounds;

    private float hitFlashTimer = 0f;
    private static final float HIT_FLASH_DURATION = 0.5f;

    private int currentLane = 0;
    private float targetY;
    private static final float LANE_SWITCH_SPEED = 600f;
    public static final float[] LANE_Y = {100f, 250f, 400f};

    private Renderable walkA, walkB, idle, hit;
    private float animTimer = 0f;

    public Player(Renderable walkA, Renderable walkB, Renderable idle, Renderable hit) {
        super("player");
        this.walkA = walkA;
        this.walkB = walkB;
        this.idle = idle;
        this.hit = hit;

        addComponent(new Transform(120f, LANE_Y[0]));
        addComponent(new Renderable(walkA.getTextureRegion(), WIDTH, HEIGHT));
        addComponent(new HealthComponent(MAX_LIVES));

        this.targetY = LANE_Y[0];
        this.bounds = new Rectangle(120f - WIDTH / 2f, LANE_Y[0] - HEIGHT / 2f, WIDTH, HEIGHT);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Transform transform = getComponent(Transform.class);

        // Smooth lane switching
        float currentY = transform.getY();
        if (Math.abs(currentY - targetY) > 1f) {
            float dir = targetY > currentY ? 1f : -1f;
            float move = dir * LANE_SWITCH_SPEED * deltaTime;
            if (Math.abs(move) > Math.abs(targetY - currentY)) {
                transform.setY(targetY);
            } else {
                transform.translate(0, move);
            }
        } else {
            transform.setY(targetY);
        }

        // Update bounds
        bounds.setPosition(transform.getX() - WIDTH / 2f, transform.getY() - HEIGHT / 2f);

        // Hit flash timer
        if (hitFlashTimer > 0f) {
            hitFlashTimer -= deltaTime;
        }

        // Animation
        animTimer += deltaTime;
        Renderable renderable = getComponent(Renderable.class);
        if (renderable != null) {
            if (hitFlashTimer > 0f && hit != null) {
                renderable.setTextureRegion(hit.getTextureRegion());
            } else {
                boolean frame = ((int)(animTimer * 6f)) % 2 == 0;
                Renderable tex = frame ? walkA : walkB;
                renderable.setTextureRegion(tex.getTextureRegion());
            }

            // Flashing visibility during invincibility
            if (hitFlashTimer > 0f) {
                boolean visible = ((int)(hitFlashTimer * 10f)) % 2 == 0;
                renderable.setVisible(visible);
            } else {
                renderable.setVisible(true);
            }
        }
    }

    public void switchLane(int direction) {
        int newLane = currentLane + direction;
        if (newLane >= 0 && newLane < LANE_Y.length) {
            currentLane = newLane;
            targetY = LANE_Y[currentLane];
        }
    }

    public int getCurrentLane() { return currentLane; }

    public void triggerHitFlash() {
        hitFlashTimer = HIT_FLASH_DURATION;
    }

    public void loseLife() {
        HealthComponent health = getComponent(HealthComponent.class);
        if (health != null) health.loseLife();
    }

    public void gainLife() {
        HealthComponent health = getComponent(HealthComponent.class);
        if (health != null) health.gainLife();
    }

    public int getLives() {
        HealthComponent health = getComponent(HealthComponent.class);
        return health != null ? health.getLives() : 0;
    }

    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    @Override
    public Rectangle getBounds() { return bounds; }

    @Override
    public void onCollision(CollisionResult result) {
        if (collisionHandler != null) {
            collisionHandler.onPlayerCollision(this, result);
        }
    }

    @Override
    public boolean isCollidable() { return isActive(); }

    public float getWidth() { return WIDTH; }
    public float getHeight() { return HEIGHT; }
}
