package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

import io.github.mathdash.AbstractEngine.inputouput.AudioManager;
import io.github.mathdash.AbstractEngine.scene.Scene;
import io.github.mathdash.AbstractEngine.scene.SceneManager;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * MainMenuScene - The main menu with level selection and high scores.
 */
public class MainMenuScene extends Scene {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;
    private static final String ASSET_BASE = "kenney_new-platformer-pack-1.1/";

    private final SceneManager sceneManager;
    private final LevelSelectCallback callback;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private Texture bgTexture;
    private Texture characterTexture;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private AudioManager audioManager;

    public interface LevelSelectCallback {
        void onLevelSelected(int level);
    }

    public MainMenuScene(SceneManager sceneManager, LevelSelectCallback callback) {
        super("mainmenu");
        this.sceneManager = sceneManager;
        this.callback = callback;
    }

    @Override
    protected void onLoad() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "Sprites/Backgrounds/Default/background_color_trees.png"));
        bgTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        characterTexture = new Texture(Gdx.files.internal(ASSET_BASE + "Sprites/Characters/Default/character_green_front.png"));
        characterTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        audioManager = new AudioManager();
        audioManager.loadSound("select", ASSET_BASE + "Sounds/sfx_select.ogg");

        titleFont = FontGenerator.create(48, Color.WHITE);
        buttonFont = FontGenerator.create(22, Color.WHITE);

        createSkin();
        createUI();
    }

    private void createSkin() {
        skin = new Skin();

        // Create button backgrounds using Pixmap
        Pixmap buttonBg = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        buttonBg.setColor(new Color(0.2f, 0.6f, 0.3f, 1f));
        buttonBg.fill();
        buttonBg.setColor(new Color(0.1f, 0.4f, 0.2f, 1f));
        buttonBg.drawRectangle(0, 0, 200, 50);
        skin.add("button-up", new Texture(buttonBg));
        buttonBg.dispose();

        Pixmap buttonOver = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        buttonOver.setColor(new Color(0.3f, 0.8f, 0.4f, 1f));
        buttonOver.fill();
        buttonOver.setColor(new Color(0.1f, 0.5f, 0.2f, 1f));
        buttonOver.drawRectangle(0, 0, 200, 50);
        skin.add("button-over", new Texture(buttonOver));
        buttonOver.dispose();

        Pixmap buttonDown = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        buttonDown.setColor(new Color(0.15f, 0.5f, 0.25f, 1f));
        buttonDown.fill();
        skin.add("button-down", new Texture(buttonDown));
        buttonDown.dispose();

        BitmapFont skinFont = FontGenerator.create(22, Color.WHITE);
        skin.add("default-font", skinFont);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(skin.get("button-up", Texture.class)));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(skin.get("button-over", Texture.class)));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(skin.get("button-down", Texture.class)));
        buttonStyle.font = skinFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        skin.add("default", buttonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skinFont;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        BitmapFont titleSkinFont = FontGenerator.create(42, Color.YELLOW, Color.DARK_GRAY, 2f);
        titleStyle.font = titleSkinFont;
        titleStyle.fontColor = Color.YELLOW;
        skin.add("title", titleStyle);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle();
        BitmapFont subtitleFont = FontGenerator.create(18, Color.BLACK);
        subtitleStyle.font = subtitleFont;
        subtitleStyle.fontColor = Color.BLACK;
        skin.add("subtitle", subtitleStyle);
    }

    private void createUI() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);

        // Title
        Label title = new Label("MathDash", skin, "title");
        root.add(title).padBottom(10).row();

        Label subtitle = new Label("Run, Think, Solve!", skin, "subtitle");
        root.add(subtitle).padBottom(40).row();

        // Level buttons with high scores
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("MathDash");

        String[] levelNames = {
            "Level 1: + -",
            "Level 2: + - x",
            "Level 3: + - x /",
            "Level 4: ( ) + - x /"
        };

        for (int i = 0; i < 4; i++) {
            final int lvl = i + 1;
            int highScore = prefs.getInteger("highscore_level_" + lvl, 0);
            String btnText = levelNames[i];
            String hsText = "Best: " + highScore;

            Table row = new Table();
            TextButton btn = new TextButton(btnText, skin);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioManager.playSound("select");
                    if (callback != null) {
                        callback.onLevelSelected(lvl);
                    }
                }
            });

            Label hsLabel = new Label(hsText, skin, "subtitle");
            row.add(btn).width(300).height(50).padRight(20);
            row.add(hsLabel).width(120);
            root.add(row).padBottom(15).row();
        }

        stage.addActor(root);
    }

    @Override
    public void update(float deltaTime) {
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw background
        batch.draw(bgTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Draw character on the side
        batch.draw(characterTexture, 50, 80, 96, 96);
        batch.draw(characterTexture, WORLD_WIDTH - 146, 80, 96, 96);

        batch.end();

        // Draw UI
        stage.draw();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    protected void onShow() {
        if (stage != null) {
            Gdx.input.setInputProcessor(stage);
            // Refresh high scores
            refreshHighScores();
        }
    }

    private void refreshHighScores() {
        // Rebuild UI to refresh scores
        if (stage != null) {
            stage.clear();
            createUI();
        }
    }

    @Override
    protected void onHide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    protected void onUnload() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        if (characterTexture != null) {
            characterTexture.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (buttonFont != null) {
            buttonFont.dispose();
        }
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}
