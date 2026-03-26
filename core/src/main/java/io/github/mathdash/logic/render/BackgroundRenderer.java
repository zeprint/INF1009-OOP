package io.github.mathdash.logic.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * BackgroundRenderer - Renders the scrolling parallax background,
 * lane bands (grass + dirt), and decorative elements.
 *
 * Extracted from GameScene to follow the Single Responsibility Principle.
 */
public class BackgroundRenderer {

    private final float worldWidth;
    private final float worldHeight;
    private static final float[][] DIRT_BANDS = {
        {50f, 100f},
        {200f, 100f},
        {350f, 100f},
    };
    private static final float[][] GRASS_BANDS = {
        {0f, 50f},
        {150f, 50f},
        {300f, 50f},
        {450f, 30f},
    };
    private static final float SKY_Y = 480f;
    private static final float DECO_SPAWN_INTERVAL = 80f;

    private final Texture bgTexture;
    private final Texture grassBgTexture;
    private final Texture dirtBgTexture;
    private final Texture decoGrassTex;
    private final Texture decoBushTex;

    private float bgScrollX = 0f;
    private float floorScrollX = 0f;
    private final Array<float[]> decorations = new Array<>();
    private float decoSpawnAccum = 0f;

    public BackgroundRenderer(float worldWidth, float worldHeight, Texture bgTexture, Texture grassBgTexture, Texture dirtBgTexture,
                              Texture decoGrassTex, Texture decoBushTex) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.bgTexture = bgTexture;
        this.grassBgTexture = grassBgTexture;
        this.dirtBgTexture = dirtBgTexture;
        this.decoGrassTex = decoGrassTex;
        this.decoBushTex = decoBushTex;

        for (float x = 0; x < this.worldWidth; x += DECO_SPAWN_INTERVAL) {
            float[] band = GRASS_BANDS[MathUtils.random(GRASS_BANDS.length - 1)];
            float y = band[0] + MathUtils.random(0f, Math.max(0f, band[1] - 48f));
            float texIdx = MathUtils.randomBoolean() ? 0f : 1f;
            decorations.add(new float[]{x + MathUtils.random(-20f, 20f), y, texIdx});
        }
    }

    public void update(float deltaTime, float scrollSpeed) {
        bgScrollX += scrollSpeed * 0.3f * deltaTime;
        floorScrollX += scrollSpeed * deltaTime;

        float decoMove = scrollSpeed * deltaTime;
        for (int i = decorations.size - 1; i >= 0; i--) {
            float[] d = decorations.get(i);
            d[0] -= decoMove;
            if (d[0] < -50f) {
                decorations.removeIndex(i);
            }
        }
        decoSpawnAccum += decoMove;
        while (decoSpawnAccum >= DECO_SPAWN_INTERVAL) {
            decoSpawnAccum -= DECO_SPAWN_INTERVAL;
            float[] band = GRASS_BANDS[MathUtils.random(GRASS_BANDS.length - 1)];
            float y = band[0] + MathUtils.random(0f, Math.max(0f, band[1] - 48f));
            float texIdx = MathUtils.randomBoolean() ? 0f : 1f;
            decorations.add(new float[]{this.worldWidth + MathUtils.random(0f, 40f), y, texIdx});
        }
    }

    public void render(SpriteBatch batch) {
        float bgWidth = this.worldWidth;

        // Sky background with parallax
        float bgOffset = bgScrollX % bgWidth;
        batch.draw(bgTexture, -bgOffset, SKY_Y, bgWidth, this.worldHeight - SKY_Y);
        batch.draw(bgTexture, bgWidth - bgOffset, SKY_Y, bgWidth, this.worldHeight - SKY_Y);

        // Scrolling lane bands
        float laneOffset = floorScrollX % bgWidth;
        for (float[] band : GRASS_BANDS) {
            batch.draw(grassBgTexture, -laneOffset, band[0], bgWidth, band[1]);
            batch.draw(grassBgTexture, bgWidth - laneOffset, band[0], bgWidth, band[1]);
        }
        float dirtTileW = 100f;
        int dirtTilesNeeded = (int) (this.worldWidth / dirtTileW) + 2;
        float dirtOffset = floorScrollX % dirtTileW;
        for (float[] band : DIRT_BANDS) {
            for (int i = 0; i < dirtTilesNeeded; i++) {
                float dx = i * dirtTileW - dirtOffset;
                batch.draw(dirtBgTexture, dx, band[0], dirtTileW, band[1]);
            }
        }

        // Decorations
        for (int i = 0; i < decorations.size; i++) {
            float[] d = decorations.get(i);
            Texture tex = d[2] == 0f ? decoGrassTex : decoBushTex;
            batch.draw(tex, d[0], d[1], 48, 48);
        }
    }
}
