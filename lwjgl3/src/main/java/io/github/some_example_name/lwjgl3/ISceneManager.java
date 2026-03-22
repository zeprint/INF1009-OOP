package io.github.some_example_name.lwjgl3;

/**
 * ISceneManager - Contract for the engine's scene management system.
 *
 * Concrete scenes depend on this interface (DIP) so they can trigger
 * scene transitions without coupling to the SceneManager implementation.
 *
 * Example: GameScene calls sceneManager.loadScene("PauseScene") when
 * the player presses ESC, without knowing how SceneManager stores or
 * caches scenes internally.
 */
public interface ISceneManager {

    /**
     * Registers a named scene. The scene is not created until first loaded.
     *
     * @param name  unique identifier for the scene
     * @param scene the Scene instance to register
     * @return true if registration succeeded
     */
    boolean addScene(String name, Scene scene);

    /**
     * Unregisters and disposes a scene by name.
     *
     * @param name the scene to remove
     * @return true if removal succeeded
     */
    boolean removeScene(String name);

    /**
     * Switches to the named scene. Pauses the current scene, creates
     * the target scene on first load (lazy initialisation), then resumes it.
     *
     * @param name the scene to switch to
     * @return true if the switch succeeded
     */
    boolean loadScene(String name);

    /**
     * Returns the currently active scene, or null if none.
     */
    Scene getCurrentScene();

    /**
     * Returns true if a scene with the given name is registered.
     */
    boolean hasScene(String name);

    /**
     * Updates the current scene. Called once per frame by GameMaster.
     *
     * @param deltaTime seconds since last frame
     * @return true if the update succeeded
     */
    boolean update(float deltaTime);

    /**
     * Renders the current scene. Called once per frame by GameMaster.
     *
     * @return true if the render succeeded
     */
    boolean render();

    /**
     * Returns the scene registered under the given name, or null if not found.
     * Used for direct scene-to-scene communication (e.g. passing score to EndScene).
     */
    Scene getScene(String name);

    /**
     * Disposes all registered scenes and clears internal state.
     */
    void dispose();
}
