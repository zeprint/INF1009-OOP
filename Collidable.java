import com.badlogic.gdx.math.Rectangle;

public interface Collidable {

    Rectangle getBounds();

    CollisionType getType();

    void onCollision(CollisionResult result);
}
