package io.github.some_example_name.lwjgl3;

/**
 * DistributionType - Strategy interface for random number generation (DIP).
 *
 * Different distributions (uniform, Gaussian, etc.) can be swapped in
 * wherever randomised values are needed (e.g. spawn positions).
 *
 * Implementing class: MobileRandom
 */
public interface DistributionType {

    /** Generate and return the next random value within the configured range. */
    float next();

    /** Set the inclusive [min, max] range for generation. */
    void setRange(float min, float max);

    float getMin();
    float getMax();
}
