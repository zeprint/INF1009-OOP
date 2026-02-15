package io.github.some_example_name.lwjgl3;

/**
* ISceneSystem - Contract for scene management (DIP).
*
* Callers depend on this interface rather than SceneManager directly,
* allowing the scene system to be swapped without touching call sites.
*
* Implementing class: SceneManager
*/
public interface ISceneSystem {

    /* Register a scene under a unique name. */
    boolean addScene(String name, Scene scene);

    /* Switch to the named scene. */
    boolean loadScene(String name);

    /** @return the currently active scene. */
    Scene getCurrentScene();

    /* Update the current scene. */
    boolean update(float deltaTime);

    /* Render the current scene. */
    boolean render();

    /* Dispose all scenes. */
    void dispose();
}
