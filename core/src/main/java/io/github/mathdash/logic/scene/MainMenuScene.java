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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.mathdash.engine.ServiceLocator;
import io.github.mathdash.engine.inputoutput.IAudioSystem;
import io.github.mathdash.engine.scene.Scene;
import io.github.mathdash.engine.scene.SceneManager;
import io.github.mathdash.logic.util.FontGenerator;

/**
 * MainMenuScene - Main menu with level select, volume/mute controls,
 * and a paginated kid-friendly rulebook.
 * Features a continuously scrolling parallax background.
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
    private Texture muteIconTex;
    private Texture unmuteIconTex;

    private float bgScrollX = 0f;
    private static final float BG_SCROLL_SPEED = 40f;

    // Audio state (always read from ServiceLocator for sync)
    private ImageButton muteBtn;
    private Label volumeLabel;
    private Window rulebookWindow;

    // Rulebook pagination
    private int currentPage = 0;
    private static final String[][] RULEBOOK_PAGES = {
        { // Page 1: Welcome
            "Welcome to MathDash!",
            "You are a runner! Your job is\n" +
            "to solve maths while running.\n\n" +
            "The faster you solve, the more\n" +
            "powerful you become!\n\n" +
            "Don't worry - it's fun and easy!\n" +
            "Let's learn how to play!"
        },
        { // Page 2: Controls
            "Controls",
            "KEYBOARD:\n" +
            "  UP arrow / W  =  Move up\n" +
            "  DOWN arrow / S  =  Move down\n" +
            "  ESC / P  =  Pause the game\n" +
            "  M  =  Mute / Unmute sound\n\n" +
            "MOUSE:\n" +
            "  Click buttons in menus\n" +
            "  Drag the volume slider"
        },
        { // Page 3: How to score
            "How to Score",
            "A maths question shows at the\n" +
            "top of the screen.\n\n" +
            "3 answer blocks come towards\n" +
            "you - one is CORRECT!\n\n" +
            "Move to the lane with the\n" +
            "CORRECT answer to score!\n\n" +
            "Wrong answers lose a life!"
        },
        { // Page 4: Dangers
            "Watch Out!",
            "Saws, spikes and slimes will\n" +
            "try to hit you!\n\n" +
            "If you get hit, you lose a life.\n" +
            "You have 3 lives total.\n\n" +
            "Lose all 3 lives = Game Over!\n\n" +
            "Be careful and dodge them!"
        },
        { // Page 5: Surge Power
            "Surge Power!",
            "Get answers RIGHT in a row to\n" +
            "fill your Surge meter!\n\n" +
            "When the meter is FULL, you\n" +
            "become INVINCIBLE!\n\n" +
            "You run super fast and smash\n" +
            "through everything!\n\n" +
            "Maths = POWER!"
        }
    };

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

        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE +
            "Sprites/Backgrounds/Default/background_color_trees.png"));
        bgTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

        characterTexture = new Texture(Gdx.files.internal(ASSET_BASE +
            "Sprites/Characters/Default/character_green_front.png"));
        characterTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        muteIconTex = new Texture(Gdx.files.internal("mute.png"));
        muteIconTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        unmuteIconTex = new Texture(Gdx.files.internal("unmute.png"));
        unmuteIconTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        createSkin();
        createUI();
    }

    // ---- Skin ----

    private void createSkin() {
        skin = new Skin();

        // -- Fonts --
        BitmapFont skinFont = FontGenerator.create(22, Color.WHITE);
        skin.add("default-font", skinFont);

        BitmapFont titleSkinFont = FontGenerator.create(42, Color.YELLOW, Color.DARK_GRAY, 2f);
        skin.add("title-font", titleSkinFont);

        BitmapFont subtitleFont = FontGenerator.create(18, Color.BLACK);
        skin.add("subtitle-font", subtitleFont);

        BitmapFont ruleHeaderFont = FontGenerator.create(28, Color.YELLOW, Color.DARK_GRAY, 1f);
        skin.add("rule-header-font", ruleHeaderFont);

        BitmapFont ruleBodyFont = FontGenerator.create(18, Color.WHITE);
        skin.add("rule-body-font", ruleBodyFont);

        // -- Button pixmaps --
        addPixmapTexture(skin, "button-up", 1, 1, new Color(0.2f, 0.6f, 0.3f, 1f));
        addPixmapTexture(skin, "button-over", 1, 1, new Color(0.3f, 0.8f, 0.4f, 1f));
        addPixmapTexture(skin, "button-down", 1, 1, new Color(0.15f, 0.5f, 0.25f, 1f));
        addPixmapTexture(skin, "nav-btn-up", 1, 1, new Color(0.25f, 0.5f, 0.7f, 1f));
        addPixmapTexture(skin, "nav-btn-over", 1, 1, new Color(0.35f, 0.65f, 0.85f, 1f));
        addPixmapTexture(skin, "nav-btn-disabled", 1, 1, new Color(0.3f, 0.3f, 0.3f, 0.5f));
        addPixmapTexture(skin, "slider-bg", 1, 1, new Color(0.3f, 0.3f, 0.3f, 0.8f));
        addPixmapTexture(skin, "slider-knob", 14, 20, new Color(0.4f, 0.8f, 0.4f, 1f));
        addPixmapTexture(skin, "slider-fill", 1, 1, new Color(0.3f, 0.7f, 0.3f, 0.9f));
        addPixmapTexture(skin, "window-bg", 1, 1, new Color(0.1f, 0.1f, 0.18f, 0.96f));

        // -- Styles --
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = drawable("button-up");
        btnStyle.over = drawable("button-over");
        btnStyle.down = drawable("button-down");
        btnStyle.font = skinFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.YELLOW;
        skin.add("default", btnStyle);

        TextButton.TextButtonStyle navStyle = new TextButton.TextButtonStyle();
        navStyle.up = drawable("nav-btn-up");
        navStyle.over = drawable("nav-btn-over");
        navStyle.disabled = drawable("nav-btn-disabled");
        navStyle.font = skinFont;
        navStyle.fontColor = Color.WHITE;
        navStyle.overFontColor = Color.YELLOW;
        navStyle.disabledFontColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
        skin.add("nav", navStyle);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = drawable("slider-bg");
        sliderStyle.knob = drawable("slider-knob");
        sliderStyle.knobBefore = drawable("slider-fill");
        skin.add("default-horizontal", sliderStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skinFont;
        windowStyle.titleFontColor = Color.YELLOW;
        windowStyle.background = drawable("window-bg");
        skin.add("default", windowStyle);

        addLabelStyle(skin, "default", skinFont, Color.WHITE);
        addLabelStyle(skin, "title", titleSkinFont, Color.YELLOW);
        addLabelStyle(skin, "subtitle", subtitleFont, Color.BLACK);
        addLabelStyle(skin, "rule-header", ruleHeaderFont, Color.YELLOW);
        addLabelStyle(skin, "rule-body", ruleBodyFont, Color.WHITE);
        addLabelStyle(skin, "page-num", skinFont, new Color(0.7f, 0.7f, 0.7f, 1f));
    }

    private void addPixmapTexture(Skin s, String name, int w, int h, Color c) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        s.add(name, new Texture(p));
        p.dispose();
    }

    private TextureRegionDrawable drawable(String name) {
        return new TextureRegionDrawable(new TextureRegion(skin.get(name, Texture.class)));
    }

    private void addLabelStyle(Skin s, String name, BitmapFont font, Color color) {
        Label.LabelStyle st = new Label.LabelStyle();
        st.font = font;
        st.fontColor = color;
        s.add(name, st);
    }

    // ---- UI ----

    private void createUI() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title
        root.add().expandY().row();
        Label title = new Label("MathDash", skin, "title");
        root.add(title).padBottom(5).colspan(1).row();

        Label subtitle = new Label("Run, Think, Solve!", skin, "subtitle");
        root.add(subtitle).padBottom(25).row();

        // Level buttons - centered in the middle
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

            Table row = new Table();
            TextButton btn = new TextButton(levelNames[i], skin);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    IAudioSystem audio = ServiceLocator.getAudio();
                    if (audio != null) audio.playSound("select");
                    if (callback != null) callback.onLevelSelected(lvl);
                }
            });

            Label hsLabel = new Label("Best: " + highScore, skin, "subtitle");
            row.add(btn).width(300).height(45).padLeft(95).padRight(15);
            row.add(hsLabel).width(80);
            root.add(row).padBottom(10).row();
        }

        // Spacer pushes controls toward the bottom
        root.add().expandY().row();

        // ---- Bottom controls ----
        Table controls = new Table();

        // "How to Play" button
        TextButton howToPlayBtn = new TextButton("How to Play", skin);
        howToPlayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRulebook();
            }
        });
        controls.add(howToPlayBtn).width(160).height(40).padRight(25);

        // Volume label + slider
        IAudioSystem audio = ServiceLocator.getAudio();
        int vol = audio != null ? Math.max(1, Math.round(audio.getVolume() * 10f)) : 7;

        volumeLabel = new Label("Vol: " + vol, skin);
        controls.add(volumeLabel).padRight(8);

        Slider volumeSlider = new Slider(1, 10, 1, false, skin);
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
        controls.add(volumeSlider).width(120).height(30).padRight(15);

        // Mute button using PNG icons
        boolean isMuted = audio != null && audio.isMuted();
        TextureRegionDrawable muteDrawable = new TextureRegionDrawable(new TextureRegion(muteIconTex));
        TextureRegionDrawable unmuteDrawable = new TextureRegionDrawable(new TextureRegion(unmuteIconTex));

        ImageButton.ImageButtonStyle muteBtnStyle = new ImageButton.ImageButtonStyle();
        muteBtnStyle.imageUp = isMuted ? muteDrawable : unmuteDrawable;
        muteBtn = new ImageButton(muteBtnStyle);
        muteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMute();
            }
        });
        controls.add(muteBtn).size(36, 36);

        root.add(controls).padBottom(20).row();
        stage.addActor(root);
    }

    private void toggleMute() {
        IAudioSystem audio = ServiceLocator.getAudio();
        if (audio == null) return;

        boolean newMuted = !audio.isMuted();
        audio.setMuted(newMuted);
        updateMuteIcon();
    }

    private void updateMuteIcon() {
        IAudioSystem audio = ServiceLocator.getAudio();
        if (audio == null || muteBtn == null) return;

        boolean muted = audio.isMuted();
        TextureRegionDrawable icon = new TextureRegionDrawable(
            new TextureRegion(muted ? muteIconTex : unmuteIconTex)
        );
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = icon;
        muteBtn.setStyle(style);
    }

    // ---- Paginated Rulebook ----

    private void showRulebook() {
        currentPage = 0;
        buildRulebookWindow();
    }

    private void buildRulebookWindow() {
        if (rulebookWindow != null) rulebookWindow.remove();

        rulebookWindow = new Window("", skin);
        rulebookWindow.setMovable(false);
        rulebookWindow.setModal(true);

        Table content = new Table();
        content.pad(20, 25, 15, 25);

        // Page header: "Section X / Total"
        String pageHeader = (currentPage + 1) + " / " + RULEBOOK_PAGES.length;
        Label pageLabel = new Label(pageHeader, skin, "page-num");
        content.add(pageLabel).right().row();

        // Section title
        Label sectionTitle = new Label(RULEBOOK_PAGES[currentPage][0], skin, "rule-header");
        sectionTitle.setAlignment(Align.center);
        content.add(sectionTitle).padTop(5).padBottom(15).expandX().center().row();

        // Section body
        Label sectionBody = new Label(RULEBOOK_PAGES[currentPage][1], skin, "rule-body");
        sectionBody.setWrap(false);
        sectionBody.setAlignment(Align.topLeft);
        content.add(sectionBody).expand().fill().row();

        // Navigation: < Prev | Next >
        Table navRow = new Table();

        TextButton prevBtn = new TextButton("<", skin, "nav");
        prevBtn.setDisabled(currentPage == 0);
        if (currentPage > 0) {
            prevBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentPage--;
                    buildRulebookWindow();
                }
            });
        }
        navRow.add(prevBtn).width(60).height(40).padRight(20);

        TextButton nextBtn = new TextButton(">", skin, "nav");
        boolean isLastPage = currentPage == RULEBOOK_PAGES.length - 1;
        nextBtn.setDisabled(isLastPage);
        if (!isLastPage) {
            nextBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentPage++;
                    buildRulebookWindow();
                }
            });
        }
        navRow.add(nextBtn).width(60).height(40);
        content.add(navRow).padTop(15).center().row();

        // Action buttons at the bottom
        Table actionRow = new Table();

        TextButton backBtn = new TextButton("Back to Menu", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rulebookWindow != null) {
                    rulebookWindow.remove();
                    rulebookWindow = null;
                }
            }
        });
        actionRow.add(backBtn).width(170).height(40).padRight(15);

        TextButton playBtn = new TextButton("Start Playing!", skin);
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rulebookWindow != null) {
                    rulebookWindow.remove();
                    rulebookWindow = null;
                }
                // Start level 1 by default
                if (callback != null) callback.onLevelSelected(1);
            }
        });
        actionRow.add(playBtn).width(170).height(40);
        content.add(actionRow).padTop(15).center().row();

        rulebookWindow.add(content).expand().fill();
        rulebookWindow.setSize(440, 480);
        rulebookWindow.setPosition(
            WORLD_WIDTH / 2f - 220,
            WORLD_HEIGHT / 2f - 240
        );

        stage.addActor(rulebookWindow);
    }

    // ---- Lifecycle ----

    @Override
    public void update(float deltaTime) {
        bgScrollX += BG_SCROLL_SPEED * deltaTime;
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float bgWidth = WORLD_WIDTH;
        float offset = bgScrollX % bgWidth;
        batch.draw(bgTexture, -offset, 0, bgWidth, WORLD_HEIGHT);
        batch.draw(bgTexture, bgWidth - offset, 0, bgWidth, WORLD_HEIGHT);

        batch.draw(characterTexture, 50, 80, 96, 96);
        batch.draw(characterTexture, WORLD_WIDTH - 146, 80, 96, 96);

        batch.end();
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
            refreshUI();
        }
    }

    private void refreshUI() {
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
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (characterTexture != null) characterTexture.dispose();
        if (muteIconTex != null) muteIconTex.dispose();
        if (unmuteIconTex != null) unmuteIconTex.dispose();
    }
}
