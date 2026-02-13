package io.github.some_example_name.lwjgl3;

/**
 * IEntity - Lifecycle contract for any object managed by the engine (ISP / DIP).
 *
 * Defines the three core lifecycle stages that the UML specifies:
 * initialise, per-frame update, and dispose.
 *
 * Implementing class: Entity (with no-op defaults that subclasses override)
 */
public interface IEntity {

    /** One-time setup after construction (e.g. load resources, configure state). */
    void initialize();

    /**
     * Advance this object's internal state by one frame.
     *
     * @param deltaTime Seconds elapsed since the previous frame.
     */
    void update(float deltaTime);

    /** Release native resources (textures, sounds, etc.). */
    void dispose();
}
