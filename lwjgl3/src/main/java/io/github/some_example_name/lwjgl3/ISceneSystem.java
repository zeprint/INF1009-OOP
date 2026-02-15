package io.github.some_example_name.lwjgl3;

/**
* Defines the strict interface for scene management for DIP
* Decouples the GameLoop from concrete SceneManager implementation
* Promotes Interface Segregation by exposing only essential lifecycle operations
* Facilitates testing and parallel development through loose coupling
*/
public interface ISceneSystem {

    // register a scene under a unique name
    boolean addScene(String name, Scene scene);

    // Switch to the named scene
    boolean loadScene(String name);

    // @return the currently active scene. may be null if no scene is active or if the active scene was removed
    Scene getCurrentScene();

    // update the current scene
    boolean update(float deltaTime);

    // Render the current scene
    boolean render();

    // Dispose all scenes
    void dispose();
}
