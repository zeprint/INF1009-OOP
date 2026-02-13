package io.github.some_example_name.lwjgl3;

/**
 * ICollisionSystem - Contract for collision management (DIP).
 *
 * Callers depend on this interface rather than CollisionManager directly,
 * allowing the collision system to be swapped (e.g. spatial hash vs brute-force)
 * without touching call sites.
 *
 * Implementing class: CollisionManager
 */
public interface ICollisionSystem {

    /* Register a collidable object. */
    void addObject(Collidable obj);

    /* Unregister a collidable object. */
    void removeObject(Collidable obj);

    /* Run collision detection and resolution for all registered objects. */
    void checkCollisions();
}
