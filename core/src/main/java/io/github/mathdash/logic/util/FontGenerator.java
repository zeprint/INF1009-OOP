package io.github.mathdash.logic.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * FontGenerator - Generates crisp BitmapFonts at any size using FreeType.
 * Instance-based: each owner creates its own FontGenerator and disposes it
 * in its own cleanup method
 */
public class FontGenerator {

    private FreeTypeFontGenerator generator;

    public FontGenerator() {
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
    }

    public BitmapFont create(int size, Color color) {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = color;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        return generator.generateFont(param);
    }

    public BitmapFont create(int size, Color color, Color borderColor, float borderWidth) {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = color;
        param.borderColor = borderColor;
        param.borderWidth = borderWidth;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        return generator.generateFont(param);
    }

    public void dispose() {
        if (generator != null) {
            generator.dispose();
            generator = null;
        }
    }
}
