package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.MathUtils;

/**
 * MobileRandom - Uniform random distribution over a configurable [min, max] range.
 *
 * Implements DistributionType so callers depend on the abstraction (DIP).
 * Uses libGDX's MathUtils.random().
 */
public class MobileRandom implements DistributionType {

    private float min;
    private float max;

    public MobileRandom(float min, float max) {
        if (Float.isNaN(min) || Float.isInfinite(min) || Float.isNaN(max) || Float.isInfinite(max)) {
            throw new IllegalArgumentException("range values must be finite");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.min = min;
        this.max = max;
    }

    /** Default unit range [0, 1]. */
    public MobileRandom() {
        this(0f, 1f);
    }

    @Override
    public float next() {
        return MathUtils.random(min, max);
    }

    @Override
    public void setRange(float min, float max) {
        if (Float.isNaN(min) || Float.isInfinite(min) || Float.isNaN(max) || Float.isInfinite(max)) {
            throw new IllegalArgumentException("range values must be finite");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.min = min;
        this.max = max;
    }

    @Override public float getMin() { return min; }
    @Override public float getMax() { return max; }
}
