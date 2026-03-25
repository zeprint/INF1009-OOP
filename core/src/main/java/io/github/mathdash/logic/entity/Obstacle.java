package io.github.mathdash.logic.entity;

import com.badlogic.gdx.math.Rectangle;
import io.github.mathdash.engine.collision.Collidable;
import io.github.mathdash.engine.collision.CollisionResult;
import io.github.mathdash.engine.entity.Entity;
import io.github.mathdash.engine.entity.Renderable;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.logic.collision.CollisionHandler;
import io.github.mathdash.logic.movement.ScrollMovement;

/**
 * Obstacle - An obstacle entity that scrolls from right to left.
 * Accepts a pre-built Renderable component for decoupled rendering.
 */
public class Obstacle extends Entity implements Collidable {

    private static final float WIDTH = 48f;
    private static final float HEIGHT = 48f;

    private CollisionHandler collisionHandler;
    private final Rectangle bounds;

    public Obstacle(Renderable template, float x, float y, float scrollSpeed) {
        super();
        addComponent(new Transform(x, y));
        addComponent(new Renderable(template.getTextureRegion(), WIDTH, HEIGHT));
        addComponent(new ScrollMovement(scrollSpeed));
        this.bounds = new Rectangle(x - WIDTH / 2f, y - HEIGHT / 2f, WIDTH, HEIGHT);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Transform transform = getComponent(Transform.class);
        if (transform != null) {
            bounds.setPosition(transform.getX() - WIDTH / 2f, transform.getY() - HEIGHT / 2f);
        }
    }

    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    public void setScrollSpeed(float speed) {
        ScrollMovement sm = getComponent(ScrollMovement.class);
        if (sm != null) sm.setSpeed(speed);
    }

    @Override
    public Rectangle getBounds() { return bounds; }

    @Override
    public void onCollision(CollisionResult result) {
        if (collisionHandler != null) {
            collisionHandler.onObstacleCollision(this, result);
        }
    }

    @Override
    public boolean isCollidable() { return isActive(); }
}
