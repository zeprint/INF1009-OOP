package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * FaultTolerantCollisionManager - Robust collision detection with comprehensive error handling.
 *
 * Features:
 * - Input validation and sanitization
 * - Exception isolation (one bad object won't crash the system)
 * - Automatic corrupt object detection and removal
 * - Circuit breaker pattern for repeatedly failing objects
 * - Performance monitoring and logging
 * - NaN/Infinity detection
 * - Per-frame error limits
 *
 * Implements ICollisionSystem for DIP compliance.
 */
public class CollisionManager implements ICollisionSystem {

    // ============================================================================
    // FIELDS
    // ============================================================================

    private final Array<Collidable> collidables = new Array<>();
    private final Array<Collidable> corruptedObjects = new Array<>();
    private final Map<Collidable, ErrorTracker> errorTrackers = new HashMap<>();
    private final CollisionMetrics metrics = new CollisionMetrics();

    // Configuration
    private static final int MAX_ERRORS_PER_OBJECT = 5;
    private static final int MAX_ERRORS_PER_FRAME = 20;
    private static final long CIRCUIT_BREAKER_RESET_MS = 5000; // 5 seconds
    private static final long PERFORMANCE_WARNING_NS = 16_000_000; // 16ms (60 FPS)

    private boolean enableLogging = true;
    private boolean enableMetrics = true;

    // ============================================================================
    // ERROR TRACKING
    // ============================================================================

    /**
     * Tracks errors and implements circuit breaker pattern for individual objects.
     */
    private static class ErrorTracker {
        int errorCount = 0;
        long lastErrorTime = 0;
        long firstErrorTime = 0;
        boolean circuitOpen = false;

        void recordError() {
            if (errorCount == 0) {
                firstErrorTime = System.currentTimeMillis();
            }
            errorCount++;
            lastErrorTime = System.currentTimeMillis();

            if (errorCount >= MAX_ERRORS_PER_OBJECT) {
                circuitOpen = true;
            }
        }

        void recordSuccess() {
            // Gradual recovery - reduce error count on success
            if (errorCount > 0) {
                errorCount--;
            }
            if (errorCount == 0) {
                circuitOpen = false;
            }
        }

        boolean shouldBlock() {
            if (!circuitOpen) return false;

            // Try to reset circuit breaker after timeout
            long timeSinceLastError = System.currentTimeMillis() - lastErrorTime;
            if (timeSinceLastError > CIRCUIT_BREAKER_RESET_MS) {
                circuitOpen = false;
                errorCount = 0;
                return false;
            }

            return true;
        }

        boolean isProblematic() {
            return errorCount >= MAX_ERRORS_PER_OBJECT;
        }
    }

    // ============================================================================
    // METRICS & MONITORING
    // ============================================================================

    /**
     * Tracks performance metrics for monitoring and debugging.
     */
    private static class CollisionMetrics {
        long totalFrames = 0;
        long totalCollisions = 0;
        long totalErrors = 0;
        long worstFrameTimeNs = 0;
        long totalFrameTimeNs = 0;
        int maxObjectCount = 0;

        void recordFrame(long elapsedNs, int objectCount, int collisionsDetected, int errorCount) {
            totalFrames++;
            totalCollisions += collisionsDetected;
            totalErrors += errorCount;
            totalFrameTimeNs += elapsedNs;

            if (elapsedNs > worstFrameTimeNs) {
                worstFrameTimeNs = elapsedNs;
            }

            if (objectCount > maxObjectCount) {
                maxObjectCount = objectCount;
            }
        }

        void printSummary() {
            if (totalFrames == 0) return;

            double avgFrameTimeMs = (totalFrameTimeNs / (double) totalFrames) / 1_000_000.0;
            double worstFrameTimeMs = worstFrameTimeNs / 1_000_000.0;
            double avgCollisions = totalCollisions / (double) totalFrames;
            double errorRate = (totalErrors / (double) totalFrames) * 100.0;

            System.out.println("\n========== Collision System Metrics ==========");
            System.out.println("Total Frames:        " + totalFrames);
            System.out.println("Max Objects:         " + maxObjectCount);
            System.out.println("Total Collisions:    " + totalCollisions);
            System.out.println("Avg Collisions/Frame: " + String.format("%.2f", avgCollisions));
            System.out.println("Total Errors:        " + totalErrors);
            System.out.println("Error Rate:          " + String.format("%.2f%%", errorRate));
            System.out.println("Avg Frame Time:      " + String.format("%.2fms", avgFrameTimeMs));
            System.out.println("Worst Frame Time:    " + String.format("%.2fms", worstFrameTimeMs));
            System.out.println("==============================================\n");
        }
    }

    // ============================================================================
    // PUBLIC API (ICollisionSystem Implementation)
    // ============================================================================

    @Override
    public void addObject(Collidable obj) {
        // Null check
        if (obj == null) {
            logWarning("Attempted to add null Collidable - rejected");
            return;
        }

        // Duplicate check
        if (collidables.contains(obj, true)) {
            logWarning("Collidable already registered - skipping: " + obj.getClass().getSimpleName());
            return;
        }

        // Validation check
        if (!validateCollidable(obj)) {
            logError("Collidable failed validation - rejected: " + obj.getClass().getSimpleName());
            return;
        }

        // Add successfully
        collidables.add(obj);
        logInfo("Added Collidable: " + obj.getClass().getSimpleName() +
            " (total: " + collidables.size + ")");
    }

    @Override
    public void removeObject(Collidable obj) {
        if (obj == null) return;

        boolean removed = collidables.removeValue(obj, true);
        if (removed) {
            errorTrackers.remove(obj);
            logInfo("Removed Collidable: " + obj.getClass().getSimpleName() +
                " (remaining: " + collidables.size + ")");
        }
    }

    /**
     * Returns a copy of the collidables array for safe iteration.
     * Modifications to this array won't affect the collision system.
     */
    public Array<Collidable> getCollidables() {
        return new Array<>(collidables);
    }

    @Override
    public void checkCollisions() {
        long startTime = System.nanoTime();

        corruptedObjects.clear();
        int frameErrors = 0;
        int collisionsDetected = 0;

        try {
            // Main collision detection loop
            for (int i = 0; i < collidables.size; i++) {
                // Emergency brake - too many errors this frame
                if (frameErrors >= MAX_ERRORS_PER_FRAME) {
                    logError("CRITICAL: Max errors per frame exceeded (" + MAX_ERRORS_PER_FRAME +
                        ") - aborting collision checks");
                    break;
                }

                for (int j = i + 1; j < collidables.size; j++) {
                    Collidable a = collidables.get(i);
                    Collidable b = collidables.get(j);

                    // Pre-flight checks
                    if (!canProcessCollision(a, b)) {
                        continue;
                    }

                    // Process collision with full error isolation
                    try {
                        if (detectCollision(a, b)) {
                            collisionsDetected++;
                            processCollisionPair(a, b);
                        }
                    } catch (Exception e) {
                        frameErrors++;
                        handleCollisionPairError(a, b, e);
                    }
                }
            }

            // Cleanup corrupted objects
            removeCorruptedObjects();

        } finally {
            // Always record metrics, even if we crashed
            long elapsed = System.nanoTime() - startTime;

            if (enableMetrics) {
                metrics.recordFrame(elapsed, collidables.size, collisionsDetected, frameErrors);
            }

            // Performance warning
            if (elapsed > PERFORMANCE_WARNING_NS) {
                double elapsedMs = elapsed / 1_000_000.0;
                logWarning(String.format("Collision detection slow: %.2fms (target: 16ms for 60 FPS)",
                    elapsedMs));
            }

            // Error summary
            if (frameErrors > 0) {
                logWarning("Frame completed with " + frameErrors + " errors");
            }
        }
    }

    // ============================================================================
    // VALIDATION
    // ============================================================================

    /**
     * Validates that a Collidable is in a valid state.
     * Checks for null references, invalid float values, and proper initialization.
     */
    private boolean validateCollidable(Collidable obj) {
        if (obj == null) {
            return false;
        }

        try {
            // Validate bounds
            Rectangle bounds = obj.getBounds();
            if (bounds == null) {
                logError("Collidable has null bounds: " + obj.getClass().getSimpleName());
                return false;
            }

            if (!isValidRectangle(bounds)) {
                logError("Collidable has invalid rectangle: " + obj.getClass().getSimpleName());
                return false;
            }

            // Validate collision type
            CollisionType type = obj.getType();
            if (type == null) {
                logError("Collidable has null CollisionType: " + obj.getClass().getSimpleName());
                return false;
            }

            return true;

        } catch (Exception e) {
            logError("Exception during validation of " + obj.getClass().getSimpleName() +
                ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a Rectangle contains valid float values.
     * Rejects NaN, Infinity, and negative dimensions.
     */
    private boolean isValidRectangle(Rectangle r) {
        if (r == null) return false;

        // Check for NaN
        if (Float.isNaN(r.x) || Float.isNaN(r.y) ||
            Float.isNaN(r.width) || Float.isNaN(r.height)) {
            return false;
        }

        // Check for Infinity
        if (Float.isInfinite(r.x) || Float.isInfinite(r.y) ||
            Float.isInfinite(r.width) || Float.isInfinite(r.height)) {
            return false;
        }

        // Check for negative dimensions
        if (r.width < 0 || r.height < 0) {
            return false;
        }

        // Optional: Check for unreasonably large values
        float maxValue = 1_000_000f;
        if (Math.abs(r.x) > maxValue || Math.abs(r.y) > maxValue ||
            r.width > maxValue || r.height > maxValue) {
            return false;
        }

        return true;
    }

    /**
     * Determines if a collision pair can be safely processed.
     * Checks for null, circuit breaker status, and validation.
     */
    private boolean canProcessCollision(Collidable a, Collidable b) {
        // Null check
        if (a == null || b == null) {
            logWarning("Null collidable encountered in collision loop");
            return false;
        }

        // Circuit breaker check
        if (isBlocked(a)) {
            // Object A is in circuit breaker mode - skip silently
            return false;
        }
        if (isBlocked(b)) {
            // Object B is in circuit breaker mode - skip silently
            return false;
        }

        // Validation check
        if (!validateCollidable(a)) {
            markAsCorrupted(a);
            return false;
        }
        if (!validateCollidable(b)) {
            markAsCorrupted(b);
            return false;
        }

        return true;
    }

    // ============================================================================
    // COLLISION DETECTION & RESOLUTION
    // ============================================================================

    /**
     * AABB (Axis-Aligned Bounding Box) collision detection.
     * Returns true if the two collidables overlap.
     */
    private boolean detectCollision(Collidable a, Collidable b) {
        try {
            Rectangle ra = a.getBounds();
            Rectangle rb = b.getBounds();

            // Additional safety check
            if (ra == null || rb == null) {
                logWarning("Null bounds in detectCollision");
                return false;
            }

            if (!isValidRectangle(ra) || !isValidRectangle(rb)) {
                logWarning("Invalid rectangle in detectCollision");
                return false;
            }

            return ra.overlaps(rb);

        } catch (Exception e) {
            logError("Exception in detectCollision: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculates collision resolution data for a pair of objects.
     * Determines overlap amounts and collision direction.
     */
    private CollisionResult resolveCollision(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();

        // Calculate overlap on each axis
        float overlapX = Math.min(ra.x + ra.width - rb.x, rb.x + rb.width - ra.x);
        float overlapY = Math.min(ra.y + ra.height - rb.y, rb.y + rb.height - ra.y);

        // Determine collision direction based on minimum overlap axis
        CollisionDirection direction;
        if (overlapX < overlapY) {
            // Collision from left or right
            direction = (ra.x < rb.x) ? CollisionDirection.RIGHT : CollisionDirection.LEFT;
        } else {
            // Collision from top or bottom
            direction = (ra.y < rb.y) ? CollisionDirection.TOP : CollisionDirection.BOTTOM;
        }

        return new CollisionResult(b, overlapX, overlapY, direction);
    }

    /**
     * Processes a collision between two objects.
     * Each object gets its own try/catch for maximum error isolation.
     */
    private void processCollisionPair(Collidable a, Collidable b) {
        // Process collision for object A
        try {
            CollisionResult resultA = resolveCollision(a, b);
            applyBehaviour(a, resultA);
            recordSuccess(a);
        } catch (Exception e) {
            handleObjectError(a, e, "processing collision with " + b.getClass().getSimpleName());
        }

        // Process collision for object B (independent of A's success/failure)
        try {
            CollisionResult resultB = resolveCollision(b, a);
            applyBehaviour(b, resultB);
            recordSuccess(b);
        } catch (Exception e) {
            handleObjectError(b, e, "processing collision with " + a.getClass().getSimpleName());
        }
    }

    /**
     * Dispatches collision event to the collidable object.
     * Only calls onCollision if the object's type triggers events.
     */
    private void applyBehaviour(Collidable obj, CollisionResult result) {
        if (obj == null || result == null) {
            logWarning("Null parameter in applyBehaviour");
            return;
        }

        try {
            CollisionType type = obj.getType();
            if (type != null && type.triggersEvent()) {
                obj.onCollision(result);
            }
        } catch (Exception e) {
            // Re-throw to be caught by caller's try/catch
            // This allows proper error tracking
            throw new RuntimeException("onCollision callback failed", e);
        }
    }

    // ============================================================================
    // ERROR HANDLING
    // ============================================================================

    /**
     * Checks if an object is blocked by the circuit breaker.
     */
    private boolean isBlocked(Collidable obj) {
        ErrorTracker tracker = errorTrackers.get(obj);
        return tracker != null && tracker.shouldBlock();
    }

    /**
     * Records a successful operation for an object.
     * Gradually reduces error count to allow recovery.
     */
    private void recordSuccess(Collidable obj) {
        ErrorTracker tracker = errorTrackers.get(obj);
        if (tracker != null) {
            tracker.recordSuccess();
        }
    }

    /**
     * Handles an error from a specific object.
     * Tracks errors and opens circuit breaker if threshold exceeded.
     */
    private void handleObjectError(Collidable obj, Exception e, String context) {
        ErrorTracker tracker = errorTrackers.computeIfAbsent(obj, k -> new ErrorTracker());
        tracker.recordError();

        String objName = obj != null ? obj.getClass().getSimpleName() : "Unknown";
        logError("Error in " + objName + " while " + context +
            " (error count: " + tracker.errorCount + "): " + e.getMessage());

        if (tracker.isProblematic()) {
            markAsCorrupted(obj);
            logError("CIRCUIT BREAKER OPENED for " + objName +
                " - object will be removed");
        }
    }

    /**
     * Handles an error from a collision pair.
     * Records errors for both objects.
     */
    private void handleCollisionPairError(Collidable a, Collidable b, Exception e) {
        String nameA = a != null ? a.getClass().getSimpleName() : "null";
        String nameB = b != null ? b.getClass().getSimpleName() : "null";

        logError("Collision pair error [" + nameA + " <-> " + nameB + "]: " + e.getMessage());

        if (a != null) {
            handleObjectError(a, e, "collision pair processing");
        }
        if (b != null) {
            handleObjectError(b, e, "collision pair processing");
        }
    }

    /**
     * Marks an object as corrupted for removal at end of frame.
     */
    private void markAsCorrupted(Collidable obj) {
        if (obj == null) return;

        if (!corruptedObjects.contains(obj, true)) {
            corruptedObjects.add(obj);
            String objName = obj.getClass().getSimpleName();
            logWarning("Marked for removal: " + objName);
        }
    }

    /**
     * Removes all corrupted objects from the system.
     * Called at the end of each collision check frame.
     */
    private void removeCorruptedObjects() {
        if (corruptedObjects.size == 0) return;

        logWarning("Removing " + corruptedObjects.size + " corrupted object(s)");

        for (Collidable corrupted : corruptedObjects) {
            collidables.removeValue(corrupted, true);
            errorTrackers.remove(corrupted);

            String objName = corrupted != null ? corrupted.getClass().getSimpleName() : "Unknown";
            logError("REMOVED corrupted collidable: " + objName);
        }

        logInfo("Collidables remaining: " + collidables.size);
    }

    // ============================================================================
    // LOGGING
    // ============================================================================

    private void logInfo(String message) {
        if (enableLogging) {
            System.out.println("[COLLISION INFO] " + message);
        }
    }

    private void logWarning(String message) {
        if (enableLogging) {
            System.out.println("[COLLISION WARNING] " + message);
        }
    }

    private void logError(String message) {
        if (enableLogging) {
            System.err.println("[COLLISION ERROR] " + message);
        }
    }

    // ============================================================================
    // CONFIGURATION & UTILITIES
    // ============================================================================

    /**
     * Enable/disable logging output.
     */
    public void setLoggingEnabled(boolean enabled) {
        this.enableLogging = enabled;
    }

    /**
     * Enable/disable metrics collection.
     */
    public void setMetricsEnabled(boolean enabled) {
        this.enableMetrics = enabled;
    }

    /**
     * Prints performance metrics summary.
     */
    public void printMetrics() {
        if (enableMetrics) {
            metrics.printSummary();
        }
    }

    /**
     * Resets all metrics.
     */
    public void resetMetrics() {
        metrics.totalFrames = 0;
        metrics.totalCollisions = 0;
        metrics.totalErrors = 0;
        metrics.worstFrameTimeNs = 0;
        metrics.totalFrameTimeNs = 0;
        metrics.maxObjectCount = 0;
    }

    /**
     * Gets the current number of registered collidables.
     */
    public int getObjectCount() {
        return collidables.size;
    }

    /**
     * Gets the number of objects currently in circuit breaker mode.
     */
    public int getBlockedObjectCount() {
        int count = 0;
        for (ErrorTracker tracker : errorTrackers.values()) {
            if (tracker.shouldBlock()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Clears all registered objects and resets error tracking.
     * Useful for scene transitions.
     */
    public void clear() {
        collidables.clear();
        corruptedObjects.clear();
        errorTrackers.clear();
        logInfo("Collision system cleared");
    }

    /**
     * Gets diagnostic information about the collision system state.
     */
    public String getDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Collision System Diagnostics ===\n");
        sb.append("Total Objects: ").append(collidables.size).append("\n");
        sb.append("Blocked Objects: ").append(getBlockedObjectCount()).append("\n");
        sb.append("Error Trackers: ").append(errorTrackers.size()).append("\n");

        if (enableMetrics) {
            sb.append("Total Frames: ").append(metrics.totalFrames).append("\n");
            sb.append("Total Errors: ").append(metrics.totalErrors).append("\n");
        }

        return sb.toString();
    }
}
