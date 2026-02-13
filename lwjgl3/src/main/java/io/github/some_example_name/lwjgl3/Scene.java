package io.github.some_example_name.lwjgl3;

/**
 * Scene - Abstract base for all scenes in the simulation.
 *
 * SRP FIX: Previously created a default EntityManager in the constructor
 * that SimulationScene immediately overwrote with the shared instance,
 * wasting an allocation. Now entityManager starts null; subclasses that
 * need one either receive it via injection or create their own.
 */
public abstract class Scene {

    protected String name;
    protected boolean isPaused;
    protected IEntitySystem entitySystem;

    public Scene() {
        this.name = "";
        this.isPaused = false;
        this.entitySystem = null;   // subclasses set as needed
    }

    // --- Lifecycle (template methods) ---

    public abstract boolean create();
    public abstract boolean update(float dt);
    public abstract boolean render();
    public abstract boolean dispose();

    public boolean pause() {
        isPaused = true;
        return true;
    }

    public boolean resume() {
        isPaused = false;
        return true;
    }

    // --- Accessors ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public IEntitySystem getEntitySystem() {
        return entitySystem;
    }
}
