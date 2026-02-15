package io.github.some_example_name.lwjgl3;

/**
 * DistributionType - Strategy interface for random number generation.
 *
 * Different distributions can be swapped in wherever randomised values are needed.
 */
public interface DistributionType {

    float next();

    void setRange(float min, float max);

    float getMin();
    float getMax();
}
