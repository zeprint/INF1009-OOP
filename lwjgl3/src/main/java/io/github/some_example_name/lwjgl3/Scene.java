package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;

/**
* Scene - Abstract base for all scenes in the simulation.
*/
public abstract class Scene {

    private static final String TAG = "Scene";
    protected String name;
    protected boolean isPaused;
    protected IEntitySystem entitySystem;

    public Scene() {
        this.name = "";
        this.isPaused = false;
        this.entitySystem = null; // subclasses set as needed
    }

    // Lifecycle, template methods to be override by subclasses
    public abstract boolean create(); // initialise scene resources, called once when scene is first loaded
    public abstract boolean update(float dt); // update scene logic per frame
    public abstract boolean render(); // render the scene visually per frame
    public abstract boolean dispose(); // clean up scene resources, called once when scene is removed

    // pause scene updates, rendering continues
    public boolean pause() {
        isPaused = true;
        return true;
    }

    // resume scene updates after pause
    public boolean resume() {
        isPaused = false;
        return true;
    }

    // Accessors

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            Gdx.app.error(TAG, "setName rejected null name");
            return;
        }
        this.name = name;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public IEntitySystem getEntitySystem() {
        return entitySystem;
    }
}
