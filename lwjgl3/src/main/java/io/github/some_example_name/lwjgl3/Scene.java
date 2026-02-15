package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;

/** Abstract base class
* Defines the strict lifecycle contract using the Template Method pattern
* Ensures consistent behaviour across all scene implementations.
*/
public abstract class Scene {

    private static final String TAG = "Scene";
    protected String name;
    protected boolean isPaused;
    protected IEntitySystem entitySystem;

    // Constructor with no parameters, all dependencies will be added by subclasses or added by GameMaster
    public Scene() {
        this.name = "";
        this.isPaused = false;
        this.entitySystem = null; // Subclasses to set as needed
    }

    // Lifecycle, template methods to be override by subclasses
    public abstract boolean create(); // Initialise scene resources, called once when scene is first loaded
    public abstract boolean update(float dt); // Update scene logic per frame
    public abstract boolean render(); // Render the scene visually per frame
    public abstract boolean dispose(); // Clean up scene resources, called once when scene is removed

    // Pause scene updates, rendering continues
    public boolean pause() {
        isPaused = true;
        return true;
    }

    // Resume scene updates after pause
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
