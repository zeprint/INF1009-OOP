package io.github.mathdash.logic.render;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.mathdash.engine.collision.CollisionManager;
import io.github.mathdash.engine.entity.EntityManager;
import io.github.mathdash.engine.entity.Transform;
import io.github.mathdash.engine.movement.MovementManager;
import io.github.mathdash.logic.collision.CollisionDispatcher;
import io.github.mathdash.logic.entity.AnswerBlock;
import io.github.mathdash.logic.entity.AnswerBlockFactory;
import io.github.mathdash.logic.entity.Obstacle;
import io.github.mathdash.logic.entity.ObstacleFactory;
import io.github.mathdash.logic.entity.Player;
import io.github.mathdash.logic.math.MathQuestion;
import io.github.mathdash.logic.movement.ScrollMovement;


/**
 * EntitySpawner - Handles spawning of obstacles and answer blocks
 * at timed intervals with safe-distance logic.
 *
 * Extracted from GameScene to follow the Single Responsibility Principle.
 */
public class EntitySpawner {

    private final float worldWidth;

    private static final float SPAWN_INTERVAL_BASE = 3.0f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2.0f;
    private static final float BASE_SCROLL_SPEED = 200f;
    private static final float ANSWER_SAFE_DISTANCE = 100f;

    private final ObstacleFactory obstacleFactory;
    private final AnswerBlockFactory answerBlockFactory;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;
    private final CollisionDispatcher collisionDispatcher;

    private final Array<AnswerBlock> activeAnswers;
    private final Array<Obstacle> activeObstacles;

    private float obstacleSpawnTimer = 0f;
    private float answerSpawnTimer = 0f;
    private boolean answersOnScreen = false;
    
    public EntitySpawner(float worldWidth,ObstacleFactory obstacleFactory, AnswerBlockFactory answerBlockFactory,
                         EntityManager entityManager, CollisionManager collisionManager,
                         MovementManager movementManager, CollisionDispatcher collisionDispatcher,
                         Array<AnswerBlock> activeAnswers, Array<Obstacle> activeObstacles) {
        this.worldWidth = worldWidth;
        this.obstacleFactory = obstacleFactory;
        this.answerBlockFactory = answerBlockFactory;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.movementManager = movementManager;
        this.collisionDispatcher = collisionDispatcher;
        this.activeAnswers = activeAnswers;
        this.activeObstacles = activeObstacles;
    }

    public void update(float deltaTime, float scrollSpeed, MathQuestion currentQuestion) {
        // Spawn obstacles
        obstacleSpawnTimer += deltaTime;
        float obstacleInterval = OBSTACLE_SPAWN_INTERVAL * (BASE_SCROLL_SPEED / scrollSpeed);
        if (obstacleSpawnTimer >= obstacleInterval) {
            obstacleSpawnTimer = 0f;
            spawnObstacle(scrollSpeed);
        }

        // Spawn answer blocks
        if (!answersOnScreen) {
            answerSpawnTimer += deltaTime;
            float answerInterval = SPAWN_INTERVAL_BASE * (BASE_SCROLL_SPEED / scrollSpeed);
            if (answerSpawnTimer >= answerInterval) {
                spawnAnswerBlocks(currentQuestion, scrollSpeed);
                answersOnScreen = true;
            }
        }
    }

    private void spawnObstacle(float scrollSpeed) {
        float spawnX = this.worldWidth + 50f;

        for (int i = 0; i < activeAnswers.size; i++) {
            AnswerBlock block = activeAnswers.get(i);
            if (!block.isActive()) continue;
            Transform bt = block.getComponent(Transform.class);
            if (bt != null && Math.abs(bt.getX() - spawnX) < ANSWER_SAFE_DISTANCE) {
                return;
            }
        }

        if (!answersOnScreen) {
            float answerInterval = SPAWN_INTERVAL_BASE * (BASE_SCROLL_SPEED / scrollSpeed);
            float timeUntilAnswers = answerInterval - answerSpawnTimer;
            float safeTime = ANSWER_SAFE_DISTANCE / scrollSpeed;
            if (timeUntilAnswers <= safeTime) {
                return;
            }
        }

        int lane = MathUtils.random(0, 2);
        float y = Player.LANE_Y[lane];

        Obstacle obs = obstacleFactory.create(spawnX, y);
        movementManager.add(obs.getComponent(ScrollMovement.class));
        obs.setCollisionHandler(collisionDispatcher);
        obs.setScrollSpeed(scrollSpeed);
        entityManager.addEntity(obs);
        collisionManager.addObject(obs);
        activeObstacles.add(obs);
    }

    private void spawnAnswerBlocks(MathQuestion currentQuestion, float scrollSpeed) {
        if (currentQuestion == null) return;

        float x = this.worldWidth + 50f;

        int[] laneOrder = {0, 1, 2};
        shuffleLanes(laneOrder);

        int[] answers = {
            currentQuestion.getCorrectAnswer(),
            currentQuestion.getWrongAnswer1(),
            currentQuestion.getWrongAnswer2()
        };
        boolean[] isCorrect = {true, false, false};

        for (int i = 0; i < 3; i++) {
            float y = Player.LANE_Y[laneOrder[i]];
            AnswerBlock block = answerBlockFactory.create(x, y, answers[i], isCorrect[i]);
            movementManager.add(block.getComponent(ScrollMovement.class));
            block.setCollisionHandler(collisionDispatcher);
            block.setScrollSpeed(scrollSpeed);
            entityManager.addEntity(block);
            collisionManager.addObject(block);
            activeAnswers.add(block);
        }
    }

    private void shuffleLanes(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = MathUtils.random(0, i);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public void clearAnswerBlocks() {
        for (int i = activeAnswers.size - 1; i >= 0; i--) {
            AnswerBlock block = activeAnswers.get(i);
            block.setActive(false);
            collisionManager.removeObject(block);
            entityManager.removeEntity(block);
            movementManager.remove(block.getComponent(ScrollMovement.class));
        }
        activeAnswers.clear();
        answersOnScreen = false;
    }

    public void resetAnswerTimer() {
        answersOnScreen = false;
        answerSpawnTimer = 0f;
    }

    public boolean isAnswersOnScreen() {
        return answersOnScreen;
    }

    public void setAnswersOnScreen(boolean value) {
        this.answersOnScreen = value;
    }
}
