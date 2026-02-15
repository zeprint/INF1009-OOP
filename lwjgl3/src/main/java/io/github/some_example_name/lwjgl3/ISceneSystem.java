package io.github.some_example_name.lwjgl3;

/**
* Defines the strict interface for scene management
* Decouples the GameLoop from concrete SceneManager implementation
* Facilitates testing and parallel development through loose coupling
*/
public interface ISceneSystem {

    boolean addScene(String name, Scene scene);

    boolean loadScene(String name);

    Scene getCurrentScene();

    boolean update(float deltaTime);

    boolean render();

    void dispose();
}
