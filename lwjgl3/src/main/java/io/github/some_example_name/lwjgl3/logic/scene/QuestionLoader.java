package io.github.some_example_name.lwjgl3.logic.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collections;

/**
 * QuestionLoader - Loads word challenge questions from a JSON file.
 *
 * SRP: This class has one responsibility — reading question data from
 * an external file and converting it into WordChallenge objects.
 *
 * OCP: Adding new questions only requires editing questions.json,
 * no Java code changes needed.
 *
 * Usage: ArrayList<WordChallenge> pool = QuestionLoader.load("questions.json");
 */
public class QuestionLoader {

    private static final String TAG = "QuestionLoader";

    /**
     * Loads all questions from the given JSON asset file.
     * Shuffles the list before returning so question order is random each time.
     *
     * @param assetPath path relative to the assets folder
     * @return shuffled list of WordChallenge objects, or empty list on error
     */
    public static ArrayList<WordChallenge> load(String assetPath) {
        ArrayList<WordChallenge> questions = new ArrayList<>();

        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(Gdx.files.internal(assetPath));

            for (JsonValue entry = root.child; entry != null; entry = entry.next) {
                String sentence = entry.getString("sentence");
                String correct  = entry.getString("correct");
                String wrong1   = entry.getString("wrong1");
                String wrong2   = entry.getString("wrong2");

                if (sentence != null && correct != null && wrong1 != null && wrong2 != null) {
                    questions.add(new WordChallenge(sentence, correct, wrong1, wrong2));
                }
            }

            Collections.shuffle(questions);
            Gdx.app.log(TAG, "Loaded " + questions.size() + " questions from " + assetPath);

        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to load questions from " + assetPath, e);
        }

        return questions;
    }

    /** Private constructor — utility class, not instantiable. */
    private QuestionLoader() {}
}
