package io.github.some_example_name.lwjgl3.logic.entity;

/**
 * CharacterFactory - Creates the player Character via GenericFactory.
 *
 * @param x  centre-lane x-coordinate
 * @param y  floor surface y-coordinate (character stands on top)
 */
public class CharacterFactory implements GenericFactory<Character> {

    @Override
    public Character create(float x, float y) {
        return new Character(x, y);
    }
}
