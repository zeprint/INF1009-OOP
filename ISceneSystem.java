package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ISceneSystem {
    void create();

    void update(float dt);

    void render(SpriteBatch batch);

    void resize(int width, int height);

    void dispose();

    void pause();

    void resume();
}