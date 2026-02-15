package io.github.some_example_name.lwjgl3;

/**
 * IEntity - Lifecycle contract for any object managed by the engine.
 */
public interface IEntity {

    boolean initialize();

    boolean update(float deltaTime);

    boolean dispose();
}
