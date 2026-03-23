package io.github.mathdash.logic.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * FontGenerator - Generates crisp BitmapFonts at any size using FreeType.
 */
public class FontGenerator {

    private static FreeTypeFontGenerator generator;

    private static FreeTypeFontGenerator getGenerator() {
        if (generator == null) {
            generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        }
        return generator;
    }

    public static BitmapFont create(int size, Color color) {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = color;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = getGenerator().generateFont(param);
        return font;
    }

    public static BitmapFont create(int size, Color color, Color borderColor, float borderWidth) {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = color;
        param.borderColor = borderColor;
        param.borderWidth = borderWidth;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = getGenerator().generateFont(param);
        return font;
    }

    public static void dispose() {
        if (generator != null) {
            generator.dispose();
            generator = null;
        }
    }
}
