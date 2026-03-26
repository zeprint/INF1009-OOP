package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.engine.ServiceLocator;
import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.engine.scene.BaseStage;
import io.github.mathdash.engine.scene.Scene;
import io.github.mathdash.engine.scene.SceneManager;
import io.github.mathdash.engine.scene.StageManager;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * DeathScene - Displayed when the player dies.
 * Shows final score and offers Try Again and Main Menu buttons.
 * Uses ServiceLocator for audio (Dependency Inversion Principle).
 * Uses BaseStage and StageManager for stage lifecycle management.
 */
public class DeathScene extends Scene {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final SceneManager sceneManager;
    private final Runnable onTryAgain;
    private final Runnable onMainMenu;

    private OrthographicCamera camera;
    private Viewport viewport;
    private StageManager stageManager;
    private Skin skin;
    private Texture overlayTexture;
    private FontGenerator fontGenerator;
    private int finalScore = 0;
    private int level = 1;

    public DeathScene(SceneManager sceneManager, Runnable onTryAgain, Runnable onMainMenu) {
        super("death");
        this.sceneManager = sceneManager;
        this.onTryAgain = onTryAgain;
        this.onMainMenu = onMainMenu;
    }

    public void setFinalScore(int score) { this.finalScore = score; }
    public void setLevel(int level) { this.level = level; }

    @Override
    protected void onLoad() {
        fontGenerator = new FontGenerator();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        stageManager = new StageManager();

        Pixmap overlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlay.setColor(0.15f, 0, 0, 0.85f);
        overlay.fill();
        overlayTexture = new Texture(overlay);
        overlay.dispose();

        createSkin();
    }

    private void createSkin() {
        skin = new Skin();

        BitmapFont skinFont = fontGenerator.create(24, Color.WHITE);
        skin.add("default-font", skinFont);

        Pixmap btnUp = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        btnUp.setColor(new Color(0.7f, 0.2f, 0.2f, 1f));
        btnUp.fill();
        skin.add("btn-up", new Texture(btnUp));
        btnUp.dispose();

        Pixmap btnOver = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        btnOver.setColor(new Color(0.9f, 0.3f, 0.3f, 1f));
        btnOver.fill();
        skin.add("btn-over", new Texture(btnOver));
        btnOver.dispose();

        Pixmap tryBtnUp = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        tryBtnUp.setColor(new Color(0.2f, 0.6f, 0.3f, 1f));
        tryBtnUp.fill();
        skin.add("try-btn-up", new Texture(tryBtnUp));
        tryBtnUp.dispose();

        Pixmap tryBtnOver = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        tryBtnOver.setColor(new Color(0.3f, 0.8f, 0.4f, 1f));
        tryBtnOver.fill();
        skin.add("try-btn-over", new Texture(tryBtnOver));
        tryBtnOver.dispose();

        TextButton.TextButtonStyle tryStyle = new TextButton.TextButtonStyle();
        tryStyle.up = new TextureRegionDrawable(new TextureRegion(skin.get("try-btn-up", Texture.class)));
        tryStyle.over = new TextureRegionDrawable(new TextureRegion(skin.get("try-btn-over", Texture.class)));
        tryStyle.font = skinFont;
        tryStyle.fontColor = Color.WHITE;
        tryStyle.overFontColor = Color.YELLOW;
        skin.add("try-again", tryStyle);

        TextButton.TextButtonStyle menuStyle = new TextButton.TextButtonStyle();
        menuStyle.up = new TextureRegionDrawable(new TextureRegion(skin.get("btn-up", Texture.class)));
        menuStyle.over = new TextureRegionDrawable(new TextureRegion(skin.get("btn-over", Texture.class)));
        menuStyle.font = skinFont;
        menuStyle.fontColor = Color.WHITE;
        menuStyle.overFontColor = Color.YELLOW;
        skin.add("default", menuStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        BitmapFont titleFont = fontGenerator.create(48, Color.RED, Color.DARK_GRAY, 2f);
        titleStyle.font = titleFont;
        titleStyle.fontColor = Color.RED;
        skin.add("title", titleStyle);

        Label.LabelStyle scoreStyle = new Label.LabelStyle();
        BitmapFont scoreFont = fontGenerator.create(32, Color.WHITE);
        scoreStyle.font = scoreFont;
        scoreStyle.fontColor = Color.WHITE;
        skin.add("score", scoreStyle);

        Label.LabelStyle infoStyle = new Label.LabelStyle();
        infoStyle.font = skinFont;
        infoStyle.fontColor = new Color(0.8f, 0.8f, 0.8f, 1f);
        skin.add("info", infoStyle);
    }

    private void createUI() {
        stageManager.dispose();
        stageManager = new StageManager();

        DeathStage deathStage = new DeathStage(viewport);
        deathStage.initialize();
        stageManager.addStage(deathStage);
    }

    /** Concrete BaseStage subclass that builds the death screen UI. */
    private class DeathStage extends BaseStage {
        public DeathStage(Viewport viewport) {
            super(viewport);
        }

        @Override
        public void initialize() {
            Table root = new Table();
            root.setFillParent(true);

            Label title = new Label("GAME OVER", skin, "title");
            root.add(title).padBottom(30).row();

            Label scoreLabel = new Label("Score: " + finalScore, skin, "score");
            root.add(scoreLabel).padBottom(10).row();

            com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("MathDash");
            int highScore = prefs.getInteger("highscore_level_" + level, 0);
            Label highScoreLabel = new Label("Best: " + highScore, skin, "info");
            root.add(highScoreLabel).padBottom(40).row();

            TextButton tryAgainBtn = new TextButton("Try Again", skin, "try-again");
            tryAgainBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (onTryAgain != null) onTryAgain.run();
                }
            });
            root.add(tryAgainBtn).width(250).height(55).padBottom(20).row();

            TextButton menuBtn = new TextButton("Main Menu", skin);
            menuBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (onMainMenu != null) onMainMenu.run();
                }
            });
            root.add(menuBtn).width(250).height(55).row();

            getStage().addActor(root);
        }
    }

    @Override
    public void update(float deltaTime) {
        stageManager.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(overlayTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();
        stageManager.render();
    }

    @Override
    protected void onResize(int width, int height) {
        viewport.update(width, height, true);
        stageManager.resize(width, height);
    }

    @Override
    protected void onShow() {
        IAudioSystem audio = ServiceLocator.getAudio();
        if (audio != null) audio.playSound("death");
        createUI();
        if (stageManager.getStageCount() > 0) {
            Gdx.input.setInputProcessor(stageManager.getStages().get(0).getStage());
        }
    }

    @Override
    protected void onHide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    protected void onUnload() {
        stageManager.dispose();
        if (skin != null) skin.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
    }
}
