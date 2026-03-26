package io.github.mathdash.logic.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.mathdash.engine.difficulty.DifficultyAdapter;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.logic.component.SurgeComponent;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.Player;
import io.github.mathdash.logic.math.MathQuestion;
import io.github.mathdash.logic.scene.GameScene;


/**
 * HudRenderer - Renders the heads-up display: hearts, score, level,
 * question text, surge bar, and streak counter.
 *
 * Extracted from GameScene to follow the Single Responsibility Principle.
 */
public class HudRenderer {

    private final Texture heartTexture;
    private final Texture heartEmptyTexture;
    private final Texture surgeBarBgTex;
    private final Texture surgeBarFillTex;
    private final Texture surgeTintTex;
    private final BitmapFont font;
    private final BitmapFont hudFont;
    private final BitmapFont questionFont;
    private final BitmapFont streakFont;
    private final GlyphLayout glyphLayout;

    public HudRenderer(Texture heartTexture, Texture heartEmptyTexture,
                       Texture surgeBarBgTex, Texture surgeBarFillTex, Texture surgeTintTex,
                       BitmapFont font, BitmapFont hudFont, BitmapFont questionFont,
                       BitmapFont streakFont, GlyphLayout glyphLayout) {
        this.heartTexture = heartTexture;
        this.heartEmptyTexture = heartEmptyTexture;
        this.surgeBarBgTex = surgeBarBgTex;
        this.surgeBarFillTex = surgeBarFillTex;
        this.surgeTintTex = surgeTintTex;
        this.font = font;
        this.hudFont = hudFont;
        this.questionFont = questionFont;
        this.streakFont = streakFont;
        this.glyphLayout = glyphLayout;
    }

    public void render(SpriteBatch batch, Player player, SurgeComponent surge,
                       DifficultyAdapter difficulty, MathQuestion currentQuestion,
                       int score, int level, Array<AnswerBlock> activeAnswers) {

        // Answer values on blocks
        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform t = block.getComponent(Transform.class);
            if (t == null) continue;
            String text = String.valueOf(block.getAnswerValue());
            glyphLayout.setText(font, text);
            font.draw(batch, text,
                t.getX() - glyphLayout.width / 2f,
                t.getY() + glyphLayout.height / 2f);
        }

        // Hearts
        float hudY = GameScene.WORLD_HEIGHT - 40f;
        int lives = player != null ? player.getLives() : 0;
        for (int i = 0; i < 3; i++) {
            Texture hTex = i < lives ? heartTexture : heartEmptyTexture;
            batch.draw(hTex, 10 + i * 35, hudY, 30, 30);
        }

        // Score
        String scoreText = "Score: " + score;
        glyphLayout.setText(hudFont, scoreText);
        hudFont.draw(batch, scoreText, GameScene.WORLD_WIDTH - glyphLayout.width - 20, hudY + 25);

        // Level
        String levelText = "Level " + level;
        glyphLayout.setText(hudFont, levelText);
        hudFont.draw(batch, levelText, GameScene.WORLD_WIDTH / 2f - glyphLayout.width / 2f, hudY + 25);

        // Question
        if (currentQuestion != null) {
            String qText = currentQuestion.getQuestionText();
            glyphLayout.setText(questionFont, qText);
            questionFont.draw(batch, qText,
                GameScene.WORLD_WIDTH / 2f - glyphLayout.width / 2f,
                GameScene.WORLD_HEIGHT - 70);
        }

        // Surge meter bar
        float barW = 200f, barH = 16f;
        float barX = GameScene.WORLD_WIDTH / 2f - barW / 2f;
        float barY = 15f;
        batch.draw(surgeBarBgTex, barX - 2, barY - 2, barW + 4, barH + 4);

        float fillW = barW * surge.getSurgeAmount();
        if (surge.isSurging()) {
            float pulse = 0.8f + 0.2f * MathUtils.sin(surge.getSurgeProgress() * 20f);
            batch.setColor(1f, 0.85f, 0f, pulse);
        } else {
            batch.setColor(0.2f, 0.9f, 0.3f, 0.9f);
        }
        batch.draw(surgeBarFillTex, barX, barY, fillW, barH);
        batch.setColor(Color.WHITE);

        // Surge label
        String surgeLabel = surge.isSurging() ? "SURGE!" : "Surge";
        glyphLayout.setText(streakFont, surgeLabel);
        streakFont.draw(batch, surgeLabel,
            barX + barW / 2f - glyphLayout.width / 2f,
            barY + barH + glyphLayout.height + 4);

        // Streak counter
        if (difficulty.getCorrectStreak() >= 2) {
            String streakText = difficulty.getCorrectStreak() + "x Streak!";
            glyphLayout.setText(streakFont, streakText);
            streakFont.draw(batch, streakText,
                GameScene.WORLD_WIDTH / 2f - glyphLayout.width / 2f,
                barY + barH + glyphLayout.height + 24);
        }

        // Surge mode golden screen tint
        if (surge.isSurging()) {
            float alpha = 0.1f + 0.05f * MathUtils.sin(surge.getSurgeProgress() * 15f);
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(surgeTintTex, 0, 0, GameScene.WORLD_WIDTH, GameScene.WORLD_HEIGHT);
            batch.setColor(Color.WHITE);
        }
    }
}
