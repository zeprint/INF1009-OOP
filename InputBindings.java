package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class InputBindings {

    // Axis can have multiple negative/positive key pairs (A/D + LEFT/RIGHT etc.)
    private final Map<InputAxis, List<int[]>> axisPairs = new EnumMap<>(InputAxis.class);

    // One key per action (you can expand later if you want multiple)
    private final Map<InputAction, Integer> actionKey = new EnumMap<>(InputAction.class);

    public void bindAxis(InputAxis axis, int negativeKey, int positiveKey) {
        axisPairs.computeIfAbsent(axis, k -> new ArrayList<>()).add(new int[]{negativeKey, positiveKey});
    }

    public List<int[]> getAxisPairs(InputAxis axis) {
        return axisPairs.getOrDefault(axis, Collections.emptyList());
    }

    public void bindAction(InputAction action, int key) {
        actionKey.put(action, key);
    }

    public Integer getActionKey(InputAction action) {
        return actionKey.get(action);
    }
}
