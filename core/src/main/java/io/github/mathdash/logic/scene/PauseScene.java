package io.github.mathdash.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.engine.ServiceLocator;
import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.engine.inputoutput.InputAction;
import io.github.mathdash.engine.inputoutput.InputManager;
import io.github.mathdash.engine.scene.BaseStage;
import io.github.mathdash.engine.scene.Scene;
import io.github.mathdash.engine.scene.SceneManager;
import io.github.mathdash.engine.scene.StageManager;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * PauseScene - Displayed when the game is paused.
 * Offers Resume / Main Menu buttons plus volume slider and mute toggle.
 * Volume/mute state is always synced via ServiceLocator (single source of truth).
 *
 * Uses BaseStage and StageManager for stage lifecycle management.
 * Routes all input through InputManager (Dependency Inversion Principle).
 */
public class PauseScene extends Scene {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final SceneManager sceneManager;
    private final Runnable onMainMenu;

    private OrthographicCamera camera;
    private Viewport viewport;
    private StageManager stageManager;
    private Skin skin;
    private Texture overlayTexture;
    private Texture muteIconTex;
    private Texture unmuteIconTex;
    private FontGenerator fontGenerator;
    private InputManager inputManager;
    private ImageButton muteBtn;
    private Label volumeLabel;
    private Slider volumeSlider;

    public PauseScene(SceneManager sceneManager, Runnable onMainMenu) {
        super("pause");
        this.sceneManager = sceneManager;
        this.onMainMenu = onMainMenu;
    }

    @Override
    protected void onLoad() {
        fontGenerator = new FontGenerator();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        stageManager = new StageManager();

        Pixmap overlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlay.setColor(0, 0, 0, 0.7f);
        overlay.fill();
        overlayTexture = new Texture(overlay);
        overlay.dispose();

        muteIconTex = new Texture(Gdx.files.internal("mute.png"));
        muteIconTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        unmuteIconTex = new Texture(Gdx.files.internal("unmute.png"));
        unmuteIconTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Fetch shared InputManager from ServiceLocator (bootstrapped by GameMaster)
        inputManager = (InputManager) ServiceLocator.getInput();

        createSkin();
    }

    private void createSkin() {
        skin = new Skin();

        BitmapFont skinFont = fontGenerator.create(24, Color.WHITE);
        skin.add("default-font", skinFont);

        addPixmap(skin, "btn-up", new Color(0.3f, 0.5f, 0.8f, 1f));
        addPixmap(skin, "btn-over", new Color(0.4f, 0.6f, 0.9f, 1f));
        addPixmap(skin, "slider-bg", new Color(0.3f, 0.3f, 0.3f, 0.8f));
        addPixmap(skin, "slider-fill", new Color(0.3f, 0.7f, 0.3f, 0.9f));

        Pixmap knob = new Pixmap(14, 20, Pixmap.Format.RGBA8888);
        knob.setColor(new Color(0.4f, 0.8f, 0.4f, 1f));
        knob.fill();
        skin.add("slider-knob", new Texture(knob));
        knob.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = drawable("btn-up");
        style.over = drawable("btn-over");
        style.font = skinFont;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        skin.add("default", style);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = drawable("slider-bg");
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(skin.get("slider-knob", Texture.class)));
        sliderStyle.knobBefore = drawable("slider-fill");
        skin.add("default-horizontal", sliderStyle);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        BitmapFont titleFont = fontGenerator.create(48, Color.WHITE);
        titleLabelStyle.font = titleFont;
        titleLabelStyle.fontColor = Color.WHITE;
        skin.add("title", titleLabelStyle);

        Label.LabelStyle defaultLabel = new Label.LabelStyle();
        defaultLabel.font = skinFont;
        defaultLabel.fontColor = Color.WHITE;
        skin.add("default", defaultLabel);
    }

    private void addPixmap(Skin s, String name, Color c) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        s.add(name, new Texture(p));
        p.dispose();
    }

    private TextureRegionDrawable drawable(String name) {
        return new TextureRegionDrawable(new TextureRegion(skin.get(name, Texture.class)));
    }

    /** Rebuilds the UI to reflect current audio state from ServiceLocator. */
    private void createUI() {
        stageManager.dispose();
        stageManager = new StageManager();

        PauseStage pauseStage = new PauseStage(viewport);
        pauseStage.initialize();
        stageManager.addStage(pauseStage);
    }

    /** Concrete BaseStage subclass that builds the pause menu UI. */
    private class PauseStage extends BaseStage {
        public PauseStage(Viewport viewport) {
            super(viewport);
        }

        @Override
        public void initialize() {
            Table root = new Table();
            root.setFillParent(true);
            root.center();

            Label title = new Label("PAUSED", skin, "title");
            root.add(title).padBottom(40).row();

            TextButton resumeBtn = new TextButton("Resume", skin);
            resumeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    sceneManager.setScene("game");
                }
            });
            root.add(resumeBtn).width(250).height(50).padBottom(15).row();

            TextButton menuBtn = new TextButton("Main Menu", skin);
            menuBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (onMainMenu != null) onMainMenu.run();
                }
            });
            root.add(menuBtn).width(250).height(50).padBottom(30).row();

            // Audio controls
            Table audioRow = new Table();

            IAudioSystem audio = ServiceLocator.getAudio();
            int vol = audio != null ? Math.max(1, Math.round(audio.getVolume() * 10f)) : 7;
            boolean muted = audio != null && audio.isMuted();

            volumeLabel = new Label("Vol: " + vol, skin);
            audioRow.add(volumeLabel).padRight(8);

            volumeSlider = new Slider(1, 10, 1, false, skin);
            volumeSlider.setValue(vol);
            volumeSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    int newVol = (int) ((Slider) actor).getValue();
                    volumeLabel.setText("Vol: " + newVol);
                    IAudioSystem a = ServiceLocator.getAudio();
                    if (a != null) a.setVolume(newVol / 10f);
                }
            });
            audioRow.add(volumeSlider).width(120).height(30).padRight(15);

            TextureRegionDrawable icon = new TextureRegionDrawable(
                new TextureRegion(muted ? muteIconTex : unmuteIconTex)
            );
            ImageButton.ImageButtonStyle muteBtnStyle = new ImageButton.ImageButtonStyle();
            muteBtnStyle.imageUp = icon;
            muteBtn = new ImageButton(muteBtnStyle);
            muteBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    toggleMute();
                }
            });
            audioRow.add(muteBtn).size(36, 36);

            root.add(audioRow).padBottom(20).row();
            getStage().addActor(root);
        }
    }

    private void toggleMute() {
        IAudioSystem audio = ServiceLocator.getAudio();
        if (audio == null) return;
        audio.setMuted(!audio.isMuted());
        updateMuteIcon();
    }

    private void updateMuteIcon() {
        IAudioSystem audio = ServiceLocator.getAudio();
        if (audio == null || muteBtn == null) return;
        TextureRegionDrawable icon = new TextureRegionDrawable(
            new TextureRegion(audio.isMuted() ? muteIconTex : unmuteIconTex)
        );
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = icon;
        muteBtn.setStyle(style);
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update();

        // Resume via InputManager
        if (inputManager.isActionTriggered(InputAction.TOGGLE_PAUSE)) {
            sceneManager.setScene("game");
            return;
        }
        // Mute toggle via InputManager
        if (inputManager.isActionTriggered(InputAction.TOGGLE_MUTE)) {
            toggleMute();
        }

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
        if (muteIconTex != null) muteIconTex.dispose();
        if (unmuteIconTex != null) unmuteIconTex.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
    }
}