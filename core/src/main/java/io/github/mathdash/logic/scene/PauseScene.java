package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.AbstractEngine.scene.Scene;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * PauseScene - Displayed when the game is paused.
 * Offers Resume and Main Menu options.
 */
public class PauseScene extends Scene {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final SceneManager sceneManager;
    private final Runnable onMainMenu;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private Texture overlayTexture;

    public PauseScene(SceneManager sceneManager, Runnable onMainMenu) {
        super("pause");
        this.sceneManager = sceneManager;
        this.onMainMenu = onMainMenu;
    }

    @Override
    protected void onLoad() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        // Create semi-transparent overlay
        Pixmap overlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlay.setColor(0, 0, 0, 0.7f);
        overlay.fill();
        overlayTexture = new Texture(overlay);
        overlay.dispose();

        createSkin();
        createUI();
    }

    private void createSkin() {
        skin = new Skin();

        BitmapFont skinFont = FontGenerator.create(24, Color.WHITE);
        skin.add("default-font", skinFont);

        Pixmap btnUp = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        btnUp.setColor(new Color(0.3f, 0.5f, 0.8f, 1f));
        btnUp.fill();
        skin.add("btn-up", new Texture(btnUp));
        btnUp.dispose();

        Pixmap btnOver = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        btnOver.setColor(new Color(0.4f, 0.6f, 0.9f, 1f));
        btnOver.fill();
        skin.add("btn-over", new Texture(btnOver));
        btnOver.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(skin.get("btn-up", Texture.class)));
        style.over = new TextureRegionDrawable(new TextureRegion(skin.get("btn-over", Texture.class)));
        style.font = skinFont;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        skin.add("default", style);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        BitmapFont titleFont = FontGenerator.create(48, Color.WHITE);
        labelStyle.font = titleFont;
        labelStyle.fontColor = Color.WHITE;
        skin.add("title", labelStyle);
    }

    private void createUI() {
        stage = new Stage(viewport);

        Table root = new Table();
        root.setFillParent(true);

        Label title = new Label("PAUSED", skin, "title");
        root.add(title).padBottom(60).row();

        TextButton resumeBtn = new TextButton("Resume", skin);
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("game");
            }
        });
        root.add(resumeBtn).width(250).height(55).padBottom(20).row();

        TextButton menuBtn = new TextButton("Main Menu", skin);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onMainMenu != null) onMainMenu.run();
            }
        });
        root.add(menuBtn).width(250).height(55).row();

        stage.addActor(root);
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            sceneManager.setScene("game");
            return;
        }
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(overlayTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        stage.draw();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    protected void onShow() {
        if (stage != null) Gdx.input.setInputProcessor(stage);
    }

    @Override
    protected void onHide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    protected void onUnload() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
    }
}
