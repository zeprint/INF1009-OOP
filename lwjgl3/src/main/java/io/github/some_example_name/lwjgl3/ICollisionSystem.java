package io.github.some_example_name.lwjgl3;

/**
 * ICollisionSystem - Contract for collision management.
 */
public interface ICollisionSystem {

    void addObject(Collidable obj);

    void removeObject(Collidable obj);

    void checkCollisions();
}
