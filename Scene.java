package io.github.some_example_name.lwjgl3;

// Abstract Scene class
// Base class for all scenes in the game
public abstract class Scene {
    protected String name;
    protected boolean isPaused;
    protected EntityManager entityManager;

    // Constructor for Scene
    public Scene() {
        this.name = "";
        this.isPaused = false;
        this.entityManager = new EntityManager();
    }

    // Initialize the scene
    // @return true if successful, false otherwise
    public abstract boolean create();

    // Update the scene
    // @param dt Delta time in seconds
    // @return true if successful, false otherwise
    public abstract boolean update(float dt);

    // Render the scene
    // @return true if successful, false otherwise
    public abstract boolean render();

    // Dispose of scene resources
    // @return true if successful, false otherwise
    public abstract boolean dispose();

    // Pause the scene
    // @return true if successful, false otherwise
    public boolean pause() {
        isPaused = true;
        return true;
    }

    // Resume the scene
    // @return true if successful, false otherwise
    public boolean resume() {
        isPaused = false;
        return true;
    }

    // Get the scene name
    // @return Scene name
    public String getName() {
        return name;
    }

    // Set the scene name
    // @param name New scene name
    public void setName(String name) {
        this.name = name;
    }

    // Check if scene is paused
    // @return true if paused, false otherwise
    public boolean isPaused() {
        return isPaused;
    }

    // Get the entity manager
    // @return EntityManager instance
    public EntityManager getEntityManager() {
        return entityManager;
    }
}