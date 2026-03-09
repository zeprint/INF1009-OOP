package io.github.some_example_name.lwjgl3;

/**
 * VerticalMovement - Recycles an entity when it crosses a vertical boundary.
 */
public class VerticalMovement extends MovementComponent {

    private float bottomBoundaryY;
    private float resetTopY;
    private boolean boundsConfigured;

    private DistributionType xDistribution;

    public VerticalMovement(Positionable entity) {
        super(entity);
        this.boundsConfigured = false;
        this.xDistribution = null;
    }

    @Override
    public void update(float deltaTime) {
        validateDeltaTime(deltaTime);

        if (!enabled || !boundsConfigured) {
            return;
        }

        Positionable entity = getEntity();
        if (entity.getY() > bottomBoundaryY) {
            return;
        }

        entity.setY(resetTopY);
        if (xDistribution != null) {
            entity.setX(xDistribution.next());
        }
    }

    public void setVerticalBounds(float bottomBoundaryY, float resetTopY) {
        if (!Float.isFinite(bottomBoundaryY) || !Float.isFinite(resetTopY)) {
            throw new IllegalArgumentException("vertical bounds must be finite");
        }
        this.bottomBoundaryY = bottomBoundaryY;
        this.resetTopY = resetTopY;
        this.boundsConfigured = true;
    }

    public void setXDistribution(DistributionType distribution) {
        this.xDistribution = distribution;
    }
}
